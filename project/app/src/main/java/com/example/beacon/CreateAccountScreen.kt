package com.example.beacon

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CreateAccountScreen(modifier: Modifier = Modifier, navController: NavController, viewModel: BeaconViewModel) {
    val firstNameState = remember { mutableStateOf("") }
    val lastNameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val confirmEmailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }
    val errorMessageState = remember { mutableStateOf<String?>("") }
    val themeStrategy by viewModel.themeStrategy

    // Check if the emails and passwords match (simple validation)
    val doEmailsMatch = emailState.value == confirmEmailState.value
    val doPasswordsMatch = passwordState.value == confirmPasswordState.value

    var (responseCode, setresponseCode) = remember {
        mutableStateOf(0)
    }
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeStrategy.primaryColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
            fontSize = 32.sp,
            color = themeStrategy.primaryTextColor,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // First Name
        OutlinedTextField(
            textStyle = TextStyle(color = themeStrategy.primaryTextColor),
            value = firstNameState.value,
            onValueChange = { firstNameState.value = it },
            label = { Text("First Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        // Last Name
        OutlinedTextField(
            textStyle = TextStyle(color = themeStrategy.primaryTextColor),
            value = lastNameState.value,
            onValueChange = { lastNameState.value = it },
            label = { Text("Last Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        // Email
        OutlinedTextField(
            textStyle = TextStyle(color = themeStrategy.primaryTextColor),
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        // Confirm Email
        OutlinedTextField(
            textStyle = TextStyle(color = themeStrategy.primaryTextColor),
            value = confirmEmailState.value,
            onValueChange = { confirmEmailState.value = it },
            label = { Text("Confirm Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            isError = !doEmailsMatch && confirmEmailState.value.isNotEmpty()
        )

        // Password
        OutlinedTextField(
            textStyle = TextStyle(color = themeStrategy.primaryTextColor),
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        // Confirm Password
        OutlinedTextField(
            textStyle = TextStyle(color = themeStrategy.primaryTextColor),
            value = confirmPasswordState.value,
            onValueChange = { confirmPasswordState.value = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            isError = !doPasswordsMatch && confirmPasswordState.value.isNotEmpty()
        )

        if (errorMessageState.value != null) {
            Text(
                text = errorMessageState.value ?: "",
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        // Register Button
        Button(
            onClick = {
                if (doEmailsMatch && doPasswordsMatch) {
                    responseCode = viewModel.createAccount(firstNameState.value, lastNameState.value,
                        emailState.value, passwordState.value)
                    if(responseCode == 0){
                        setShowDialog(true)
                    }
                } else {
                    errorMessageState.value = "Please make sure emails and passwords match."
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = themeStrategy.secondaryColor, contentColor = themeStrategy.secondaryTextColor)
        ) {
            Text("CREATE ACCOUNT")
        }

        if (showDialog){
            AlertDialog(
                onDismissRequest = { setShowDialog(false) },
                title = { Text("Account successfully created.",
                    color = themeStrategy.secondaryTextColor) },
                containerColor = themeStrategy.primaryTextColor,
                text = { Text(
                    text = "",
                    color = themeStrategy.secondaryTextColor,
                    fontSize = 16.sp) },
                confirmButton = {
                    Button(
                        onClick = {
                            firstNameState.value = ""
                            lastNameState.value = ""
                            emailState.value = ""
                            confirmEmailState.value = ""
                            passwordState.value = ""
                            confirmPasswordState.value = ""
                            setShowDialog(false)
                            navController.navigate(BeaconScreens.SignIn.name)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = themeStrategy.primaryColor, contentColor = themeStrategy.primaryTextColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text("Sign In Now")
                    }
                }
            )
        }
    }
}
