package com.example.activitytracker.domain

import java.time.Clock
import java.time.LocalDate

object StreakLogic {

    fun calculateStreak(dates: List<LocalDate>, clock: Clock = Clock.systemDefaultZone()): Int {
        val today = LocalDate.now(clock)
        if (dates.isEmpty()) return 0

        val uniqueDays = dates.filter { it <= today }.distinct().sortedDescending()
        var currentDate = today

        if (currentDate !in uniqueDays) {
            currentDate = currentDate.minusDays(1)
            if (currentDate !in uniqueDays) {
                return 0
            }
        }

        var streak = 0
        for (day in uniqueDays) {
            if (day == currentDate) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else if (day.isBefore(currentDate)) {
                break
            }
        }
        return streak
    }
}