package com.example.activitytracker.ui.screens.home

import com.example.activitytracker.ui.components.StreakBadge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activitytracker.Core.theme.*
import com.example.activitytracker.data.local.entity.ActivityEntity
import java.time.LocalDate
import com.example.activitytracker.ui.components.CalendarSlider
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale


@Composable
fun HomeScreen(
    activities: List<ActivityEntity> = emptyList(),
    streak: Int = 0,
    selectedDay: LocalDate,
    onDaySelected: (LocalDate) -> Unit,
    onSettingsClick: () -> Unit = {},
    onActivityClick: (ActivityEntity) -> Unit = {}
) {
    val today = LocalDate.now()
    val totalDays = 366
    val pagerState = rememberPagerState(initialPage = 365, pageCount = { totalDays })

    // Notifies the parent of changes and makes the pager the single source of truth (doesn't matter if swipe or click)
    LaunchedEffect(pagerState.currentPage) {
        val newDay = today.minusDays((totalDays - 1 - pagerState.currentPage).toLong())
        if (newDay != selectedDay) {
            onDaySelected(newDay)
        }
    }

    val activeDays = activities.map { it.activityDate }.toSet()
    val coroutineScope = rememberCoroutineScope()

    val selectedDayFormatted = selectedDay.format(
        DateTimeFormatter.ofPattern("EEEE, d. MMMM", Locale.GERMAN)
    ).replaceFirstChar { it.uppercase() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // medal and streak
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                StreakBadge(streak = streak)
            }
            // Date
            Text(
                text = selectedDayFormatted,
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 18.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f)
            )
            // Settings
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment
                    .CenterEnd
            ) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CalendarSlider(
            activeDays = activeDays,
            selectedDay = selectedDay,
            onDaySelected = { day ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        totalDays - 1 - ChronoUnit.DAYS.between(day, today).toInt()
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(32.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val dayForPage = today.minusDays((totalDays - 1 - page).toLong())
            val filteredActivities = activities.filter { it.activityDate == dayForPage }

            if (filteredActivities.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "Keine Aktivitäten an diesem Tag",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredActivities) { entity ->
                        ActivityCard(
                            name = entity.activityName,
                            onClick = { onActivityClick(entity) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityCard(name: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(35))
            .border(1.5.dp, PrimaryAccent, RoundedCornerShape(35))
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}