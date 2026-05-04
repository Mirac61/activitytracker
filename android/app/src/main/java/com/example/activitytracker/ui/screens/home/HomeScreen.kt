package com.example.activitytracker.ui.screens.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.activitytracker.Core.theme.*
import com.example.activitytracker.data.local.entity.ActivityEntity
import java.time.LocalDate


@Composable
fun HomeScreen(activities: List<ActivityEntity> = emptyList()) {
    val today = LocalDate.now()

    val todaysActivities = activities.filter { it.activityDate == today }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // HEADER aus Branch 19 - Kalenderanzeige
        Spacer(modifier = Modifier.height(8.dp))

        // Kalenderslider aus Branch 19 - Kalenderanzeige

        if (todaysActivities.isEmpty()) {
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
                items(todaysActivities) { entity ->
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
            .clip(RoundedCornerShape(50))
            .border(1.5.dp, PrimaryAccent, RoundedCornerShape(50)),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}