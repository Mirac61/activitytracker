package com.example.activitytracker.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activitytracker.data.ActivityApplication

// White screen after splash screen with our logo
// Checks auth state on app start and redirects to home or login
@Composable
fun SplashWatcher(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val application = LocalContext.current.applicationContext as ActivityApplication

    val viewModel: SplashViewModel = viewModel(
        factory = SplashViewModelFactory(application.authStorage)
    )

    LaunchedEffect(viewModel.navigationState) {
        when (viewModel.navigationState) {
            is SplashViewModel.NavigationState.Authenticated -> onNavigateToHome()
            is SplashViewModel.NavigationState.Unauthenticated -> onNavigateToLogin()
            is SplashViewModel.NavigationState.Loading -> { }
        }
    }

    Box(modifier = Modifier.fillMaxSize())
}