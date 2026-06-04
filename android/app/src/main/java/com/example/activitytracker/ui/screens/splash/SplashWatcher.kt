package com.example.activitytracker.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activitytracker.data.ActivityApplication
import com.example.activitytracker.ui.screens.login.LoginViewModel
import com.example.activitytracker.ui.screens.login.LoginViewModelFactory

@Composable
fun SplashWatcher(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val application = LocalContext.current.applicationContext as ActivityApplication
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(application.authStorage, application)
    )

    LaunchedEffect(viewModel.isChecking) {
        if (!viewModel.isChecking) {
            if (viewModel.loginSuccess) {
                onNavigateToHome()
            } else {
                onNavigateToLogin()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize())
}