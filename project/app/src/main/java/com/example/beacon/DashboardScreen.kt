package com.example.beacon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
        Column(){} // this will hold the user's active beacons, if any
        Column(){} // this will hold notifications from messages to/from other users, if any
        Column(){} // this will hold active beacons to which the user has responded, if any
    }
}