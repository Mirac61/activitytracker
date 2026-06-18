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
import com.example.activitytracker.ui.screens.login.LoginScreen
import com.example.activitytracker.ui.screens.splash.SplashWatcher
import com.example.activitytracker.data.local.entity.ActivityEntity
import com.example.activitytracker.ui.screens.tracking.EditActivity
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityTrackerApp(openAddActivityRequestId: Int = 0) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.SPLASH) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedActivity by remember { mutableStateOf<ActivityEntity?>(null) }
    var showEditBottomSheet by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(openAddActivityRequestId) {
        if (openAddActivityRequestId > 0) {
            showBottomSheet = true
        }
    }

    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val application = LocalContext.current.applicationContext as ActivityApplication

    val trackingViewModel: TrackingViewModel = viewModel(
        factory = ActivityEntryModelFactory(
            repository = application.repository,
            appContext = application.applicationContext
        )
    )

    // Observe the activity List from Room
    val activities by trackingViewModel.activityEntity.observeAsState(emptyList())
    val streak by trackingViewModel.streak.observeAsState(0)
    val listOfActivityNames by trackingViewModel.listOfActivityNames.observeAsState(emptyList())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentDestination != AppDestinations.LOGIN && currentDestination != AppDestinations.REGISTER && currentDestination != AppDestinations.SPLASH) {
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
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (currentDestination) {
                AppDestinations.SPLASH -> SplashWatcher(
                    onNavigateToHome = {
                        currentDestination = AppDestinations.HOME
                    },
                    onNavigateToLogin = {
                        currentDestination = AppDestinations.LOGIN
                    }
                )

                AppDestinations.LOGIN -> LoginScreen(
                    onLoginSuccess = {
                        currentDestination = AppDestinations.HOME
                    },
                    onNavigateToRegister = {
                        currentDestination = AppDestinations.REGISTER
                    }
                )
                AppDestinations.HOME -> HomeScreen(
                    activities = activities,
                    streak = streak,
                    onDaySelected = { selectedDay = it },
                    onSettingsClick = {},
                    onActivityClick = { activity ->
                        selectedActivity = activity
                        showEditBottomSheet = true
                    }
                )
                AppDestinations.FRIENDS -> FriendsScreen()
                AppDestinations.TRACKING -> {}

                AppDestinations.REGISTER -> RegistrationScreen(
                    onRegistrationComplete = {
                        currentDestination = AppDestinations.LOGIN
                    },
                    onNavigateToLogin = {
                        currentDestination = AppDestinations.LOGIN
                    }
                )
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { },
            sheetState = addSheetState
        ) {
            AddActivity(
                onDismiss = { showBottomSheet = false},
                onSave = { activityName, activityDate ->
                    trackingViewModel.saveActivity(activityName, activityDate)
                    showBottomSheet = false
                },
                activityNames = listOfActivityNames,
                selectedDate = selectedDay
            )
        }
    }

    if (showEditBottomSheet && selectedActivity != null) {
        ModalBottomSheet(
            onDismissRequest = {
            },
            sheetState = editSheetState
        ) {
            EditActivity(
                activity = selectedActivity!!,
                onDismiss = {
                    selectedActivity = null
                },
                onSave = { updatedActivity ->
                    trackingViewModel.updateActivity(updatedActivity)
                    selectedActivity = null
                }
            )
        }
    }
}