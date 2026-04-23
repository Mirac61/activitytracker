package com.example.activitytracker.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.activitytracker.ui.components.MainNavBar
import com.example.activitytracker.ui.screens.home.HomeScreen
import com.example.activitytracker.ui.screens.friends.FriendsScreen
import com.example.activitytracker.ui.screens.tracking.AddActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityTrackerApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MainNavBar(
                currentDestination = currentDestination,
                onNavigate = { selectedDestination ->
                    currentDestination = selectedDestination;

                },
                onPlusClicked = {
                    showBottomSheet = true
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (currentDestination) {
                AppDestinations.HOME -> HomeScreen()
                AppDestinations.FRIENDS -> FriendsScreen()
                AppDestinations.TRACKING -> {}

            }
        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            AddActivity(
                onDismiss = { showBottomSheet = false },
                onSave = {
                    showBottomSheet = false

                }
            )
        }
    }

}