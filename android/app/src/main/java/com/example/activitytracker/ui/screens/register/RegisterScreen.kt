package com.example.activitytracker.ui.screens.register

import androidx.compose.foundation.BorderStroke
import com.example.activitytracker.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activitytracker.Core.theme.InputField
import com.example.activitytracker.Core.theme.PrimaryAccent
import com.example.activitytracker.Core.theme.TextDescription
import com.example.activitytracker.data.ActivityApplication
import com.example.activitytracker.ui.components.AuthTextField
import kotlinx.coroutines.delay

@Composable
fun RegistrationScreen(onRegistrationComplete: () -> Unit, onNavigateToLogin: () -> Unit) {

    val application = LocalContext.current.applicationContext as ActivityApplication
    val viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(application.authStorage, application)
    )

    val scrollState = rememberScrollState()
    var showSuccessMessage by remember { mutableStateOf(false)}

    LaunchedEffect(viewModel.registrationSuccess) {
        if (viewModel.registrationSuccess) {
            showSuccessMessage = true
            delay(1500)
            showSuccessMessage = false
            viewModel.resetRegistrationStatus()
            onRegistrationComplete()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // logo and title
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_running_man),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "ActivityTracker",
                    fontSize = 32.sp,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Text above inputs
            Text(
                text = "Registrierung",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Willkommen! Registriere dich, um fortzufahren.",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDescription
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Input fields
            AuthTextField(
                value = viewModel.firstName,
                onValueChange = { viewModel.onFirstNameChanged(it) },
                label = "Vorname"
            )

            AuthTextField(
                value = viewModel.lastName,
                onValueChange = { viewModel.onLastNameChanged(it) },
                label = "Nachname"
            )

            AuthTextField(
                value = viewModel.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                label = "E-Mail",
                keyboardType = KeyboardType.Email,
                isError = viewModel.email.isNotEmpty() && !viewModel.isEmailValid,
                errorMessage = "Ungültige Emailadresse"
            )

            AuthTextField(
                value = viewModel.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                label = "Passwort",
                visualTransformation = PasswordVisualTransformation()
            )

            viewModel.errorMessage?.takeIf { it.isNotEmpty() }?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // show success message
            if (showSuccessMessage) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Registrierung erfolgreich! Leite weiter...",
                    color = PrimaryAccent,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Google Login Button
            OutlinedButton(
                onClick = { /* TODO: Google Login */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        modifier = Modifier.size(24.dp),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Mit Google anmelden",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Register button
            Button(
                onClick = { viewModel.register() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = viewModel.isFormValid && !viewModel.isLoading && !showSuccessMessage,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryAccent,
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = InputField, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        "Registrieren",
                        color = InputField,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.align(CenterHorizontally)
            ) {
                Text(
                    text = "Bereits ein Konto? Hier anmelden",
                    color = PrimaryAccent,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Spacer(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.ime)
                    .fillMaxWidth()
            )
        }
    }
}