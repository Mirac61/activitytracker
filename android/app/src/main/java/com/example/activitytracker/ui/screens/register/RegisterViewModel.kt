package com.example.activitytracker.ui.screens.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    var vorname by mutableStateOf("")
    var nachname by mutableStateOf("")

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    val isEmailValid: Boolean get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormValid: Boolean get() = vorname.isNotBlank() && nachname.isNotBlank() && isEmailValid && password.isNotBlank() && password.length >= 6
}