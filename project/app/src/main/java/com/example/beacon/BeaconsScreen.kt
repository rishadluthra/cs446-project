package com.example.beacon

import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import kotlin.math.round
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.Manifest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeaconsScreen(modifier: Modifier = Modifier, viewModel: BeaconViewModel) {
    val themeStrategy by viewModel.themeStrategy
    val uiState by viewModel.uiState.collectAsState()
    val tagsState = remember { mutableStateListOf<String>() }
    val tags = listOf("labour", "tools", "tech", "social")
    var selectedTags by remember { mutableStateOf(listOf<String>()) }
    var sliderValue by remember { mutableStateOf(1) }
    var maxDistance by remember { mutableStateOf(1) }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val coroutineScope = rememberCoroutineScope()

    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                coroutineScope.launch {
                    try {
                        val locationResult = fusedLocationClient.lastLocation.await()
                        locationResult?.let {
                            latitude = it.latitude
                            longitude = it.longitude
                        }
                    } catch (e: SecurityException) {
                    }
                }
            }
        }
    )

    LaunchedEffect(key1 = true) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Permission is already granted, directly request location
                coroutineScope.launch {
                    try {
                        val locationResult = fusedLocationClient.lastLocation.await()
                        locationResult?.let {
                            latitude = it.latitude
                            longitude = it.longitude
                            viewModel.refreshNearby(tagsState, maxDistance, latitude, longitude)
                        }
                    } catch (e: SecurityException) {
                        // Handle exception
                    }
                }
            }
            else -> {
                // Request permission
                locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    LaunchedEffect(selectedTags) {
        viewModel.refreshNearby(selectedTags, maxDistance, latitude, longitude)
    }

    LaunchedEffect(maxDistance) {
        viewModel.refreshNearby(selectedTags, maxDistance, latitude, longitude)
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
                                text = "Explore Beacons",
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
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = themeStrategy.secondaryColor
                ) {
                    Column() {
                        Text("Choose tags to filter",modifier = Modifier.padding(8.dp), color = themeStrategy.secondaryTextColor)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            tags.forEach { tag ->
                                var isChecked by remember { mutableStateOf(false) }
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { isSelected ->
                                        isChecked = isSelected
                                        selectedTags = if (isSelected) {
                                            selectedTags + tag
                                        } else {
                                            selectedTags - tag
                                        }
                                    }
                                )
                                Text(text = tag, modifier = Modifier.align(Alignment.CenterVertically), color = themeStrategy.secondaryTextColor)
                            }
                        }
                        Text("Slide to enter maximum search range",modifier = Modifier.padding(8.dp), color = themeStrategy.secondaryTextColor)
                        Text("Current range: ${sliderValue} Km", modifier = Modifier.padding(8.dp), color = themeStrategy.secondaryTextColor)
                        Slider(
                            value = sliderValue.toFloat(),
                            onValueChange = { sliderValue = it.toInt() },
                            valueRange = 1f..100f,
                            onValueChangeFinished = {
                                maxDistance = sliderValue
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = themeStrategy.primaryColor,
                                activeTrackColor = themeStrategy.primaryColor
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

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
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(bottom = 8.dp),
                            color = themeStrategy.secondaryTextColor,
                        )
                        Text(
                            text = uiState.nearbyBeacons[i].description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = themeStrategy.secondaryTextColor
                        )
                        Row(

                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = (round((uiState.nearbyBeacons[i].distance / 1000) * 10.0) / 10.0).toString() + " km away",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = themeStrategy.secondaryTextColor,
                                )
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data =
                                                android.net.Uri.parse("mailto:${uiState.nearbyBeacons[i].creatorEmail}")
                                            putExtra(
                                                Intent.EXTRA_SUBJECT,
                                                "Responding to Beacon: ${uiState.nearbyBeacons[i].title}"
                                            )
                                        }
                                        if (intent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(
                                                Intent.createChooser(
                                                    intent,
                                                    "Send Email..."
                                                )
                                            )
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "No email applications found.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(top = 8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = themeStrategy.primaryColor
                                    ),
                                ) {
                                    Text(
                                        text = "Contact",
                                        color = themeStrategy.primaryTextColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


