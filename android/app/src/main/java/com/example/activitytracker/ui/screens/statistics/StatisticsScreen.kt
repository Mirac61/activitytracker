package com.example.activitytracker.ui.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.YearMonth

@Composable
fun StatisticsScreen(
    longestStreak: Int,
    averagePerDay: Double,
    mostActiveWeekday: DayOfWeek?,
    sumByWeekday: Map<DayOfWeek, Int>,
    averageByWeekday: Map<DayOfWeek, Double>,
    availableYears: List<Int>,
    onMonthSelected: (YearMonth) -> Unit
) {
    var selectedMonth by remember { mutableStateOf(YearMonth.now().monthValue) }
    var selectedYear by remember { mutableStateOf(YearMonth.now().year) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Dein Fortschritt",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
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
            availableYears = availableYears,
            selectedMonth = selectedMonth,
            selectedYear = selectedYear,
            onMonthChange = { newMonth ->
                selectedMonth = newMonth
                onMonthSelected(YearMonth.of(selectedYear, newMonth))
            },
            onYearChange = {newYear ->
                selectedYear = newYear
                val currentYearMonth = YearMonth.now()
                if (newYear == currentYearMonth.year && selectedMonth > currentYearMonth.monthValue) {
                    selectedMonth = currentYearMonth.monthValue
                }
                onMonthSelected(YearMonth.of(newYear, selectedMonth))
            }
        )
    }
}

