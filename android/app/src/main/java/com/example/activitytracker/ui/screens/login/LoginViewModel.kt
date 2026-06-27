package com.example.activitytracker.ui.screens.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.activitytracker.data.local.storage.AuthStorage
import com.example.activitytracker.data.remote.RetrofitClient
import com.example.activitytracker.data.remote.dto.GoogleLoginRequest
import com.example.activitytracker.data.remote.dto.LoginRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(private val authStorage: AuthStorage) : ViewModel() {

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

    // Status variables
    var loginSuccess by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val instance = RetrofitClient.api
    private val tag = "LoginViewModel"

    // Standard email/password login
    fun login() {
        if (!isFormValid) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = LoginRequest(email = email, password = password)
                val response = instance.loginUser(request)

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        authStorage.saveUserId(loginResponse.userId)
                        authStorage.saveAccessToken(loginResponse.accessToken)
                        authStorage.saveRefreshToken(loginResponse.refreshToken)
                        loginSuccess = true
                    } ?: run {
                        errorMessage = "Unerwarteter Fehler: Server-Antwort war leer."
                    }
                } else {
                    errorMessage = when (response.code()) {
                        401 -> "E-Mail oder Passwort ist falsch."
                        else -> "Anmeldung fehlgeschlagen. Bitte erneut versuchen."
                    }
                }
            } catch (e: IOException) {
                Log.e(tag, "Network error during standard email login", e)
                errorMessage = "Netzwerkfehler. Bitte überprüfe deine Verbindung."
            } catch (e: HttpException) {
                Log.e(tag, "Unexpected HTTP error during standard email login", e)
                errorMessage = "Serverfehler. Bitte versuche es später erneut."
            } finally {
                isLoading = false
            }
        }
    }

    // Builds the specific request configuration for the CredentialManager.
    fun buildGoogleCredentialRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("741626481823-gceju18n97rqdd5l16pcea00s6cttk2l.apps.googleusercontent.com")
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    // Executes the CredentialManager dialog and handles platform-specific exceptions granularly.
    suspend fun executeGetCredential(
        credentialManager: CredentialManager,
        context: Context,
        request: GetCredentialRequest
    ): GetCredentialResponse? {
        return try {
            credentialManager.getCredential(context = context, request = request)
        } catch (e: GetCredentialCancellationException) {
            Log.d(tag, "Google authentication flow was cancelled by the user.")
            null
        } catch (e: GetCredentialException) {
            Log.e(tag, "CredentialManager encountered a specific security or configuration error", e)
            errorMessage = "Google-Anmeldung fehlgeschlagen. Bitte versuche es erneut."
            null
        }
    }

    // Processes the successful credential bundle returned from the UI.
    fun onGoogleCredentialReceived(credentialData: android.os.Bundle) {
        try {
            val idToken = GoogleIdTokenCredential.createFrom(credentialData).idToken
            if (idToken.isNotEmpty()) {
                Log.d(tag, "Google ID token successfully extracted. Triggering backend authentication.")
                loginWithGoogle(idToken)
            } else {
                Log.w(tag, "Google ID token inside the credential bundle data was null or empty.")
                errorMessage = "Google-Anmeldung fehlgeschlagen: Ungültiges Token."
            }
        } catch (e: IllegalArgumentException) {
            Log.e(tag, "Failed to parse Google credential bundle due to invalid format or missing data", e)
            errorMessage = "Fehler bei der Verarbeitung des Google-Logins."
        }
    }

    // Backend API call using the extracted Google ID token
    private fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = instance.loginWithGoogle(GoogleLoginRequest(idToken))

                if (response.isSuccessful) {
                    response.body()?.let { googleResponse ->
                        withContext(Dispatchers.IO) {
                            authStorage.saveAccessToken(googleResponse.accessToken)
                            authStorage.saveRefreshToken(googleResponse.refreshToken)
                            authStorage.saveUserId(googleResponse.userId)
                        }
                        Log.d(tag, "Google authentication verified by backend. Session tokens securely saved.")
                        loginSuccess = true
                    } ?: run {
                        errorMessage = "Unerwarteter Fehler beim Auslesen der Google-Sitzung."
                    }
                } else {
                    Log.e(tag, "Backend rejected the Google authentication request with status code: ${response.code()}")
                    errorMessage = "Anmeldung via Google vom Server abgelehnt."
                }
            } catch (e: IOException) {
                Log.e(tag, "Network call to backend failed during Google authentication", e)
                errorMessage = "Netzwerkfehler. Verbindung zum Server fehlgeschlagen."
            } catch (e: HttpException) {
                Log.e(tag, "HTTP status exception received during Google backend authentication", e)
                errorMessage = "Serverfehler bei der Google-Authentifizierung."
            } finally {
                isLoading = false
            }
        }
    }
}

class LoginViewModelFactory(
    private val authStorage: AuthStorage,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authStorage) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model specification: ${modelClass.name}")
    }
}