package com.example.activitytracker.ui.screens.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activitytracker.data.network.RegisterApi
import com.example.activitytracker.data.network.RegisterRequest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterViewModel : ViewModel() {
    var vorname by mutableStateOf("")
    var nachname by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    // Create Retrofit instance
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(RegisterApi::class.java)

    // Validation
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

    // sending the input Data to RegisterApi.kt
    fun register() {
        viewModelScope.launch {
            try {
                val request = RegisterRequest(
                    vorname = vorname,
                    nachname = nachname,
                    email = email,
                    password = password
                )

                val response = api.registerUser(request)

                if (response.isSuccessful) {
                    println("DEBUG: Registration successfully!")
                    registrationSuccess = true
                } else {
                    println("DEBUG: Server Failure: ${response.code()}")
                    errorMessage = "Registrierung fehlgeschlagen. E-Mail eventuell bereits vergeben."
                }
            } catch (e: Exception) {
                println("DEBUG: Connection not made! Mistake: ${e.localizedMessage}")
                e.printStackTrace()
                errorMessage = "Netzwerkfehler."
            }
        }
    }
}