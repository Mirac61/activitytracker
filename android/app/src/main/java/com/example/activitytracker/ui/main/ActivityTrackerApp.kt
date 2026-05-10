package com.example.activitytracker.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.activitytracker.data.ActivityApplication
import com.example.activitytracker.ui.components.MainNavBar
import com.example.activitytracker.ui.screens.home.HomeScreen
import com.example.activitytracker.ui.screens.friends.FriendsScreen
import com.example.activitytracker.ui.screens.tracking.AddActivity
import com.example.activitytracker.ui.screens.tracking.TrackingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activitytracker.ui.screens.tracking.ActivityEntryModelFactory
import com.example.activitytracker.ui.screens.register.RegistrationScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityTrackerApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val application = LocalContext.current.applicationContext as ActivityApplication

    val trackingViewModel: TrackingViewModel = viewModel(
        factory = ActivityEntryModelFactory(application.repository)
    )

    // Observe the activity List from Room
    val activities by trackingViewModel.activityEntity.observeAsState(emptyList())
    val streak by trackingViewModel.streak.observeAsState(0)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MainNavBar(
                currentDestination = currentDestination,
                onNavigate = { selectedDestination ->
                    currentDestination = selectedDestination
                },
                onPlusClicked = {
                    showBottomSheet = true
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (currentDestination) {
                AppDestinations.HOME -> HomeScreen(activities = activities, streak = streak, onSettingsClick = {})
                AppDestinations.FRIENDS -> FriendsScreen()
                AppDestinations.TRACKING -> {}

                //placeholder to test Registration screen until full implementation
                AppDestinations.REGISTER -> RegistrationScreen()

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
                onSave = { activityName, activityDate ->
                    trackingViewModel.saveActivity(activityName, activityDate)
                    showBottomSheet = false

                }
            )
        }
    }

}