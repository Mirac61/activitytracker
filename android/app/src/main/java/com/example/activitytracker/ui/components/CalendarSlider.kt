package com.example.activitytracker.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activitytracker.Core.theme.InterFamily
import com.example.activitytracker.R
import java.time.LocalDate
import java.time.format.TextStyle as FormatStyle
import java.util.Locale

private const val BACKWARD_DAYS_COUNT = 365

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun CalendarSlider(
    activeDays: Set<LocalDate>,
    selectedDay: LocalDate,
    onDaySelected: (LocalDate) -> Unit) {

    val today = LocalDate.now()

    val daysList = remember(today) {
        (BACKWARD_DAYS_COUNT downTo 0).map { today.minusDays(it.toLong()) }
    }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = daysList.lastIndex)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val itemWidth = (screenWidth - 16.dp) / 7

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(daysList) { day ->
            DayOfWeek(
                day = day,
                selectedDay = selectedDay,
                activeDays = activeDays,
                itemWidth = itemWidth,
                onDaySelected = onDaySelected
            )
        }
    }
}


@Composable
fun DayOfWeek(
    day: LocalDate,
    selectedDay: LocalDate,
    activeDays: Set<LocalDate>,
    itemWidth: androidx.compose.ui.unit.Dp,
    onDaySelected: (LocalDate) -> Unit
) {
    val isSelected = day == selectedDay
    val hasActivity = activeDays.contains(day)
    val dayLabel = day.dayOfWeek.getDisplayName(FormatStyle.SHORT, Locale.GERMAN)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(itemWidth)
            .clickable { onDaySelected(day) }
    ) {
        Box(
            modifier = Modifier.size(42.dp)
                .then(
                    when {
                        isSelected -> Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                            .border(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), CircleShape)
                        else -> Modifier.size(24.dp)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                hasActivity && isSelected ->
                    SvgImage(
                        rawResId = R.raw.noto_fire,
                        modifier = Modifier.size(32.dp)
                )
                hasActivity ->
                    SvgImage(
                        rawResId = R.raw.noto_fire,
                        modifier = Modifier.size(24.dp)
                    )
                !hasActivity && isSelected ->
                    SvgImage(
                        rawResId = R.raw.noto_grayed_fire,
                        modifier = Modifier.size(32.dp)
                    )
                !hasActivity ->
                    SvgImage(
                        rawResId = R.raw.noto_grayed_fire,
                        modifier = Modifier.size(24.dp)
                    )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Box(
            modifier = Modifier.height(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayLabel,
                style = TextStyle(
                    fontFamily = InterFamily,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = if (isSelected) 14.sp else 12.sp,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
        }
    }
}