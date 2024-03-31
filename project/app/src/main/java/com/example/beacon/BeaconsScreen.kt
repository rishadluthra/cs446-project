package com.example.beacon

import android.text.Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beacon.ui.theme.Black
import com.example.beacon.ui.theme.PrimaryYellow
import com.example.beacon.ui.theme.White
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.SideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeaconsScreen(modifier: Modifier = Modifier, viewModel: BeaconViewModel) {
    val themeStrategy by viewModel.themeStrategy
    LaunchedEffect(true) {
        viewModel.refresh()
    }
    val uiState by viewModel.uiState.collectAsState()
    val tagsState = remember { mutableStateListOf<String>() }
    val tags = listOf("labour", "tools", "tech", "social")

    SideEffect {
        println("Selected Tags: ${tagsState.joinToString()}")
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
                MultiTagSelection(
                    selectedTags = tagsState,
                    tags = tags,
                    onTagSelected = { tag, isSelected ->
                        if (isSelected && !tagsState.contains(tag)) {
                            tagsState.add(tag)
                        } else if (!isSelected && tagsState.contains(tag)) {
                            tagsState.remove(tag)
                        }
                    },
                    chooseTagsLabel = "Choose tags to filter on"
                )
                print(tagsState.joinToString())
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

@Composable
fun MultiTagSelection(
    selectedTags: List<String>,
    tags: List<String>,
    onTagSelected: (String, Boolean) -> Unit,
    chooseTagsLabel: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = chooseTagsLabel,
            modifier = Modifier.align(Alignment.Start)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(tags) { tag ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedTags.contains(tag),
                        onCheckedChange = { isChecked ->
                            onTagSelected(tag, isChecked)
                        }
                    )
                    Text(
                        text = tag,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}
