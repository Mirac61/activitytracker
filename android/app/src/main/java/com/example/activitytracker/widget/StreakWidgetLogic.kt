package com.example.activitytracker.widget

import java.time.LocalDate

object StreakWidgetLogic {

    fun getVisibleDays(widthDp: Int): Int {
        return when {
            widthDp < 100 -> 1
            widthDp < 170 -> 3
            widthDp < 240 -> 5
            else -> 7
        }
    }

    fun getDisplayedDays(today: LocalDate, visibleDays: Int): List<LocalDate> {
        return (visibleDays - 1 downTo 0).map { offset ->
            today.minusDays(offset.toLong())
        }
    }
}