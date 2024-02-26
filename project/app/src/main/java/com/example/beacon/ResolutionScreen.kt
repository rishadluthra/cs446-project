package com.example.beacon

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star

@Composable
fun ResolutionScreen(modifier: Modifier, beacon: BeaconInfo) {
    var user by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    Column {
        TextField(value = user, onValueChange = { user = it }, label = {Text("who helped you? (leave blank if nobody)")})
        Row() {
            repeat(5) {
                starNum ->
                    val starMod = Modifier.clickable { rating = starNum + 1 }
                    if (starNum < rating) FilledStar(modifier = starMod) else EmptyStar(modifier = starMod)
            }
        }
        TextField(value = review, onValueChange = { review = it }, label = {Text("leave a review")})
        Button(onClick = {
            viewModel.resolveBeacon(beacon)
            navController.navigate(BeaconScreens.Dashboard.name)
        }){
            Text(text = "confirm resolution")
        }
    }
}

//TODO: Figure out how to differentiate these stars
@Composable
fun FilledStar(modifier: Modifier) {
    RoundedPolygon.star(numVerticesPerRadius = 5)
}

@Composable
fun EmptyStar(modifier: Modifier) {
    RoundedPolygon.star(numVerticesPerRadius = 5)
}