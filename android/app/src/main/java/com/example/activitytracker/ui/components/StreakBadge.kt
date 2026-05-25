package com.example.activitytracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activitytracker.R

@Composable
fun StreakBadge(streak: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SvgImage(
            rawResId = R.raw.medal,
            modifier = Modifier.size(32.dp)
        )

        Text(
            text = streak.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}