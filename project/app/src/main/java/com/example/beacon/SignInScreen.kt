package com.example.beacon
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun SignInScreen(modifier: Modifier = Modifier, navController: NavController, viewModel: BeaconViewModel) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val rememberMeState = remember { mutableStateOf(false) }
    val themeStrategy by viewModel.themeStrategy
    val errorMessageState = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeStrategy.primaryColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "sign In",
            fontSize = 32.sp,
            color = themeStrategy.primaryTextColor,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        // Email input field
        OutlinedTextField(
            textStyle = TextStyle(color = themeStrategy.primaryTextColor),
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        // Password input field
        OutlinedTextField(
            textStyle = TextStyle(color = themeStrategy.primaryTextColor),
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        if (errorMessageState.value != null) {
            Text(
                text = errorMessageState.value ?: "",
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        // Remember me checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Checkbox(
                checked = rememberMeState.value,
                onCheckedChange = { rememberMeState.value = it },
            )
            Text(
                text = "remember me",
                modifier = Modifier.clickable { rememberMeState.value = !rememberMeState.value },
                color = themeStrategy.primaryTextColor
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "forgot password?",
                color = themeStrategy.primaryTextColor
//                modifier = Modifier.clickable { /* TODO: Handle forgot password */ }
            )
        }
        // Sign In button
        Button(
            onClick = {
                viewModel.signIn(emailState.value, passwordState.value,
                    onSuccess = {
                        navController.navigate(BeaconScreens.Dashboard.name)
                    },
                    onError = {
                        errorMessageState.value = "Email or Password is incorrect. Please try again."
                    })
                      },
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = themeStrategy.secondaryColor, contentColor = themeStrategy.secondaryTextColor)
        ) {
            Text("SIGN IN")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("don't have account?",
                color = themeStrategy.primaryTextColor)
            Text(
                text = " create account",
                color = themeStrategy.primaryTextColor,
                modifier = Modifier.clickable { navController.navigate(BeaconScreens.CreateAccount.name) }
            )
        }
    }
}