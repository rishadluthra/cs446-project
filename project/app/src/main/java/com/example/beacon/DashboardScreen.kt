package com.example.beacon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

fun onlyOurBeacons(data: List<BeaconInfo>, name: String?): MutableList<BeaconInfo> {
    val retList : MutableList<BeaconInfo> = mutableListOf()
    for (item in data) {
        if (item.name == name) {
            retList.add(item)
        }
    }
    return retList
}

@Composable
fun DashboardScreen(modifier: Modifier = Modifier, viewModel: BeaconViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    val ourBeacons = onlyOurBeacons(uiState.beacons, uiState.name)
    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(text = "Dashboard")
        }
        LazyColumn{
            items(ourBeacons.size) { i ->
                Surface {
                    Column {
                        Text(text = ourBeacons[i].title)
                        Column {
                            //tags go here
                        }
                        Text(text = ourBeacons[i].description)
                        Button(onClick = {
                            navController.navigate(BeaconScreens.Resolution.name)
                        }) {
                            Text(text = "resolve")
                        }
                    }
                }
            }
        } // this will hold the user's active beacons, if any
        Column {} // this will hold notifications from messages to/from other users, if any
        Column {} // this will hold active beacons to which the user has responded, if any
    }
}