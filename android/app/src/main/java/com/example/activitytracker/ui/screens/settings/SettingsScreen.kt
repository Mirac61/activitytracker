package com.example.activitytracker.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.activitytracker.data.local.entity.ReminderEntity
import com.example.activitytracker.ui.components.AddReminderDialog
import com.example.activitytracker.ui.components.AddReminderTile
import com.example.activitytracker.ui.components.ReminderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    reminders: List<ReminderEntity>,
    onBack: () -> Unit,
    onAddReminder: (dayOfWeek: Int, hour: Int, minute: Int, title: String, text: String, isDaily: Boolean) -> Unit,
    onDeleteReminder: (ReminderEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Zurück")
            }
            Text(
                text = "Einstellungen",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Erinnerungen",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                )
            }
            items(reminders, key = { it.requestCode }) { reminder ->
                ReminderCard(
                    reminder = reminder,
                    onDelete = { onDeleteReminder(reminder) }
                )
            }
            item {
                AddReminderTile(onClick = { showDialog = true })
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    if (showDialog) {
        AddReminderDialog(
            onDismiss = { showDialog = false },
            onConfirm = { day, hour, minute, title, text, isDaily ->
                onAddReminder(day, hour, minute, title, text, isDaily)
                showDialog = false
            }
        )
    }
}
