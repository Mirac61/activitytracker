package com.example.activitytracker.ui.screens.tracking
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivity(onDismiss: () ->  Unit, onSave: (String) -> Unit){
    var activityName by remember {mutableStateOf( "")}

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        // Der Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Schließen")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Aktivität erstellen", style = MaterialTheme.typography.headlineMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nameneintrag
        Text("Name", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = activityName,
            onValueChange = { activityName = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Knopf zum Speichern
        Button(
            onClick = {onSave(activityName)},
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C7A50))
        ) {
            Text("Speichern", color = Color.White)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}