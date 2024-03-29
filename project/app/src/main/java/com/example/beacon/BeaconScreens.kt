package com.example.beacon


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
    DropBeacon(title = "Drop Beacon")
}


@Composable
fun BeaconApp(navController: NavHostController = rememberNavController(),
              drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
) {

    val viewModel: BeaconViewModel = viewModel()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = BeaconScreens.valueOf(
        backStackEntry?.destination?.route ?: BeaconScreens.Dashboard.name
    )
    val coroutineScope = rememberCoroutineScope()
    ModalNavigationDrawer(drawerState = drawerState,
        drawerContent = {ModalDrawerSheet{
            Surface(color = MaterialTheme.colorScheme.surface){
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    //TODO: Beacon image goes here
                    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                        items(BeaconScreens.entries) {
                            item ->
                                Button(onClick={
                                    coroutineScope.launch{drawerState.close()}
                                    if (currentScreen != item) {
                                        navController.navigate(item.name)
                                    }
                                    }) {
//                                    Text(text = item.title.lowercase())
                                    Text(text = item.title)
                                }
                        }
                    }
                }
            }
        }}
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
                DashboardScreen(modifier = Modifier.fillMaxHeight(), viewModel = viewModel)
            }
            composable(route = BeaconScreens.Beacons.name) {
                BeaconsScreen(modifier = Modifier.fillMaxHeight(), viewModel = viewModel)
            }
            composable(route = BeaconScreens.SignIn.name) {
                SignInScreen(modifier = Modifier.fillMaxHeight(), navController = navController)
            }
            composable(route = BeaconScreens.DropBeacon.name) {
                DropBeaconScreen(modifier = Modifier.fillMaxHeight(), viewModel = viewModel, navController = navController)
            }
        }
    }
}