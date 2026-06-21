package com.example.activitytracker.ui.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.YearMonth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.activitytracker.R
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun StatisticsScreen(
    longestStreak: Int,
    averagePerDay: Double,
    mostActiveWeekday: DayOfWeek?,
    sumByWeekday: Map<DayOfWeek, Int>,
    averageByWeekday: Map<DayOfWeek, Double>,
    onMonthSelected: (YearMonth) -> Unit
) {
    var selectedMonth by remember { mutableStateOf(YearMonth.now().monthValue) }
    var selectedYear by remember { mutableStateOf(YearMonth.now().year) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Dein Fortschritt",
            fontSize = 30.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 32.dp)
        )

        LongestStreakCard(longestStreak)

        Spacer(modifier = Modifier.height(16.dp))

        ActivityStatsCard(
            averagePerDay = averagePerDay,
            mostActiveWeekday = mostActiveWeekday,
            sumByWeekday = sumByWeekday,
            averageByWeekday = averageByWeekday,
            selectedMonth = selectedMonth,
            selectedYear = selectedYear,
            onMonthChange = { newMonth ->
                selectedMonth = newMonth
                onMonthSelected(YearMonth.of(selectedYear, newMonth))
            },
            onYearChange = { newYear ->
                selectedYear = newYear
                onMonthSelected(YearMonth.of(newYear, selectedMonth))
            }
        )

        // TODO: remove this after everything is tested
//        Text("Durchschnitt/Tag: $averagePerDay")
//        Text("Aktivster Tag: $mostActiveWeekday")
//        Text("Summe pro Wochentag: $sumByWeekday")
//        Text("Durchschnitt pro Wochentag: $averageByWeekday")
    }
}

@Composable
fun LongestStreakCard(longestStreak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_flame_medal),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Längster Streak",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$longestStreak",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    // TODO: Colors einheitlich machen per Color.kt
                    color = Color(0xFF33691E)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Tage",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    // TODO: Colors einheitlich machen per Color.kt
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}

@Composable
fun ActivityStatsCard(
    averagePerDay: Double,
    mostActiveWeekday: DayOfWeek?,
    sumByWeekday: Map<DayOfWeek, Int>,
    averageByWeekday: Map<DayOfWeek, Double>,
    selectedMonth: Int,
    selectedYear: Int,
    onMonthChange: (Int) -> Unit,
    onYearChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_running_man),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Deine Aktivitäten im Tag",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterDropdown(
                    label = "Monat",
                    selected = selectedMonth,
                    options = (1..12).toList(),
                    optionLabel = { monthNumber ->
                        YearMonth.of(selectedYear, monthNumber)
                            .month.getDisplayName(TextStyle.FULL, Locale.GERMAN)
                    },
                    onSelected = { onMonthChange(it) },
                    modifier = Modifier.weight(1f)
                )
                FilterDropdown(
                    label = "Jahr",
                    selected = selectedYear,
                    options = (2024..2026).toList(),
                    optionLabel = { it.toString() },
                    onSelected = { onYearChange(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Kacheln
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F5F7))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Aktivster Tag",
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                        Text(
                            text = mostActiveWeekday?.getDisplayName(TextStyle.SHORT, Locale.GERMAN) ?: "–",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        val avgForActiveDay = mostActiveWeekday?.let { averageByWeekday[it] } ?: 0.0
                        Text(
                            text = "${String.format(Locale.GERMAN, "%.1f", avgForActiveDay)} Aktivitäten im Schnitt",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F5F7))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = "Durchschn./Tag",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            text = String.format(Locale.GERMAN, "%.1f", averagePerDay),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Aktivitäten",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            /* TODO: Die bar charts rechachieren ob selber implementieren oder lieber
                eine Libraby benutzen besser ist. Danach hier implementieren
             */



            
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FilterDropdown(
    label: String,
    selected: T,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = optionLabel(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                // TODO: look after whats the better thing
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}