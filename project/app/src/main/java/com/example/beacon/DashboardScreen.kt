package com.example.beacon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun DashboardScreen(modifier: Modifier = Modifier, viewModel: BeaconViewModel, navController: NavController) {
    val ourBeacons = viewModel.getOurBeacons()
    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(text = "Dashboard")
        }
        LazyColumn(){
            items(ourBeacons.entries) {
                item ->
                    Surface(){
                        Column(){
                            Text(text = item.title)
                            Column() {
                                //tags go here
                            }
                            Text(text = item.description)
                            Button(onClick ={
                                navController.navigate(BeaconScreens.Resolution.name)
                            }) {
                                Text(text = "resolve")
                            }
                        }
                    }
            }
        } // this will hold the user's active beacons, if any
        Column(){} // this will hold notifications from messages to/from other users, if any
        Column(){} // this will hold active beacons to which the user has responded, if any
    }
}