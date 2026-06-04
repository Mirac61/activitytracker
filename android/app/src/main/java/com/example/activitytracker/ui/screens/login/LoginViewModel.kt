package com.example.activitytracker.ui.screens.login

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.activitytracker.BuildConfig
import com.example.activitytracker.data.local.storage.AuthStorage
import com.example.activitytracker.data.network.LoginApi
import com.example.activitytracker.data.network.LoginRequest
import com.example.activitytracker.data.network.RefreshRequest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginViewModel(private val authStorage: AuthStorage, private val appContext: Context) : ViewModel() {

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    init {
        checkAndRefreshLogin()
    }

    fun onEmailChanged(newValue: String) {
        email = newValue
        errorMessage = null
    }

    fun onPasswordChanged(newValue: String) {
        password = newValue
        errorMessage = null
    }

    // Validation
    val isEmailValid: Boolean get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormValid: Boolean get() = isEmailValid && password.isNotBlank()

    // Status-variables
    var loginSuccess by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isChecking by mutableStateOf(true)
        private set

    // Create Retrofit instance
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(LoginApi::class.java)

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

                val response = api.loginUser(request)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        authStorage.saveUserId(loginResponse.userId)
                        authStorage.saveAccessToken(loginResponse.accessToken)
                        authStorage.saveRefreshToken(loginResponse.refreshToken)

                        loginSuccess = true
                    }
                } else {
                    println("DEBUG: Server Failure: ${response.code()}")
                    if (response.code() == 401) {
                        errorMessage = "E-Mail oder Passwort ist falsch."
                    } else {
                        errorMessage = "Anmeldung fehlgeschlagen. Bitte erneut versuchen."
                    }
                }
            } catch (e: Exception) {
                println("DEBUG: Connection not made! Mistake: ${e.localizedMessage}")
                e.printStackTrace()
                errorMessage = "Netzwerkfehler. Bitte überprüfe deine Verbindung."
            } finally {
                isLoading = false
            }
        }
    }

    //get new tokens
    private fun checkAndRefreshLogin() {
        viewModelScope.launch {
            val savedRefreshToken = authStorage.getRefreshToken()

            if (savedRefreshToken.isNullOrBlank()) {
                isChecking = false
                return@launch
            }

            try {
                val response = api.refreshToken(RefreshRequest(refreshToken = savedRefreshToken))

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        authStorage.saveUserId(loginResponse.userId)
                        authStorage.saveAccessToken(loginResponse.accessToken)
                        authStorage.saveRefreshToken(loginResponse.refreshToken)

                        loginSuccess = true
                    }
                } else {
                    authStorage.clearAll()
                    errorMessage = "Deine Sitzung ist abgelaufen."
                }
            } catch (e: Exception) {
                errorMessage = "Netzwerkfehler."
            } finally {
                isChecking = false
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