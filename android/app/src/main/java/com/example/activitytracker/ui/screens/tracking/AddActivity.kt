package com.example.activitytracker.ui.screens.tracking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.window.PopupProperties
import com.example.activitytracker.Core.theme.SecondaryAccent
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivity(onDismiss: () ->  Unit, onSave: (String, LocalDate) -> Unit, activityNames: List<String>){
    var activityName by remember {mutableStateOf( "")}
    var showDatePicker by remember { mutableStateOf(false) }
    val filtered: List<String> = if (activityName.isBlank()) {activityNames} else {activityNames.filter { it.contains(activityName, ignoreCase = true) }}

    var textFieldWidth by remember { mutableStateOf(0) }
    var showSuggestions by remember { mutableStateOf(true) }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    // Today as date picker
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    val dateTextForUI = datePickerState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    } ?: ""



    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9F).padding(24.dp).imePadding().pointerInput(Unit) {
        detectTapGestures(onTap = { focusManager.clearFocus() })
    }) {

        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Schließen")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Aktivität erstellen", style = MaterialTheme.typography.headlineMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))


        Text("Name", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
      Box{
          OutlinedTextField(
              value = activityName,
              onValueChange = { activityName = it },
              modifier = Modifier.fillMaxWidth().onSizeChanged{textFieldWidth = it.width}
                  .onFocusChanged{isFocused = it.isFocused; if(it.isFocused){showSuggestions = true} },
              shape = RoundedCornerShape(12.dp),
              singleLine = true
          )
          DropdownMenu(
              expanded = filtered.isNotEmpty() && (isFocused || activityName.isNotBlank()) && showSuggestions,
              onDismissRequest = { showSuggestions = false },
              properties = PopupProperties(focusable = false),
              modifier =  Modifier.width(with(LocalDensity.current) { textFieldWidth.toDp()}),
          ) {
              filtered.forEach{ suggestion ->
                  DropdownMenuItem(
                      text = { Text(suggestion) },
                      onClick = {activityName = suggestion; showSuggestions = false
                      }
                  )
              }
          }
      }

        Spacer(modifier = Modifier.height(16.dp))


        Text("Datum", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            OutlinedTextField(
                value = dateTextForUI,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                readOnly = true,
                placeholder = { Text("TT.MM.JJJJ") }
            )
            Box(
                modifier = Modifier.matchParentSize().clickable { showDatePicker = true }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Abbrechen")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = {
                val millis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                val localDate = Instant.ofEpochMilli(millis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                onSave(activityName, localDate)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = SecondaryAccent),
            enabled = activityName.isNotBlank() && dateTextForUI.isNotBlank()
        ) {
            Text("Speichern", color = Color.White)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}