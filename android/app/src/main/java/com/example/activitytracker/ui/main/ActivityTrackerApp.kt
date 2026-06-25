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
import com.example.activitytracker.ui.screens.friends.FriendScreen
import com.example.activitytracker.ui.screens.friends.FriendViewModel
import com.example.activitytracker.ui.screens.friends.FriendViewModelFactory
import com.example.activitytracker.ui.screens.tracking.AddActivity
import com.example.activitytracker.ui.screens.tracking.TrackingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activitytracker.ui.screens.tracking.ActivityEntryModelFactory
import com.example.activitytracker.ui.screens.register.RegistrationScreen
import com.example.activitytracker.ui.screens.login.LoginScreen
import com.example.activitytracker.ui.screens.splash.SplashWatcher
import com.example.activitytracker.ui.screens.settings.SettingsScreen
import com.example.activitytracker.ui.screens.settings.SettingsViewModel
import com.example.activitytracker.ui.screens.settings.SettingsViewModelFactory
import com.example.activitytracker.data.local.entity.ActivityEntity
import com.example.activitytracker.ui.screens.statistics.StatisticsScreen
import com.example.activitytracker.ui.screens.statistics.StatisticsViewModel
import com.example.activitytracker.ui.screens.statistics.StatisticsViewModelFactory
import com.example.activitytracker.data.repository.FriendRepository
import com.example.activitytracker.ui.screens.tracking.EditActivity
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.compose.runtime.collectAsState
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityTrackerApp(openAddActivityRequestId: Int = 0) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.SPLASH) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedActivity by remember { mutableStateOf<ActivityEntity?>(null) }
    var showEditBottomSheet by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }

    val application = LocalContext.current.applicationContext as ActivityApplication
    var userId by remember { mutableStateOf<UUID?>(null) }
    var tokenReady by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val stored = application.authStorage.getUserId()
        android.util.Log.d("AppDebug", "userId geladen: $stored")
        if (stored != null) {
            userId = UUID.fromString(stored)
        }
    }

    LaunchedEffect(openAddActivityRequestId) {
        if (openAddActivityRequestId > 0) {
            showBottomSheet = true
        }
    }

    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val trackingViewModel: TrackingViewModel = viewModel(
        factory = ActivityEntryModelFactory(
            repository = application.repository,
            appContext = application.applicationContext
        )
    )

    val friendViewModel: FriendViewModel? = if (userId != null) {
        viewModel(
            factory = FriendViewModelFactory(
                repository = FriendRepository(),
                userId = userId!!
            )
        )
    } else null

    val ownFriendCode by friendViewModel?.ownFriendCode?.observeAsState("") ?: remember { mutableStateOf("") }
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            application = application,
            repository = application.reminderRepository
        )
    )

    val statisticsViewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory(repository = application.repository)
    )

    // Observe the activity List from Room
    val activities by trackingViewModel.activityEntity.observeAsState(emptyList())
    val streak by trackingViewModel.streak.observeAsState(0)
    val longestStreak by statisticsViewModel.longestStreak.observeAsState(0)
    val averagePerDay by statisticsViewModel.averagePerDay.observeAsState(0.0)
    val mostActiveWeekday by statisticsViewModel.mostActiveWeekday.observeAsState(null)
    val sumByWeekday by statisticsViewModel.sumByWeekday.observeAsState(emptyMap())
    val averageByWeekday by statisticsViewModel.averageByWeekday.observeAsState(emptyMap())
    val listOfActivityNames by trackingViewModel.listOfActivityNames.observeAsState(emptyList())
    val reminders by settingsViewModel.reminders.collectAsState()
    val isRefreshing by friendViewModel?.isRefreshing?.observeAsState(false) ?: remember { mutableStateOf(false) }
    val availableYears by statisticsViewModel.availableYears.observeAsState(listOf(java.time.YearMonth.now().year))

    val friends by friendViewModel?.friends?.observeAsState(emptyList()) ?: remember { mutableStateOf(emptyList()) }
    val pendingRequests by friendViewModel?.pendingRequests?.observeAsState(emptyList()) ?: remember { mutableStateOf(emptyList()) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentDestination != AppDestinations.LOGIN && currentDestination != AppDestinations.REGISTER && currentDestination != AppDestinations.SPLASH && currentDestination != AppDestinations.SETTINGS) {
                MainNavBar(
                    currentDestination = currentDestination,
                    onNavigate = { currentDestination = it },
                    onPlusClicked = { showBottomSheet = true }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (currentDestination) {
                AppDestinations.SPLASH -> SplashWatcher(
                    onNavigateToHome = { currentDestination = AppDestinations.HOME },
                    onNavigateToLogin = { currentDestination = AppDestinations.LOGIN }
                )
                AppDestinations.LOGIN -> LoginScreen(
                    onLoginSuccess = {
                        scope.launch {
                            kotlinx.coroutines.delay(200)
                            val stored = application.authStorage.getUserId()
                            if (stored != null) {
                                userId = UUID.fromString(stored)
                            }
                            currentDestination = AppDestinations.HOME
                        }
                    },
                    onNavigateToRegister = { currentDestination = AppDestinations.REGISTER }
                )
                AppDestinations.HOME -> HomeScreen(
                    activities = activities,
                    streak = streak,
                    selectedDay = selectedDay,
                    onDaySelected = { selectedDay = it },
                    onSettingsClick = { currentDestination = AppDestinations.SETTINGS },
                    onActivityClick = { activity ->
                        selectedActivity = activity
                        showEditBottomSheet = true
                    }
                )
                AppDestinations.TRACKING -> {}
                AppDestinations.STATISTICS -> StatisticsScreen(
                    longestStreak = longestStreak,
                    averagePerDay = averagePerDay,
                    mostActiveWeekday = mostActiveWeekday,
                    sumByWeekday = sumByWeekday,
                    averageByWeekday = averageByWeekday,
                    availableYears = availableYears,
                    onMonthSelected = { statisticsViewModel.selectMonth(it) }
                )
                AppDestinations.SETTINGS -> SettingsScreen(
                    reminders = reminders,
                    onBack = { currentDestination = AppDestinations.HOME },
                    onAddReminder = { day, hour, minute, title, text, isDaily ->
                        settingsViewModel.addReminder(day, hour, minute, title, text, isDaily)
                    },
                    onDeleteReminder = { settingsViewModel.deleteReminder(it) }
                )

                AppDestinations.FRIENDS -> FriendScreen(
                    friends = friends,
                    pendingRequests = pendingRequests,
                    ownFriendCode = ownFriendCode,
                    isRefreshing = isRefreshing,
                    onRefresh = { friendViewModel?.refresh() },
                    onSendRequest = { friendCode ->
                        friendViewModel?.sendFriendRequest(friendCode)
                    },
                    onAcceptRequest = { requestId ->
                        friendViewModel?.acceptRequest(requestId)
                    },
                    onDeclineRequest = { requestId ->
                        friendViewModel?.declineRequest(requestId)
                    },
                    onRemoveFriend = { friendId ->
                        friendViewModel?.removeFriend(friendId)
                    }
                )

                AppDestinations.REGISTER -> RegistrationScreen(
                    onRegistrationComplete = { currentDestination = AppDestinations.LOGIN },
                    onNavigateToLogin = { currentDestination = AppDestinations.LOGIN }
                )
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = addSheetState
        ) {
            AddActivity(
                onDismiss = { showBottomSheet = false },
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
                showEditBottomSheet = false
                selectedActivity = null
            },
            sheetState = editSheetState
        ) {
            EditActivity(
                activity = selectedActivity!!,
                onDismiss = {
                    showEditBottomSheet = false
                    selectedActivity = null
                },
                onSave = { updatedActivity ->
                    trackingViewModel.updateActivity(updatedActivity)
                    showEditBottomSheet = false
                    selectedActivity = null
                }
            )
        }
    }
}