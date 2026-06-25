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

    fun calculateLongestStreak(dates: List<LocalDate>): Int {
        val sortedDays = dates.distinct().sorted()
        if (dates.isEmpty()) return 0

        var longestStreak = 1
        var currentStreak = 1

        // Start at 1 as each day is compared to its predecessor (i - 1), so the first day is skipped.
        for (i in 1 until sortedDays.size) {

            val previousDay = sortedDays[i - 1]
            val currentDay = sortedDays[i]

            if (isNextDay(previousDay, currentDay)) {
                currentStreak++
            } else {
                currentStreak = 1
            }

            longestStreak = maxOf(longestStreak, currentStreak)
        }
        return longestStreak
    }

    private fun isNextDay(day: LocalDate, nextDay: LocalDate): Boolean {
        return nextDay == day.plusDays(1)
    }
}