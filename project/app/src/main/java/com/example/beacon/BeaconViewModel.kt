//package com.example.beacon
//
//import androidx.lifecycle.ViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlin.concurrent.thread
//
//data class UiState(
//   var name: String? = null
////fill in needed parameters here
//)
//
//class BeaconViewModel : ViewModel() {
//    private val _uiState = MutableStateFlow(UiState())
//    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
//    init {
//        thread {
//            _uiState.update {
//                currentState -> currentState
//                //initialize parameters here
//            }
//        }
//    }
//}


package com.example.beacon

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread

data class UiState(
    var name: String = "testName", //TODO: Set this to the dummy name from the backend!!!!
    var ourBeacons: List<BeaconInfo> = emptyList()
//fill in needed parameters here
)
@Serializable
data class BeaconInfo(val id: String, val creatorId: String, val title: String, val description: String, val location: Location, val createdAt: String, val updatedAt: String)
@Serializable
data class Location(val type: String, val coordinates: Array<Float>)

class BeaconViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
//    init {
//        thread {
//            _uiState.update {
//                    currentState -> currentState.copy(ourBeacons = fetchOurBeacons().asList())
////                initialize parameters here
//
//
//            }
//        }
//    }
    fun refresh() {
        thread {
            _uiState.update { currentState ->
                 currentState.copy(ourBeacons = fetchOurBeacons().asList())
            //initialize parameters here
            }
        }
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
