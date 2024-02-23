package com.example.beacon

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.concurrent.thread

data class UiState(
   var name: String? = null
//fill in needed parameters here
)

class BeaconViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    init {
        thread {
            _uiState.update {
                currentState -> currentState
                //initialize parameters here
            }
        }
    }
}