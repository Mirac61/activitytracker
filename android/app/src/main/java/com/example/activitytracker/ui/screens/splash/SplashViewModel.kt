package com.example.activitytracker.ui.screens.splash

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.activitytracker.data.local.storage.AuthStorage
import com.example.activitytracker.data.remote.dto.RefreshRequest
import com.example.activitytracker.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class SplashViewModel(private val authStorage: AuthStorage) : ViewModel() {

    sealed class NavigationState {
        object Loading : NavigationState()
        object Authenticated : NavigationState()
        object Unauthenticated : NavigationState()
    }

    var navigationState by mutableStateOf<NavigationState>(NavigationState.Loading)
        private set

    private val instance = RetrofitClient.logininstance

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val savedRefreshToken = authStorage.getRefreshToken()

            if (savedRefreshToken.isNullOrBlank()) {
                navigationState = NavigationState.Unauthenticated
                return@launch
            }

            try {
                val response = instance.refreshToken(RefreshRequest(refreshToken = savedRefreshToken))

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        authStorage.saveUserId(loginResponse.userId)
                        authStorage.saveAccessToken(loginResponse.accessToken)
                        authStorage.saveRefreshToken(loginResponse.refreshToken)

                        navigationState = NavigationState.Authenticated
                    } else {
                        navigationState = NavigationState.Unauthenticated
                    }
                } else {
                    authStorage.clearAll()
                    navigationState = NavigationState.Unauthenticated
                }
            } catch (e: Exception) {
                Log.e("SplashViewModel", "Fehler beim Auto-Login", e)
                // Bei Netzwerkfehlern offline trotzdem reinlassen (wenn Tokens da sind) oder zum Login zwingen:
                navigationState = NavigationState.Unauthenticated
            }
        }
    }
}

class SplashViewModelFactory(private val authStorage: AuthStorage) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SplashViewModel(authStorage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}