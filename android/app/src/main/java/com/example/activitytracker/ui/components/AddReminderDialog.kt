package com.example.activitytracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.activitytracker.Core.theme.AlertError
import com.example.activitytracker.Core.theme.SecondaryAccent
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
    val timePickerState = rememberTimePickerState(initialHour = 12, initialMinute = 0, is24Hour = true)
    var showTimePicker by remember { mutableStateOf(false) }
    var titleText by remember { mutableStateOf("") }
    var reminderText by remember { mutableStateOf("") }

    val canSave = titleText.isNotBlank() && reminderText.isNotBlank()

    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = AlertDialogDefaults.containerColor,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Uhrzeit wählen",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    )
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            selectorColor = SecondaryAccent,
                            clockDialSelectedContentColor = Color.White,
                            timeSelectorSelectedContainerColor = SecondaryAccent,
                            timeSelectorSelectedContentColor = Color.White,
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Abbrechen", color = AlertError)
                        }
                        Button(
                            onClick = { showTimePicker = false },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryAccent)
                        ) { Text("OK", color = Color.White) }
                    }
                }
            }
        }
    }

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
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "%02d:%02d Uhr".format(timePickerState.hour, timePickerState.minute),
                        style = MaterialTheme.typography.bodyLarge
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        selectedDay.second,
                        timePickerState.hour,
                        timePickerState.minute,
                        titleText.trim(),
                        reminderText.trim(),
                        isDaily
                    )
                },
                enabled = canSave,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryAccent)
            ) {
                Text("Speichern", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = AlertError)
            }
        }
    )
}
