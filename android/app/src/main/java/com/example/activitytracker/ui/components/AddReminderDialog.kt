package com.example.activitytracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (dayOfWeek: Int, hour: Int, minute: Int, title: String, text: String, isDaily: Boolean) -> Unit
) {
    val dayOptions = listOf(
        "Montag" to Calendar.MONDAY,
        "Dienstag" to Calendar.TUESDAY,
        "Mittwoch" to Calendar.WEDNESDAY,
        "Donnerstag" to Calendar.THURSDAY,
        "Freitag" to Calendar.FRIDAY,
        "Samstag" to Calendar.SATURDAY,
        "Sonntag" to Calendar.SUNDAY
    )
    var selectedDay by remember { mutableStateOf(dayOptions[0]) }
    var expanded by remember { mutableStateOf(false) }
    var isDaily by remember { mutableStateOf(false) }
    var hourText by remember { mutableStateOf("12") }
    var minuteText by remember { mutableStateOf("00") }
    var titleText by remember { mutableStateOf("") }
    var reminderText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Erinnerung hinzufügen") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!isDaily) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedDay.first,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Wochentag") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            dayOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.first) },
                                    onClick = {
                                        selectedDay = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = hourText,
                        onValueChange = { if (it.length <= 2) hourText = it.filter { c -> c.isDigit() } },
                        label = { Text("Stunde") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minuteText,
                        onValueChange = { if (it.length <= 2) minuteText = it.filter { c -> c.isDigit() } },
                        label = { Text("Minute") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Titel") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = reminderText,
                    onValueChange = { reminderText = it },
                    label = { Text("Erinnerungstext") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isDaily,
                        onCheckedChange = { isDaily = it }
                    )
                    Text(
                        text = "Täglich",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val hour = hourText.toIntOrNull()
                val minute = minuteText.toIntOrNull()
                when {
                    hour == null || hour !in 0..23 -> error = "Stunde muss zwischen 0 und 23 liegen"
                    minute == null || minute !in 0..59 -> error = "Minute muss zwischen 0 und 59 liegen"
                    titleText.isBlank() -> error = "Bitte einen Titel eingeben"
                    reminderText.isBlank() -> error = "Bitte einen Text eingeben"
                    else -> onConfirm(selectedDay.second, hour, minute, titleText.trim(), reminderText.trim(), isDaily)
                }
            }) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}
