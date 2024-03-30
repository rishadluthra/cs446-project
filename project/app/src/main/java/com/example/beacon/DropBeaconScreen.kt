package com.example.beacon
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
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
//TODO: Add this parameters to the screen - modifier: Modifier = Modifier, viewModel: BeaconViewModel, navController: NavController
fun DropBeaconScreen(modifier: Modifier = Modifier, viewModel: BeaconViewModel, navController: NavController) {
    val titleState = remember { mutableStateOf("") }
    val tagsState = remember { mutableStateOf("") }
    val pincodeState = remember { mutableStateOf("") }
    val descriptionState = remember { mutableStateOf("") }
    val themeStrategy by viewModel.themeStrategy

    //var responseCode = 0
    var (responseCode, setresponseCode) = remember {
        mutableStateOf(0)
    }
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
                label = "enter a title...",
                state = titleState,
                modifier = Modifier
                    .fillMaxWidth()
            )
            DropdownWithLabel(
                label = "choose tags",
                state = tagsState,
                options = listOf("select a tag", "labour", "tools", "tech", "social"),
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
            Button(
                onClick = {
                    if (tagsState.value != "select a tag") {
                        responseCode = viewModel.sendBeacon(titleState.value, tagsState.value, descriptionState.value, pincodeState.value)
                        if(responseCode == 0){
                            setShowDialog(true)
                        }
                    }
                },

                colors = ButtonDefaults.buttonColors(containerColor = themeStrategy.primaryTextColor, contentColor = themeStrategy.secondaryColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Drop",
                        color = themeStrategy.primaryColor)
            }
            if (showDialog){
                AlertDialog(
                    onDismissRequest = { setShowDialog(false) },
                    title = { Text("Success",
                        color = themeStrategy.secondaryTextColor) },
                    containerColor = themeStrategy.primaryTextColor,
                    text = { Text(
                        text = "Beacon dropped",
                        color = themeStrategy.secondaryTextColor,
                        fontSize = 16.sp) },
                    dismissButton = {
                        Button(
                            onClick = {
                                titleState.value = ""
                                tagsState.value = ""
                                pincodeState.value = ""
                                descriptionState.value = ""
                                setShowDialog(false)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = themeStrategy.primaryColor, contentColor = themeStrategy.primaryTextColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 0.dp)
                        ) {
                            Text("Close")
                        }
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
                            colors = ButtonDefaults.buttonColors(containerColor = themeStrategy.primaryColor, contentColor = themeStrategy.primaryTextColor),
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

@Composable
fun TextFieldWithLabel(viewModel: BeaconViewModel, label: String, state: MutableState<String>, modifier: Modifier = Modifier, visualTransformation: VisualTransformation = VisualTransformation.None) {
    val themeStrategy by viewModel.themeStrategy
    OutlinedTextField(
        textStyle = TextStyle(color = themeStrategy.primaryTextColor),
        value = state.value,
        onValueChange = { state.value = it },
        label = { Text(label) },
        singleLine = label != "enter description of your beacon",
        modifier = modifier
            .padding(vertical = 8.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = if (label == "enter description of your beacon") ImeAction.Default else ImeAction.Next),
        visualTransformation = visualTransformation
    )
}

@Composable
fun DropdownWithLabel(label: String, state: MutableState<String>, modifier: Modifier = Modifier, options: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }

    Column(modifier.padding(vertical = 8.dp)) {
        Text(label)
        Box {
            Text(
                text = options[selectedIndex],
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { expanded = true })
                    .padding(16.dp)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(onClick = {
                        selectedIndex = index
                        state.value = option
                        expanded = false
                    }, text = {Text(option)})
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAppBar(title: String, navController: NavController, viewModel: BeaconViewModel) {
    val themeStrategy by viewModel.themeStrategy
    TopAppBar(
        title = { Text(text = title, color = themeStrategy.primaryTextColor, fontSize = 18.sp) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = themeStrategy.primaryColor
        ),
        navigationIcon = {
            IconButton(onClick = { navController.navigate(BeaconScreens.Dashboard.name) }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = themeStrategy.primaryTextColor
                )
            }
        },
    )
}

