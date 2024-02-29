package com.example.beacon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable @Preview
fun DashboardScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(text = "Dashboard")
        }
        Column(){} // this will hold the user's active beacons, if any
        Column(){} // this will hold notifications from messages to/from other users, if any
        Column(){} // this will hold active beacons to which the user has responded, if any
    }
}