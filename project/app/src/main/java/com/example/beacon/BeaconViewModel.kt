package com.example.beacon

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread

data class UiState(
    var name: String = "testName", //TODO: Set this to the dummy name from the backend!!!!
    var ourBeacons: List<BeaconInfo> = emptyList(),
    var nearbyBeacons: List<BeaconInfo> = emptyList()
//fill in needed parameters here
)
@Serializable
data class BeaconInfo(val id: String, val creatorId: String, val title: String, val description: String, val location: Location, val createdAt: String, val updatedAt: String)
@Serializable
data class Location(val type: String, val coordinates: Array<Float>)

class BeaconViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun refresh() {
        thread {
            _uiState.update { currentState ->
                 currentState.copy(ourBeacons = fetchOurBeacons().asList(), nearbyBeacons = fetchNearbyBeacons().asList())
            //initialize parameters here
            }
        }
    }

    fun sendBeacon(title: String, description: String, postalCode: String, creatorId: String): Int {
        var responseCode = 0
        thread {
            val newBeaconJsonObject = buildJsonObject {
                put("title", title)
                put("description", description)
                put("postalCode", postalCode)
                put("creatorId", creatorId)
            }
            val newBeaconJsonString =
                Json.encodeToString(JsonObject.serializer(), newBeaconJsonObject)
            responseCode = postBeacon(newBeaconJsonString)
        }
        return responseCode
    }

    fun delete(id: String): Int {
        var response = 0
        thread {
            response = deleteBeacon(id)
        }
        return response
    }
}

fun fetchOurBeacons(): Array<BeaconInfo> {
    try {
        val request = okhttp3.Request.Builder()
            .url("http://10.0.2.2:4000/beacons/my_beacons?creatorId=rishad").build()
        val response = OkHttpClient().newCall(request).execute()
        val json = response.body!!.string()
        return Json.decodeFromString<Array<BeaconInfo>>(json)
    } catch (e: Exception) {
        println(e.message)
    }
    return emptyArray()
}

fun fetchNearbyBeacons(): Array<BeaconInfo> {
    try {
        val request = okhttp3.Request.Builder()
            .url("http://10.0.2.2:4000/beacons?latitude=43.475807&longitude=-80.542007&maxDistance=10000").build()
        val response = OkHttpClient().newCall(request).execute()
        val json = response.body!!.string()
        return Json.decodeFromString<Array<BeaconInfo>>(json)
    } catch (e: Exception) {
        println(e.message)
    }
    return emptyArray()
}

fun deleteBeacon(id: String): Int {
    try {
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/beacons/$id").delete().build()
        val response = OkHttpClient().newCall(request).execute()
        return response.code
    } catch (e: Exception) {
        println(e.message)
    }
    return 400
}

fun postBeacon(newBeaconJsonString: String): Int {
    try {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = newBeaconJsonString.toRequestBody(mediaType)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/beacons")
            .post(requestBody)
            .build()
        val response = client.newCall(request).execute()
        return response.code
    } catch (e: Exception) {
        println(e.message)
    }
    return 400
}
