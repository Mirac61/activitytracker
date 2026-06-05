package com.example.activitytracker.ui.screens.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.activitytracker.data.local.storage.AuthStorage
import com.example.activitytracker.data.remote.dto.LoginRequest
import com.example.activitytracker.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel(private val authStorage: AuthStorage, private val appContext: Context) : ViewModel() {

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    fun onEmailChanged(newValue: String) {
        email = newValue
        errorMessage = null
    }

    fun onPasswordChanged(newValue: String) {
        password = newValue
        errorMessage = null
    }

    val isEmailValid: Boolean get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormValid: Boolean get() = isEmailValid && password.isNotBlank()

    // Status-variables
    var loginSuccess by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val instance = RetrofitClient.logininstance

    // process login data
    fun login() {
        if (!isFormValid) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = LoginRequest(
                    email = email,
                    password = password
                )

                val response = instance.loginUser(request)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        authStorage.saveUserId(loginResponse.userId)
                        authStorage.saveAccessToken(loginResponse.accessToken)
                        authStorage.saveRefreshToken(loginResponse.refreshToken)
                        loginSuccess = true
                    }
                } else {
                    if (response.code() == 401) {
                        errorMessage = "E-Mail oder Passwort ist falsch."
                    } else {
                        errorMessage = "Anmeldung fehlgeschlagen. Bitte erneut versuchen."
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Netzwerk- oder Serverfehler aufgetreten", e)
                e.printStackTrace()
                errorMessage = "Netzwerkfehler. Bitte überprüfe deine Verbindung."
            } finally {
                isLoading = false
            }
        }
    }
}

class LoginViewModelFactory(
    private val authStorage: AuthStorage,
    private val appContext: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authStorage, appContext) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}