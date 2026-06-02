package com.example.activitytracker.ui.screens.tracking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.activitytracker.data.local.entity.ActivityEntity

@Composable
fun EditActivity(
    activity: ActivityEntity,
    onDismiss: () -> Unit,
    onSave: (ActivityEntity) -> Unit
) {
    var activityName by remember(activity.id) {
        mutableStateOf(activity.activityName)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9F)
            .padding(24.dp)
            .imePadding()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Schließen")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Aktivität bearbeiten",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Name", style = MaterialTheme.typography.labelMedium)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = activityName,
            onValueChange = { activityName = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSave(
                    activity.copy(
                        activityName = activityName.trim()
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C7A50)),
            enabled = activityName.isNotBlank()
        ) {
            Text("Speichern", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}