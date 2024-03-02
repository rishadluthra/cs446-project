package com.example.beacon

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
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
}

fun fetchOurBeacons(): Array<BeaconInfo> {
    val request = okhttp3.Request.Builder().url("http://localhost:4000/my_beacons").build()
    val response = OkHttpClient().newCall(request).execute()

    val json = response.body!!.string()
    return Json.decodeFromString<Array<BeaconInfo>>(json)
}