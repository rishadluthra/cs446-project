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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(modifier: Modifier = Modifier, viewModel: BeaconViewModel) {
    LaunchedEffect(true) {
        viewModel.refresh()
    }
    val themeStrategy by viewModel.themeStrategy
    val uiState by viewModel.uiState.collectAsState()
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(themeStrategy.primaryColor)
    ) {
        LazyColumn(modifier = Modifier.height(750.dp), verticalArrangement = Arrangement.Top) {
            item{
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "Dashboard  ",
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "My Active Beacons",
                        color = themeStrategy.primaryTextColor,
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
                    color = themeStrategy.secondaryColor
                ) {
                    Column(
                        modifier = Modifier
                            .padding(all = 16.dp)
                    ) {
                        Text(
                            text = uiState.ourBeacons[i].title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp),
                            color = themeStrategy.secondaryTextColor
                        )
                        Text(
                            text = uiState.ourBeacons[i].description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = themeStrategy.secondaryTextColor

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
                                containerColor = themeStrategy.primaryColor
                            ),
                        ) {
                            Text(text = "resolve",
                                color = themeStrategy.primaryTextColor)
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
                                colors = ButtonDefaults.buttonColors(containerColor = themeStrategy.primaryColor, contentColor = themeStrategy.secondaryColor),
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
