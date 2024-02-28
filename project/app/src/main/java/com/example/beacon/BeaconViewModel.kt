package com.example.beacon

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.concurrent.thread

data class UiState(
   var name: String? = null,
    var beacons: List<BeaconInfo> = emptyList()
//fill in needed parameters here
)

data class BeaconInfo(val id: String, val name: String, val title: String, val description: String)

fun sanitizeBeacons(data: String): MutableList<BeaconInfo> {
    var beaconInfo = mutableListOf<BeaconInfo>()
    val lines = data.split("\n")
    for (line in lines) {
        val idStart = line.indexOf("id") + 6
        if (idStart > 0) {
            val idEnd = line.indexOf("\"", idStart)
            val lineId = line.substring(idStart, idEnd)
            val nameStart = line.indexOf("name") + 8
            val nameEnd = line.indexOf("\"", nameStart)
            val lineName = line.substring(nameStart, nameEnd)
            val titleStart = line.indexOf("title") + 9
            val titleEnd = line.indexOf("\"", titleStart)
            val lineTitle = line.substring(titleStart, titleEnd)
            val descStart = line.indexOf("description") + 15
            val descEnd = line.indexOf("\"", descStart)
            val lineDesc = line.substring(descStart, descEnd)
            val lineInfo = BeaconInfo(lineId, lineName, lineTitle, lineDesc)
            beaconInfo.add(lineInfo)
        }
    }
    return beaconInfo
}

class BeaconViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    val testData = "[\n" +
            "{\"id\": \"5\", \"title\": \"This is a beacon\", \"description\": \"help me please\", \"location\": \"something\"},\n" +
            "{\"id\": \"6\", \"title\": \"This is another beacon\", \"description\": \"I too need help\",  \"location\": \"something else\"}\n" +
            "] \n"
    init {
        thread {
            _uiState.update {
                currentState -> currentState.copy(beacons = sanitizeBeacons(testData))
                //initialize parameters here


            }
        }
    }
}