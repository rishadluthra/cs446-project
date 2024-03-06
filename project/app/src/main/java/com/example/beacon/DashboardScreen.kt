package com.example.beacon

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.beacon.ui.theme.Black
import com.example.beacon.ui.theme.PrimaryYellow

//@Composable
//fun DashboardScreen(modifier: Modifier = Modifier, viewModel: BeaconViewModel) {
//    LaunchedEffect(true) {
//        viewModel.refresh()
//    }
//    val uiState by viewModel.uiState.collectAsState()
//    LazyColumn(modifier = Modifier.height(200.dp), verticalArrangement = Arrangement.SpaceBetween) {
//       item {
//           Row(horizontalArrangement = Arrangement.Center) {
//               Text(text = "Dashboard")
//           }
//       }
////        items(uiState.ourBeacons.size) { i ->
////            Surface {
////                Column {
////                    Text(text = uiState.ourBeacons[i].title)
////                    Column {
////                        //tags go here
////                    }
////                    Text(text = uiState.ourBeacons[i].description)
////                    Button(onClick = {
//////                            navController.navigate(BeaconScreens.Resolution.name)
////                    }) {
////                        Text(text = "resolve")
////                    }
////                }
////            }
////        } // this will hold the user's active beacons, if any
//        items(uiState.ourBeacons.size) { i ->
//            Surface(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                shape = RoundedCornerShape(20.dp), // Adjust the corner radius to match your design
//                color = Color(0xFFFFEB46) // This color should be the yellow background color you want
//            ) {
//                Row(
//                    modifier = Modifier
//                        .padding(all = 16.dp)
//                        .fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(end = 16.dp)
//                    ) {
//                        Text(
//                            text = uiState.ourBeacons[i].title,
//                            style = MaterialTheme.typography.bodyLarge
//                        )
//                        Text(
//                            text = uiState.ourBeacons[i].description,
//                            style = MaterialTheme.typography.bodyLarge,
//                            modifier = Modifier.padding(top = 4.dp)
//                        )
//                    }
//                    Button(
//                        onClick = { /* TODO: Insert navigate action here */ },
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.Transparent,
//                            contentColor = Color.Black
//                        )
//                    ) {
//                        Text(text = "resolve")
//                    }
//                }
//            }
//        }
////        item {
////            if (uiState.ourBeacons.isEmpty()) {
////                Text(text = "you don't have any active beacons")
////            }
////            Surface() {
////                Column {
////                    Text(text = "notifications")
////                    Text(text = "you don't have any notifications")
////                } // this will hold notifications from messages to/from other users, if any
////                Column {
////                    Text(text = "beacons you've messaged")
////                    Text(text = "you haven't replied to any beacons")
////                } // this will hold active beacons to which the user has responded, if any
////            }
////        }
//
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(modifier: Modifier = Modifier, viewModel: BeaconViewModel) {
    LaunchedEffect(true) {
        viewModel.refresh()
    }
    val uiState by viewModel.uiState.collectAsState()
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    // Set the LazyColumn's background to yellow
    Box(modifier = Modifier
        .fillMaxSize()
        .background(PrimaryYellow)
    ) {
        LazyColumn(modifier = Modifier.height(750.dp), verticalArrangement = Arrangement.SpaceBetween) {
            item{
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "Dashboard  ",
                                fontSize = 32.sp,
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.Center) // Center align the text
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryYellow)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                       // .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                       // .background(Color.Black)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "My Active Beacons",
                        color = Black,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }
            items(uiState.ourBeacons.size) { i ->
                // Set each item's Surface to white
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White // Set the item background to white
                ) {
                    Column(
                        modifier = Modifier
                            .padding(all = 16.dp)
                    ) {
                        Text(
                            text = uiState.ourBeacons[i].title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = uiState.ourBeacons[i].description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(
                            onClick = {
                                val code = viewModel.delete(uiState.ourBeacons[i].id)
                                if (code == 0) {
                                    setShowDialog(true)
                                }
                                viewModel.refresh()
                            },
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Black
                            ),
                        ) {
                            Text(text = "resolve")
                        }
                    }
                }
            }
            item {
                if (showDialog){
                    AlertDialog(
                        onDismissRequest = { setShowDialog(false) },
                        title = { Text("Success") },
                        text = { Text(
                            text = "Beacon resolved",
                            fontSize = 16.sp) },
                        confirmButton = {
                            Button(
                                onClick = {
                                    setShowDialog(false)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryYellow, contentColor = Color.Black),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            ) {
                                Text("Close")
                            }
                        }
                    )
                }
            }
        }
    }
}
