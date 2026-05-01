package com.example.activitytracker.Core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.activitytracker.R


val InterFamily: FontFamily = FontFamily(
    Font(R.font.inter_tight_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_bold, FontWeight.Bold)
)

val InterTightFamily = FontFamily(
    Font(R.font.inter_tight_regular, FontWeight.Normal),
    Font(R.font.inter_tight_medium, FontWeight.Medium),
    Font(R.font.inter_tight_bold, FontWeight.Bold)
)

val AppTypography = Typography(
    headlineLarge = TextStyle(fontFamily = InterTightFamily, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontFamily = InterTightFamily, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontFamily = InterTightFamily, fontWeight = FontWeight.Bold),
    bodyLarge = TextStyle(fontFamily = InterFamily, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontFamily = InterFamily, fontWeight = FontWeight.Normal),
    labelMedium = TextStyle(fontFamily = InterFamily, fontWeight = FontWeight.Medium),
)
