package com.example.beacon

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import kotlin.concurrent.thread

data class UiState(
   var name: String? = null,
    var beacons: List<BeaconInfo> = emptyList()
//fill in needed parameters here
)

data class BeaconInfo(val id: String, val name: String, val title: String, val description: String, val location: Location)

data class Location(val latitude: Float, val longitude: Float)

class BeaconViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    init {
        thread {
            _uiState.update {
                currentState -> currentState.copy(beacons = fetchBeacons().asList())
                //initialize parameters here


            }
        }
    }
}

fun fetchBeacons(): Array<BeaconInfo> {
    val request = okhttp3.Request.Builder().url("http://localhost:4000/beacons").build()
    val response = OkHttpClient().newCall(request).execute()

    val json = response.body!!.string()
    val responseBody = Json.decodeFromString<Array<BeaconInfo>>(json)
    return responseBody
}