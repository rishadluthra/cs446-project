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
    val confirmVerificationState = remember { mutableStateOf("") }
    val errorMessageState = remember { mutableStateOf<String?>("") }
    val verificationState = remember { mutableStateOf("") }
    val errorVerificationState = remember { mutableStateOf("") }
    val themeStrategy by viewModel.themeStrategy

    // Check if the emails and passwords match (simple validation)
    val pattern = ".*(@uwaterloo.ca)"
    val validEmail = emailState.value.isNotEmpty() && Regex(pattern).matches(emailState.value)
    val validPassword = passwordState.value.isNotEmpty()
    val doEmailsMatch = emailState.value == confirmEmailState.value
    val doPasswordsMatch = passwordState.value == confirmPasswordState.value

    val doesVerificationMatch = verificationState.value == confirmVerificationState.value

    var (responseCode, setresponseCode) = remember {
        mutableStateOf(0)
    }
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val (showVerification, setShowVerification) = remember { mutableStateOf(false) }

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
                if (!(validEmail && validPassword)) {
                    errorMessageState.value = "Please enter valid email or password."
                }
                else if (doEmailsMatch && doPasswordsMatch) {
                    setShowVerification(true)
                    viewModel.sendEmailAndVerify(emailState.value,
                        onSuccess = {
                            confirmVerificationState.value = VerificationManager.getVerificationCode()
                        },
                        onError = {
                            errorMessageState.value = "Failed to send verification email."
                        })
                } else {
                    errorMessageState.value = "Please make sure emails and passwords match."
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = themeStrategy.secondaryColor, contentColor = themeStrategy.secondaryTextColor)
        ) {
            Text("CREATE ACCOUNT")
        }
        // Back to Sign In Button
        Button(
            onClick = {
                navController.navigate(BeaconScreens.SignIn.name)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = themeStrategy.secondaryColor, contentColor = themeStrategy.secondaryTextColor)
        ) {
            Text("BACK TO SIGN IN")
        }

        if (showVerification) {
            AlertDialog(
                onDismissRequest = { setShowVerification(false) },
                title = { Text("Email Verification Code",
                    color = themeStrategy.secondaryTextColor) },
                containerColor = themeStrategy.primaryTextColor,
                text = {
                    Column (
                        verticalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            textStyle = TextStyle(color = themeStrategy.secondaryTextColor),
                            value = verificationState.value,
                            onValueChange = { verificationState.value = it },
                            label = { Text("Enter Verification Code") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                            isError = !doesVerificationMatch && confirmVerificationState.value.isNotEmpty()
                        )
                    }
                       },
                confirmButton = {
                    Column  (
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = errorVerificationState.value,
                            color = Color.Red,
                            )
                        Button(
                            onClick = {
                                viewModel.sendEmailAndVerify(emailState.value,
                                    onSuccess = {},
                                    onError = {
                                        errorMessageState.value = "Failed to send email."
                                    })
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeStrategy.primaryColor,
                                contentColor = themeStrategy.primaryTextColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Text("Resend Code")
                        }
                        Button(
                            onClick = {
                                if (doesVerificationMatch && confirmVerificationState.value.isNotEmpty()) {
                                    errorVerificationState.value = ""
                                    verificationState.value = ""
                                    responseCode = viewModel.createAccount(firstNameState.value, lastNameState.value,
                                        emailState.value, passwordState.value)
                                    if (responseCode == 0) {
                                        setShowVerification(false)
                                        setShowDialog(true)
                                    }
                                }
                                else {
                                    errorVerificationState.value = "Incorrect Verification Code"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeStrategy.primaryColor,
                                contentColor = themeStrategy.primaryTextColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Text("Verify")
                        }
                    }
                }
            )
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
                            navController.navigate(BeaconScreens.Dashboard.name)
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
