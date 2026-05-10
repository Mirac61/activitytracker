package com.example.activitytracker.ui.screens.register
import androidx.compose.foundation.BorderStroke
import com.example.activitytracker.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activitytracker.Core.theme.PrimaryAccent

@Composable
fun RegistrationScreen(viewModel: RegisterViewModel = viewModel()) {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //logo and title
        Spacer(modifier = Modifier.height(40.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_edit),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = PrimaryAccent
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

        //Text above inputs
        Text(
            text = "Registrierung",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 16.sp,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Willkommen! Registriere dich, um fortzufahren.",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        //Input fields
        CustomTextField(
            value = viewModel.vorname,
            onValueChange = { viewModel.vorname = it },
            label = "Vorname"
        )

        CustomTextField(
            value = viewModel.nachname,
            onValueChange = { viewModel.nachname = it },
            label = "Nachname"
        )

        CustomTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = "E-Mail",
            isError = viewModel.email.isNotEmpty() && !viewModel.isEmailValid,
            errorMessage = "Ungültige Emailadresse"
        )

        CustomTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = "Passwort",
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Google Login Button
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
            border = BorderStroke(1.dp, Color(0xFF000000))
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

        // 5. Register button
        Button(
            onClick = { /* TODO: Regular Login */ },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = viewModel.isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryAccent,
                disabledContainerColor = Color.LightGray
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Registrieren", color = Color.White)
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorMessage: String = "",
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = isError,
            visualTransformation = visualTransformation,
            singleLine = true,
            keyboardOptions = if (visualTransformation is PasswordVisualTransformation)
            {
                KeyboardOptions(keyboardType = KeyboardType.Password)
            } else {
                KeyboardOptions.Default
            }
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}