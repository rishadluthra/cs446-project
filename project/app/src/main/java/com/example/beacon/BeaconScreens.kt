package com.example.beacon


import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

enum class BeaconScreens(val title: String) {
    //define your screens in here as an enum
    Dashboard(title = "Dashboard")
}

@Composable
fun BeaconApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = BeaconScreens.valueOf(
        backStackEntry?.destination?.route ?: BeaconScreens.Dashboard.name
    )
    //going to need to add a ModalNavigationDrawer in here for our sidebar: https://developer.android.com/jetpack/compose/components/drawer
    NavHost(
        navController = navController,
        startDestination = BeaconScreens.Dashboard.name,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        composable(route = BeaconScreens.Dashboard.name) {
            DashboardScreen(modifier = Modifier.fillMaxHeight())
        }
    }
}