package com.example.beacon

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeaconsScreen(modifier: Modifier = Modifier, viewModel: BeaconViewModel) {
    val themeStrategy by viewModel.themeStrategy
    val uiState by viewModel.uiState.collectAsState()
    val tagsState = remember { mutableStateListOf<String>() }
    val tags = listOf("labour", "tools", "tech", "social")
    var selectedTags by remember { mutableStateOf(listOf<String>()) }
    var sliderValue by remember { mutableStateOf(1000) }
    var maxDistance by remember { mutableStateOf(1000) }

    LaunchedEffect(true) {
        viewModel.refreshNearby(tagsState, maxDistance)
    }
    LaunchedEffect(selectedTags) {
        Log.d("SelectedTags", selectedTags.joinToString(", "))
        viewModel.refreshNearby(selectedTags, maxDistance)
    }

    LaunchedEffect(maxDistance) {
        Log.d("SelectedTags", selectedTags.joinToString(", "))
        viewModel.refreshNearby(selectedTags, maxDistance)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeStrategy.primaryColor)
    ) {
        LazyColumn(
            modifier = Modifier.height(750.dp)
            , verticalArrangement = Arrangement.Top
        ) {
            item {
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "Beacons Nearby",
                                fontSize = 32.sp,
                                color = themeStrategy.primaryTextColor,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = themeStrategy.primaryColor)
                )
            }
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = themeStrategy.secondaryColor
                ) {
                    Column() {
                        Text("Choose tags to filter",modifier = Modifier.padding(8.dp), color = themeStrategy.secondaryTextColor)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            tags.forEach { tag ->
                                var isChecked by remember { mutableStateOf(false) }
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { isSelected ->
                                        isChecked = isSelected
                                        selectedTags = if (isSelected) {
                                            selectedTags + tag
                                        } else {
                                            selectedTags - tag
                                        }
                                    }
                                )
                                Text(text = tag, modifier = Modifier.align(Alignment.CenterVertically), color = themeStrategy.secondaryTextColor)
                            }
                        }
                        Text("Slide to enter maximum search range",modifier = Modifier.padding(8.dp), color = themeStrategy.secondaryTextColor)
                        Text("Current range: ${sliderValue} Km", modifier = Modifier.padding(8.dp), color = themeStrategy.secondaryTextColor)
                        Slider(
                            value = sliderValue.toFloat(),
                            onValueChange = { sliderValue = it.toInt() },
                            valueRange = 1000f..8000f,
                            onValueChangeFinished = {
                                maxDistance = sliderValue
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = themeStrategy.primaryColor,
                                activeTrackColor = themeStrategy.primaryColor
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

            }
            items(uiState.nearbyBeacons.size) { i ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = themeStrategy.secondaryColor
                ) {
                    Column(
                        modifier = Modifier
                            .padding(all = 16.dp)
                    ) {
                        Text(
                            text = uiState.nearbyBeacons[i].title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp),
                            color = themeStrategy.secondaryTextColor
                        )
                        Text(
                            text = uiState.nearbyBeacons[i].description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = themeStrategy.secondaryTextColor
                        )
                        Button(
                            onClick = { /* TODO: Insert navigate action here */ },
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeStrategy.primaryColor
                            ),
                        ) {
                            Text(text = "Contact",
                                color = themeStrategy.primaryTextColor)
                        }
                    }
                }
            }
        }
    }
}


