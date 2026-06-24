package com.example.activitytracker.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.activitytracker.Core.theme.PrimaryAccent
import com.example.activitytracker.data.local.entity.ReminderEntity
import java.util.Calendar

@Composable
fun ReminderCard(reminder: ReminderEntity, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, PrimaryAccent, RoundedCornerShape(35))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reminder.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${if (reminder.isDaily) "Täglich" else dayName(reminder.dayOfWeek)}, %02d:%02d".format(reminder.hour, reminder.minute),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = reminder.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = "Löschen",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun AddReminderTile(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, PrimaryAccent, RoundedCornerShape(35))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.Add,
            contentDescription = "Erinnerung hinzufügen",
            tint = PrimaryAccent
        )
    }
}

private fun dayName(dayOfWeek: Int): String = when (dayOfWeek) {
    Calendar.MONDAY -> "Montag"
    Calendar.TUESDAY -> "Dienstag"
    Calendar.WEDNESDAY -> "Mittwoch"
    Calendar.THURSDAY -> "Donnerstag"
    Calendar.FRIDAY -> "Freitag"
    Calendar.SATURDAY -> "Samstag"
    Calendar.SUNDAY -> "Sonntag"
    else -> "?"
}
