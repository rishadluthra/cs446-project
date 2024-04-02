package com.example.beacon
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.material3.icons.Icons
//import androidx.compose.material3.icons.filled.ArrowBack
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.beacon.ui.theme.Black
import com.example.beacon.ui.theme.PrimaryYellow
import com.example.beacon.ui.theme.White
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateBeaconScreen(modifier: Modifier = Modifier, viewModel: BeaconViewModel, navController: NavController) {
    val beacon = BeaconManager.getBeacon()
    val titleState = remember { mutableStateOf(beacon.title) }
    val tagsState = remember { mutableStateOf(beacon.tag) }
    val pincodeState = remember { mutableStateOf("") }
    val descriptionState = remember { mutableStateOf(beacon.description) }
    val errorMessageState = remember { mutableStateOf<String?>(null) }
    val themeStrategy by viewModel.themeStrategy

    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CustomAppBar("drop a beacon", navController, viewModel) },
        containerColor = themeStrategy.primaryColor

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFieldWithLabel(
                viewModel,
                label = "enter a title",
                state = titleState,
                modifier = Modifier
                    .fillMaxWidth()
            )
            TextFieldWithLabel(
                viewModel,
                label = "choose tags",
                state = tagsState,
                modifier = Modifier.fillMaxWidth()
            )
            TextFieldWithLabel(
                viewModel,
                label = "enter the postal code for the beacon",
                state = pincodeState,
                modifier = Modifier.fillMaxWidth()
            )
            TextFieldWithLabel(
                viewModel,
                label = "enter description of your beacon",
                state = descriptionState,
                modifier = Modifier
                    .fillMaxWidth(),
//                    .weight(3f), // This makes the TextField expand
                visualTransformation = VisualTransformation.None
            )
            if (errorMessageState.value != null) {
                Text(
                    text = errorMessageState.value ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Button(
                onClick = {
                    if (titleState.value.isNotEmpty() &&
                        tagsState.value.isNotEmpty() &&
                        pincodeState.value.isNotEmpty() &&
                        descriptionState.value.isNotEmpty()) {
                        viewModel.updateBeacon(
                            titleState.value,
                            tagsState.value,
                            descriptionState.value,
                            pincodeState.value,
                            beacon.id,
                            onSuccess = {
                                setShowDialog(true)
                                errorMessageState.value = ""
                            },
                            onError = {
                                errorMessageState.value = "Could not update beacon. Please make sure all fields are entered correctly, including a valid postal code."
                            }
                        )
                    }
                    else {
                        errorMessageState.value = "Please fill in all fields."
                    }
                },

                colors = ButtonDefaults.buttonColors(
                    containerColor = themeStrategy.primaryTextColor,
                    contentColor = themeStrategy.secondaryColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    "Update",
                    color = themeStrategy.primaryColor
                )
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { setShowDialog(false) },
                    title = {
                        Text(
                            "Success",
                            color = themeStrategy.secondaryTextColor
                        )
                    },
                    containerColor = themeStrategy.primaryTextColor,
                    text = {
                        Text(
                            text = "Beacon updated",
                            color = themeStrategy.secondaryTextColor,
                            fontSize = 16.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                setShowDialog(false)
                                titleState.value = ""
                                tagsState.value = ""
                                pincodeState.value = ""
                                descriptionState.value = ""
                                navController.navigate(BeaconScreens.Dashboard.name)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeStrategy.primaryColor,
                                contentColor = themeStrategy.primaryTextColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Text("Go to Dashboard")
                        }
                    }
                )
            }
        }
    }
}

