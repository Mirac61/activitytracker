package com.example.activitytracker.ui.screens.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.activitytracker.Core.theme.CardSurface
import com.example.activitytracker.Core.theme.PrimaryAccent
import com.example.activitytracker.Core.theme.TertiaryAccent
import com.example.activitytracker.R
import com.example.activitytracker.ui.components.FilterDropdown
import com.example.activitytracker.ui.components.WeekdayBarChart
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.ceil

@Composable
fun ActivityStatsCard(
    averagePerDay: Double,
    mostActiveWeekday: DayOfWeek?,
    sumByWeekday: Map<DayOfWeek, Int>,
    averageByWeekday: Map<DayOfWeek, Double>,
    availableYears: List<Int>,
    selectedMonth: Int,
    selectedYear: Int,
    onMonthChange: (Int) -> Unit,
    onYearChange: (Int) -> Unit
) {
    val avgMax = ceil(averageByWeekday.values.maxOrNull() ?: 1.0).toInt().coerceAtLeast(2)
    val sumMax = (sumByWeekday.values.maxOrNull() ?: 5).let { ((it + 4) / 5) * 5 }.coerceAtLeast(5)

    val currentYearMonth = YearMonth.now()
    val availableMonths = if (selectedYear == currentYearMonth.year) {
        (1..currentYearMonth.monthValue).toList()
    } else {
        (1..12).toList()
    }

    val hasData = sumByWeekday.values.any { it > 0 }

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
                    options = availableMonths,
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
                    options = availableYears.ifEmpty { listOf(currentYearMonth.year) },
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
                            text = "${
                                String.format(
                                    Locale.GERMAN,
                                    "%.1f",
                                    avgForActiveDay
                                )
                            } Aktivitäten im Schnitt",
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

            if (!hasData) {
                Text(
                    text = "Keine Aktivitäten in diesem Zeitraum gefunden.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                )
            } else {
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
                    barColor = PrimaryAccent
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
                    barColor = TertiaryAccent
                )
            }
        }
    }
}