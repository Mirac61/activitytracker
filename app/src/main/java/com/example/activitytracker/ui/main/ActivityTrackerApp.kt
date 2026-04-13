package com.example.activitytracker.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.activitytracker.ui.components.MainNavBar
import com.example.activitytracker.ui.screens.home.HomeScreen
import com.example.activitytracker.ui.screens.friends.FriendsScreen
import com.example.activitytracker.ui.screens.tracking.TrackingScreen

@Composable
fun ActivityTrackerApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MainNavBar(
                currentDestination = currentDestination,
                onNavigate = { selectedDestination ->
                    currentDestination = selectedDestination
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (currentDestination) {
                AppDestinations.HOME -> HomeScreen()
                AppDestinations.FRIENDS -> FriendsScreen()
                AppDestinations.TRACKING -> TrackingScreen()
            }
        }
    }
}