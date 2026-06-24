package com.example.activitytracker.ui.screens.register

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.activitytracker.data.local.AppDatabase
import com.example.activitytracker.data.local.storage.AuthStorage
import com.example.activitytracker.data.remote.RetrofitClient
import com.example.activitytracker.data.remote.dto.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class RegisterViewModel(
    private val authStorage: AuthStorage,
    private val database: AppDatabase
) : ViewModel() {

    var firstName by mutableStateOf("")
        private set
    var lastName by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    fun onFirstNameChanged(newValue: String) {
        firstName = newValue
    }

    fun onLastNameChanged(newValue: String) {
        lastName = newValue
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
    val isFormValid: Boolean get() = firstName.isNotBlank() && lastName.isNotBlank() && isEmailValid && password.isNotBlank() && password.length >= 6

    // Status variables
    var registrationSuccess by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun resetRegistrationStatus() {
        registrationSuccess = false
    }

    private val instance = RetrofitClient.api
    private val tag = "RegisterViewModel"

    // Sending the input data to backend api
    fun register() {
        if (!isFormValid) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = RegisterRequest(
                    vorname = firstName,
                    nachname = lastName,
                    email = email,
                    password = password
                )

                val response = instance.registerUser(request)

                if (response.isSuccessful) {
                    val userId = response.body()?.userId
                    if (userId != null) {
                        // Clear database on an IO thread safely using the injected database instance
                        withContext(Dispatchers.IO) {
                            database.clearAllTables()
                            authStorage.saveUserId(userId)
                        }
                        registrationSuccess = true
                    } else {
                        Log.e(tag, "Registration succeeded but server returned an empty user ID response body.")
                        errorMessage = "Unerwarteter Fehler: Server-Antwort war unvollständig."
                    }
                } else {
                    Log.e(tag, "Server rejected registration request with HTTP status code: ${response.code()}")
                    errorMessage = "Registrierung fehlgeschlagen. E-Mail bereits vergeben."
                }
            } catch (e: IOException) {
                Log.e(tag, "Network connectivity failure occurred during user registration", e)
                errorMessage = "Netzwerkfehler. Bitte überprüfe deine Verbindung."
            } catch (e: HttpException) {
                Log.e(tag, "Unexpected HTTP status error received during user registration", e)
                errorMessage = "Serverfehler. Bitte versuche es später erneut."
            } finally {
                isLoading = false
            }
        }
    }
}

class RegisterViewModelFactory(
    private val authStorage: AuthStorage,
    private val database: AppDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(authStorage, database) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model specification: ${modelClass.name}")
    }
}