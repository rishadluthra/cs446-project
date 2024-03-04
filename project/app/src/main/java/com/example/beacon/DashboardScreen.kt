package com.example.beacon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DashboardScreen(modifier: Modifier = Modifier, viewModel: BeaconViewModel) {
    LaunchedEffect(true) {
        viewModel.refresh()
    }
    val uiState by viewModel.uiState.collectAsState()
    LazyColumn(modifier = Modifier.height(200.dp), verticalArrangement = Arrangement.SpaceBetween) {
       item {
           Row(horizontalArrangement = Arrangement.Center) {
               Text(text = "Dashboard")
           }
       }
        items(uiState.ourBeacons.size) { i ->
            Surface {
                Column {
                    Text(text = uiState.ourBeacons[i].title)
                    Column {
                        //tags go here
                    }
                    Text(text = uiState.ourBeacons[i].description)
                    Button(onClick = {
//                            navController.navigate(BeaconScreens.Resolution.name)
                    }) {
                        Text(text = "resolve")
                    }
                }
            }
        } // this will hold the user's active beacons, if any
        item {
            if (uiState.ourBeacons.isEmpty()) {
                Text(text = "you don't have any active beacons")
            }
            Surface() {
                Column {
                    Text(text = "notifications")
                    Text(text = "you don't have any notifications")
                } // this will hold notifications from messages to/from other users, if any
                Column {
                    Text(text = "beacons you've messaged")
                    Text(text = "you haven't replied to any beacons")
                } // this will hold active beacons to which the user has responded, if any
            }
        }
    }
}