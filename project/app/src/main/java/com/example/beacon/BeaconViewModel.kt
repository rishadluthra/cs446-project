package com.example.beacon

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread

data class UiState(
    var name: String = "testName", //TODO: Set this to the dummy name from the backend!!!!
    var ourBeacons: List<BeaconInfo> = emptyList(),
    var nearbyBeacons: List<BeaconInfo> = emptyList()
)
@Serializable
data class BeaconInfo(val id: String, val creatorId: String, val title: String, val description: String, val location: Location, val tag: String, val createdAt: String, val updatedAt: String)
@Serializable
data class Location(val type: String, val coordinates: Array<Float>)

class BeaconViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    var themeStrategy: MutableState<ThemeStrategy> = mutableStateOf(LightThemeStrategy)

    fun refreshOurBeacons() {
        thread {
            _uiState.update { currentState ->
                currentState.copy(
                    ourBeacons = fetchOurBeacons().asList(),
                )
            }
        }
    }

    fun refreshNearby(tags: List<String>?, maxDistanceKm: Int) {
        thread {
            _uiState.update { currentState ->
                currentState.copy(
                    nearbyBeacons = fetchNearbyBeacons(tags, maxDistanceKm).asList()
                )
            }
        }
    }

    fun sendBeacon(title: String,
                   tag: String,
                   description: String,
                   postalCode: String,
                   onSuccess: (Int) -> Unit,
                   onError: (Int) -> Unit) {
        viewModelScope.launch {
            // Perform the network operation on a background thread
            val responseCode = withContext(Dispatchers.IO) {
                val newBeaconJsonObject = buildJsonObject {
                    put("title", title)
                    put("tag", tag)
                    put("description", description)
                    put("postalCode", postalCode)
                }
                val newBeaconJsonString =
                    Json.encodeToString(JsonObject.serializer(), newBeaconJsonObject)
                postBeacon(newBeaconJsonString)
            }
            // Now back on the main thread, check the response and call onSuccess or onError
            if (responseCode == 201) {
                onSuccess(responseCode)
            } else {
                onError(responseCode)
            }
        }
    }

    fun delete(id: String,
               onSuccess: (Int) -> Unit,
               onError: (Int) -> Unit) {
        viewModelScope.launch {
            val responseCode = withContext(Dispatchers.IO) {
                deleteBeacon(id)
            }
            if (responseCode == 200) {
                onSuccess(responseCode)
            } else {
                onError(responseCode)
            }
        }
    }

    fun toggleTheme() {
        themeStrategy.value = if (themeStrategy.value == DarkThemeStrategy) {
            LightThemeStrategy
        } else {
            DarkThemeStrategy
        }
        print("current theme: ${themeStrategy.value}")
    }

    fun signIn(email: String, password: String, onSuccess: (String) -> Unit, onError: (Int) -> Unit) {
         // Launch a coroutine in the ViewModelScope
            viewModelScope.launch {
                // Perform the network operation on a background thread
                val (responseCode, authToken) = withContext(Dispatchers.IO) {
                    val signInJsonObject = buildJsonObject {
                        put("email", email)
                        put("password", password)
                    }
                    val signInJsonString = Json.encodeToString(JsonObject.serializer(), signInJsonObject)
                    postSignIn(signInJsonString)
                }
                // Now back on the main thread, check the response and call onSuccess or onError
                if (responseCode == 201 && authToken != "") {
                    AuthManager.setAuthToken(authToken)
                    onSuccess(authToken)
                } else {
                    onError(responseCode)
                }
            }
    }



fun fetchOurBeacons(): Array<BeaconInfo> {
    try {
        val authToken = AuthManager.getAuthToken()
        val request = okhttp3.Request.Builder()
            .url("http://10.0.2.2:4000/beacons/my_beacons")
            .addHeader("Authorization", "Bearer $authToken")
            .build()
        val response = OkHttpClient().newCall(request).execute()
        val json = response.body!!.string()
        return Json.decodeFromString<Array<BeaconInfo>>(json)
    } catch (e: Exception) {
        println(e.message)
    }
    return emptyArray()
}

fun fetchNearbyBeacons(tags: List<String>?, maxDistanceKm: Int): Array<BeaconInfo> {
    val maxDistance = maxDistanceKm * 1000
    val baseUrl = "http://10.0.2.2:4000/beacons?latitude=43.475807&longitude=-80.542007"
    val distUrl = "$baseUrl&maxDistance=$maxDistance"

    val url: String = if (!tags.isNullOrEmpty()) {
        val tagsQueryString = tags.joinToString("&") { "tags[]=$it" }
        "$distUrl&$tagsQueryString"
    } else {
        val allTags = "&tags[]=labour&tags[]=tools&tags[]=tech&tags[]=social"
        "$distUrl$allTags"
    }
    try {
        val authToken = AuthManager.getAuthToken()
        val request = okhttp3.Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $authToken")
            .build()
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
        val authToken = AuthManager.getAuthToken()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/beacons/$id")
            .addHeader("Authorization", "Bearer $authToken")
            .delete()
            .build()
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
        val authToken = AuthManager.getAuthToken()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/beacons")
            .addHeader("Authorization", "Bearer $authToken")
            .post(requestBody)
            .build()
        val response = client.newCall(request).execute()
        return response.code
    } catch (e: Exception) {
        println(e.message)
    }
    return 400
}

suspend fun postSignIn(signInJsonString: String): Pair<Int, String> {
    try {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = signInJsonString.toRequestBody(mediaType)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/auth/login")
            .post(requestBody)
            .build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val jsonObject = JSONObject(responseBody.toString())
                val authToken = jsonObject.optString("access_token", "")
                return Pair(response.code, authToken)
            }
        }
    } catch (e: Exception) {
            println(e.message)
    }
        return Pair(400, "") // Indicate a client error in case of exception
    }
}

