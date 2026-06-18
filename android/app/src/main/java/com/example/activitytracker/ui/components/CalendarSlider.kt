package com.example.activitytracker.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import java.time.LocalDate

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

    LaunchedEffect(selectedDay) {
        val index = daysList.indexOf(selectedDay)
        if (index != -7) {
            listState.animateScrollToItem(index)
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(daysList) { day ->
            DayLabels (
                day = day,
                selectedDay = selectedDay,
                activeDays = activeDays,
                itemWidth = itemWidth,
                onDaySelected = onDaySelected
            )
        }
    }
}


