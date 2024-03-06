package com.example.beacon

import android.text.Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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

//@Composable
//fun BeaconsScreen(modifier: Modifier = Modifier) {
//    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
//        Row(horizontalArrangement = Arrangement.Center) {
//            Text(text = "Beacons")
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeaconsScreen(modifier: Modifier = Modifier, viewModel: BeaconViewModel) {
    LaunchedEffect(true) {
        viewModel.refresh()
    }
    val uiState by viewModel.uiState.collectAsState()

// Set the LazyColumn's background to yellow
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
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
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.Center) // Center align the text
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryYellow)
                )
            }
            items(uiState.ourBeacons.size) { i ->
                // Set each item's Surface to white
                Box {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = Dp.Hairline, color = Color.Black),
                        shape = RoundedCornerShape(32.dp),
                        color = Color.White // Set the item background to white
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(2f)
                                    .padding(all = 8.dp)
                            ) {
                                Text(
                                    text = uiState.ourBeacons[i].title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = uiState.ourBeacons[i].description,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier

                                        .padding(bottom = 8.dp)
                                )
                            }
                            Button(
                                onClick = { /* TODO: Insert navigate action here */ },
//                            modifier = Modifier.align(Alignment.Center),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryYellow
                                ),
                            ) {
                                Text(
                                    text = "Reach Out",
                                    color = Color.Black
                                )
                            }
                        }
                    }
//                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
                }
            }
        }
    }
}
