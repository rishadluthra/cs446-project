package com.example.beacon


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

enum class BeaconScreens(val title: String) {
    //define your screens in here as an enum
    Dashboard(title = "Dashboard"),
    Beacons(title = "Beacons"),
    SignIn(title = "Sign In"),
    DropBeacon(title = "Drop Beacon"),
    UpdateBeacon(title = "Update Beacon"),
    CreateAccount(title = "Create Account")
}


@Composable
fun BeaconApp(navController: NavHostController = rememberNavController(),
              drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
) {

    val viewModel: BeaconViewModel = viewModel()
    val themeStrategy by viewModel.themeStrategy
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = BeaconScreens.valueOf(
        backStackEntry?.destination?.route ?: BeaconScreens.Dashboard.name
    )
    val coroutineScope = rememberCoroutineScope()
    val sidebarButtons = listOf(BeaconScreens.Dashboard, BeaconScreens.Beacons, BeaconScreens.DropBeacon)
    val drawerEnabled = currentScreen !in listOf(BeaconScreens.SignIn, BeaconScreens.CreateAccount)
    LaunchedEffect(currentScreen) {
        if (!drawerEnabled) {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerEnabled,
        drawerContent = {
            ModalDrawerSheet {
                Surface(
                    color = themeStrategy.secondaryColor,
                    modifier = Modifier
                        .fillMaxHeight()
                        .requiredWidth(250.dp)
                ){
                    Column(

                    ) {
                        Spacer(modifier = Modifier.height(40.dp))
                        Image(
                            painter = painterResource(id = R.drawable.beacon_app_logo),
                            contentDescription = "Drawer Header Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(129.dp)
                                .padding(end = 20.dp)
                        )
                        LazyColumn(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            items(sidebarButtons) {
                                    item ->
                                Button(
                                    onClick = {
                                        coroutineScope.launch{drawerState.close()}
                                        if (currentScreen != item) {
                                            navController.navigate(item.name)
                                        }
                                    },
                                    modifier = Modifier.padding(top = 12.dp, start = 40.dp).width(150.dp).align(Alignment.CenterHorizontally),
                                    colors = ButtonDefaults.buttonColors(containerColor = themeStrategy.primaryColor, contentColor = themeStrategy.primaryTextColor)
                                ) {
                                    Text(text = item.title)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(300.dp))
                        Button(
                            onClick = {
                                // Toggle the theme strategy
                                viewModel.toggleTheme()
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 12.dp, end = 20.dp)
                                .width(150.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = themeStrategy.primaryColor, contentColor = themeStrategy.primaryTextColor)
                        ) {
                            Text("Switch Theme")
                        }
                        Button(
                            onClick = {
                                AuthManager.setAuthToken("")
                                navController.navigate(BeaconScreens.SignIn.name)
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 12.dp, end = 20.dp)
                                .width(150.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = themeStrategy.primaryColor, contentColor = themeStrategy.primaryTextColor)
                        ) {
                            Text("Log Out")
                        }
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = BeaconScreens.SignIn.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            //add in each screen and its associated object here
            composable(route = BeaconScreens.Dashboard.name) {
                DashboardScreen(modifier = Modifier.fillMaxHeight(), viewModel = viewModel, navController = navController)
            }
            composable(route = BeaconScreens.Beacons.name) {
                BeaconsScreen(modifier = Modifier.fillMaxHeight(), viewModel = viewModel)
            }
            composable(route = BeaconScreens.SignIn.name) {
                SignInScreen(modifier = Modifier.fillMaxHeight(), navController = navController, viewModel = viewModel)
            }
            composable(route = BeaconScreens.DropBeacon.name) {
                DropBeaconScreen(modifier = Modifier.fillMaxHeight(), viewModel = viewModel, navController = navController)
            }
            composable(route = BeaconScreens.UpdateBeacon.name) {
                UpdateBeaconScreen(modifier = Modifier.fillMaxHeight(), viewModel = viewModel, navController = navController)
            }
            composable(route = BeaconScreens.CreateAccount.name) {
                CreateAccountScreen(modifier = Modifier.fillMaxHeight(), viewModel = viewModel, navController = navController)
            }
            composable(route = BeaconScreens.Reviews.name) {
                ReviewScreen(modifier = Modifier.fillMaxHeight(), viewModel = viewModel, )
            }
        }
    }
}