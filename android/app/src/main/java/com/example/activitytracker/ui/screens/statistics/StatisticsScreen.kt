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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activitytracker.Core.theme.CardSurface
import com.example.activitytracker.Core.theme.ChartGreen
import com.example.activitytracker.Core.theme.ChartGreenLight
import com.example.activitytracker.R
import com.example.activitytracker.ui.components.FilterDropdown
import com.example.activitytracker.ui.components.WeekdayBarChart
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.ceil

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
    }
}

@Composable
fun LongestStreakCard(longestStreak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$longestStreak",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChartGreen
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Tage",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 8.dp)
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
    val avgMax = ceil(averageByWeekday.values.maxOrNull() ?: 1.0).toInt().coerceAtLeast(2)
    val sumMax = (sumByWeekday.values.maxOrNull() ?: 5).let { ((it + 4) / 5) * 5 }.coerceAtLeast(5)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_running_man),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Deine Aktivitäten im Tag",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    options = (2024..YearMonth.now().year).toList(),
                    optionLabel = { it.toString() },
                    onSelected = { onYearChange(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Aktivster Tag",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = mostActiveWeekday?.getDisplayName(TextStyle.SHORT, Locale.GERMAN) ?: "–",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        val avgForActiveDay = mostActiveWeekday?.let { averageByWeekday[it] } ?: 0.0
                        Text(
                            text = "${String.format(Locale.GERMAN, "%.1f", avgForActiveDay)} Aktivitäten im Schnitt",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Durchschn./Tag",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = String.format(Locale.GERMAN, "%.1f", averagePerDay),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Aktivitäten",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Durchschnittliche Aktivitäten im Monat",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(16.dp))

            WeekdayBarChart(
                values = averageByWeekday.mapValues { (_, avg) -> avg.toFloat() },
                yMax = avgMax,
                step = 1,
                barColor = ChartGreen
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Aufsummierte Aktivitäten im Monat",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(16.dp))

            WeekdayBarChart(
                values = sumByWeekday.mapValues { (_, count) -> count.toFloat() },
                yMax = sumMax,
                step = (sumMax / 4).coerceAtLeast(1),
                barColor = ChartGreenLight
            )
        }
    }
}