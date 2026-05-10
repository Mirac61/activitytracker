package com.example.activitytracker.ui.screens.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activitytracker.Core.theme.*
import com.example.activitytracker.data.local.entity.ActivityEntity
import java.time.LocalDate
import com.example.activitytracker.ui.components.CalendarSlider
import com.example.activitytracker.ui.components.SvgImage
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun HomeScreen(activities: List<ActivityEntity> = emptyList(), streak: Int = 0, onSettingsClick: () -> Unit = {}
) {
    val today = LocalDate.now()
    var selectedDay by remember { mutableStateOf(today) }
    // Passing only dates having activites in CalendarSlider
    var activeDays = activities.map{it.activityDate}.toSet()

    val filteredActivites = activities.filter { it.activityDate == selectedDay }

    // Filter list by whatever day is selected in the slider
    val filteredDays = activities.filter { it.activityDate == selectedDay }

    val selectedDayFormatted = selectedDay.format(
        DateTimeFormatter.ofPattern("EEEE, d. MMMM", Locale.GERMAN)
    ).replaceFirstChar { it.uppercase() }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Medal with streak
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SvgImage(
                        rawResId = com.example.activitytracker.R.raw.medal,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = "$streak",
                        style = TextStyle(
                            fontFamily = InterTightFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black,
                        ),
                        modifier = Modifier.offset(x = (32).dp)

                    )
                }
            }

            // Date
            Text(
                text = selectedDayFormatted,
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 18.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f)
            )

            // Settings
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Einstellungen",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CalendarSlider(
            activeDays = activeDays,
            selectedDay = selectedDay,
            onDaySelected = {selectedDay = it}
        )
        Spacer(modifier = Modifier.height(32.dp))


        if (filteredActivites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
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
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredActivites) { entity ->
                    ActivityCard(name = entity.activityName)
                }
            }
        }
    }
}

@Composable
fun ActivityCard(name: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(35))
            .border(1.5.dp, PrimaryAccent, RoundedCornerShape(35)),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}