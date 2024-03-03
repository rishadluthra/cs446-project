package com.example.beacon
import androidx.compose.foundation.background
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
import com.example.beacon.ui.theme.Black
import com.example.beacon.ui.theme.PrimaryYellow
import com.example.beacon.ui.theme.White
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


@OptIn(ExperimentalMaterial3Api::class)
@Composable @Preview
//TODO: Add this parameters to the screen - modifier: Modifier = Modifier, viewModel: BeaconViewModel, navController: NavController
fun DropABeaconScreen() {
    val titleState = remember { mutableStateOf("") }
    val tagsState = remember { mutableStateOf("") }
    val pincodeState = remember { mutableStateOf("") }
    val descriptionState = remember { mutableStateOf("") }

    Scaffold(
        topBar = { CustomAppBar("drop a beacon") },
        containerColor = PrimaryYellow

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            TextFieldWithLabel(
                label = "enter a title...",
                state = titleState,
                modifier = Modifier
                    .fillMaxWidth()
            )
            TextFieldWithLabel(
                label = "choose tags",
                state = tagsState,
                modifier = Modifier.fillMaxWidth()
            )
            TextFieldWithLabel(
                label = "enter the postal code for the beacon",
                state = pincodeState,
                modifier = Modifier.fillMaxWidth()
            )
            TextFieldWithLabel(
                label = "enter description of your beacon",
                state = descriptionState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // This makes the TextField expand
                visualTransformation = VisualTransformation.None
            )
            Button(
                onClick = {
                    val newBeaconJsonObject = buildJsonObject {
                        put("title", titleState.value)
                        //put("tags", tagsState.value)
                        put("postalCode", pincodeState.value)
                        put("description", descriptionState.value)
                    }
                    
                    val newBeaconJsonString = Json.encodeToString(JsonObject.serializer(), newBeaconJsonObject)

                    print(newBeaconJsonString)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Black, contentColor = White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Drop")
            }
        }
    }
}

@Composable
fun TextFieldWithLabel(label: String, state: MutableState<String>, modifier: Modifier = Modifier, visualTransformation: VisualTransformation = VisualTransformation.None) {
    OutlinedTextField(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAppBar(title: String) {
    TopAppBar(
        title = { Text(text = title, color = Color.Black, fontSize = 18.sp) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryYellow,
        ),
        navigationIcon = {
            IconButton(onClick = { /* TODO: Handle back press */ }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        },
    )
}