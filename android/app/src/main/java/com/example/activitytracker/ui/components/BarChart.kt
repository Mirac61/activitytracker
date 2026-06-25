package com.example.activitytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activitytracker.Core.theme.GridlineGray
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

private val YAxisWidth = 24.dp

@Composable
fun WeekdayBarChart(
    values: Map<DayOfWeek, Float>,
    yMax: Int,
    step: Int,
    barColor: Color,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 160.dp,
) {
    val ticks = (0..yMax step step).toList().reversed()

    Column(modifier = modifier.fillMaxWidth()) {

        Row(modifier = Modifier.height(chartHeight)) {

            Column(
                modifier = Modifier.fillMaxHeight().width(YAxisWidth),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                ticks.forEach { Text("$it", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline) }
            }

            Spacer(Modifier.width(8.dp))

            Box(modifier = Modifier.fillMaxHeight().weight(1f)) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    ticks.forEach { _ ->
                        HorizontalDivider(color = GridlineGray)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DayOfWeek.entries.forEach { day ->
                        val fraction = ((values[day] ?: 0f) / yMax).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(fraction)
                                .background(barColor)
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(Modifier.width(YAxisWidth + 8.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DayOfWeek.entries.forEach { day ->
                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, Locale.GERMAN),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}