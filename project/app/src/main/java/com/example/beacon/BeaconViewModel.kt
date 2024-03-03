package com.example.beacon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.concurrent.thread

data class UiState(
   var name: String? = "testName", //TODO: Set this to the dummy name from the backend!!!!
    var ourBeacons: List<BeaconInfo> = emptyList()
//fill in needed parameters here
)

data class BeaconInfo(val id: String, val creator_id: String, val title: String, val description: String, val location: Location)

data class Location(val latitude: Float, val longitude: Float)

class BeaconViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    init {
        thread {
            _uiState.update {
                currentState -> currentState.copy(ourBeacons = fetchOurBeacons().asList())
                //initialize parameters here


            }
        }
    }
    fun refresh() {
        _uiState.update {
                currentState -> currentState.copy(ourBeacons = fetchOurBeacons().asList())
            //initialize parameters here


        }
    }

    fun sendBeacon(title: String, description: String, postalCode: String) {

        viewModelScope.launch {
            val latLong = fetchLatLong(postalCode)
            if(latLong != null) {
                val newBeaconJsonObject = buildJsonObject {
                    put("title", title)
                    put("description", description)
                    putJsonObject("location") {
                        put("latitude", latLong.first)
                        put("longitude", latLong.second)
                    }
                }
                val newBeaconJsonString = Json.encodeToString(JsonObject.serializer(), newBeaconJsonObject)
                postBeacon(newBeaconJsonString)
            }
        }
    }
}

fun fetchOurBeacons(): Array<BeaconInfo> {
    val request = okhttp3.Request.Builder().url("http://localhost:4000/my_beacons").build()
    val response = OkHttpClient().newCall(request).execute()

    val json = response.body!!.string()
    return Json.decodeFromString<Array<BeaconInfo>>(json)
}

suspend fun fetchLatLong(postalCode: String): Pair<Double, Double>? = withContext(Dispatchers.IO){
    val client = OkHttpClient()
    val apiKey = "YOUR_API_KEY" // Replace with your actual API key for the geocoding service
    val locationUrl = "https://api.geoapify.com/v1/geocode/search?postcode=$postalCode&country=Canada&apiKey=$apiKey"

    val request = Request.Builder()
        .url(locationUrl)
        .build()

    try {
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        if (responseBody != null && response.isSuccessful) {
            val json = Json { ignoreUnknownKeys = true }
            val jsonObject = json.parseToJsonElement(responseBody).jsonObject
            val features = jsonObject["features"]?.jsonArray
            val properties = features?.firstOrNull()?.jsonObject?.get("properties")?.jsonObject
            val latitude = properties?.get("lat")?.jsonPrimitive?.doubleOrNull
            val longitude = properties?.get("lon")?.jsonPrimitive?.doubleOrNull
            if (latitude != null && longitude != null) {
                return@withContext Pair(latitude, longitude)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return@withContext null
}

suspend fun postBeacon(newBeaconJsonString: String) {
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBody = newBeaconJsonString.toRequestBody(mediaType)
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("http://localhost:4000/beacons")
        .post(requestBody)
        .build()

    val response = OkHttpClient().newCall(request).execute()
}


