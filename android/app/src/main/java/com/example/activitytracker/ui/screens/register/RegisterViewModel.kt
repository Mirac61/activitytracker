package com.example.activitytracker.ui.screens.register

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.activitytracker.BuildConfig
import com.example.activitytracker.data.local.AppDatabase
import com.example.activitytracker.data.local.storage.AuthStorage
import com.example.activitytracker.data.network.RegisterApi
import com.example.activitytracker.data.network.RegisterRequest
import com.example.activitytracker.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterViewModel(private val authStorage: AuthStorage, private val appContext: Context) : ViewModel() {
    var vorname by mutableStateOf("")
        private set
    var nachname by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    fun onVornameChanged(newValue: String) {
        vorname = newValue
    }

    fun onNachnameChanged(newValue: String) {
        nachname = newValue
    }

    fun onEmailChanged(newValue: String) {
        email = newValue
        errorMessage = null
    }

    fun onPasswordChanged(newValue: String) {
        password = newValue
        errorMessage = null
    }

    val isEmailValid: Boolean get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormValid: Boolean get() = vorname.isNotBlank() && nachname.isNotBlank() && isEmailValid && password.isNotBlank() && password.length >= 6

    // Variable that listens if the registration process is successful
    var registrationSuccess by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun clearErrorMessage() {
        errorMessage = null
    }

    fun resetRegistrationStatus() {
        registrationSuccess = false
    }

    var isLoading by mutableStateOf(false)
        private set

    // Create Retrofit instance
    private val instance = RetrofitClient.registerinstance

    // sending the input Data to RegisterApi.kt
    fun register() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = RegisterRequest(
                    vorname = vorname,
                    nachname = nachname,
                    email = email,
                    password = password
                )

                val response = instance.registerUser(request)

                if (response.isSuccessful) {
                    val userId = response.body()?.userId
                    if (userId != null) {
                        // Alte Activities löschen bei neuem Account
                        val db = AppDatabase.getInstance(appContext)
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                            db.clearAllTables()
                        }
                        authStorage.saveUserId(userId)
                        registrationSuccess = true
                    }

                }else {
                    Log.e("RegisterViewModel", "Server Failure during registration: ${response.code()}")
                    errorMessage = "Registrierung fehlgeschlagen. E-Mail bereits vergeben."
                }
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Netzwerk- oder Serverfehler aufgetreten", e)
                e.printStackTrace()
                errorMessage = "Netzwerkfehler. Bitte überprüfe deine Verbindung."
            } finally {
                isLoading = false
            }
        }
    }
}

class RegisterViewModelFactory(
    private val authStorage: AuthStorage,
    private val appContext: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(authStorage, appContext) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}