package com.example.activitytracker.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activitytracker.Core.theme.InterFamily
import com.example.activitytracker.Core.theme.Outline
import com.example.activitytracker.Core.theme.StreakFill
import com.example.activitytracker.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val PAGE_COUNT = 10_000
private const val INITIAL_PAGE = 5_000

@Composable
fun CalendarSlider(activeDays: Set<LocalDate>, selectedDay: LocalDate, onDaySelected: (LocalDate) -> Unit){
    val today = LocalDate.now()
    val todayisMonday = today.minusDays((today.dayOfWeek.value - 1).toLong())
    val pagerState = rememberPagerState(
        initialPage = INITIAL_PAGE,
        pageCount = { PAGE_COUNT }
    )


    // When page changes, auto-select today if returning to current week,
    // otherwise select Monday of the new week
    LaunchedEffect(pagerState.currentPage) {
        val weekOffset = pagerState.currentPage - INITIAL_PAGE
        val pageMonday = todayisMonday.plusWeeks(weekOffset.toLong())
        val autoSelect = if (weekOffset == 0) today else pageMonday
        onDaySelected(autoSelect)
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) {
        page ->         val weekOffset = page - INITIAL_PAGE
        val pageMonday = todayisMonday.plusWeeks(weekOffset.toLong())

        WeekStrip(
            monday = pageMonday,
            today = today,
            selectedDay = selectedDay,
            activeDays = activeDays,
            onDaySelected = onDaySelected
        )
    }
}


@Composable
fun WeekStrip(monday: LocalDate, today: LocalDate, selectedDay: LocalDate, activeDays: Set<LocalDate>, onDaySelected: (LocalDate) -> Unit ){
    val dayLabels = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0..6){
            val day = monday.plusDays(i.toLong())
            val isToday = day == today
            val isSelected = day == selectedDay
            val isFuture = day.isAfter(today)
            val hasActivity = activeDays.contains(day)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.clickable { onDaySelected(day) }
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .then(
                            when {
                                isToday && isSelected -> Modifier
                                    .clip(CircleShape)
                                    .background(StreakFill.copy(alpha = 0.25f))
                                    .border(2.dp, Outline, CircleShape)
                                isSelected -> Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                    .border(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), CircleShape)
                                isToday -> Modifier
                                    .clip(CircleShape)
                                    .background(StreakFill.copy(alpha = 0.25f))
                                    .border(2.dp, Outline, CircleShape)
                                isFuture -> Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                else -> Modifier
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isFuture -> Unit
                        hasActivity -> SvgImage(
                            rawResId = R.raw.noto_fire,
                            modifier = Modifier.size(24.dp)
                        )
                        else -> SvgImage(
                            rawResId = R.raw.noto_grayed_fire,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = dayLabels[i],
                    style = TextStyle(
                        fontFamily = InterFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(48.dp))
}
