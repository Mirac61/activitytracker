package com.example.activitytracker.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

object StatisticsLogic {

    fun sumByWeekday(dates: List<LocalDate>): Map<DayOfWeek, Int> {
        return dates.groupingBy { it.dayOfWeek }.eachCount()
    }

    fun mostActiveWeekday(dates: List<LocalDate>): DayOfWeek? {
        return sumByWeekday(dates).maxByOrNull { it.value }?.key
    }

    fun averagePerDay(dates: List<LocalDate>): Double {
        if (dates.isEmpty()) return 0.0

        val totalActivities = dates.size
        val activeDays = dates.distinct().size

        return totalActivities / activeDays.toDouble()
    }

    fun averageByWeekday(dates: List<LocalDate>, month: YearMonth): Map<DayOfWeek, Double> {
        return sumByWeekday(dates).mapValues {
            val occurrences = (1..month.lengthOfMonth()).count { day ->
                month.atDay(day).dayOfWeek == it.key
            }
            it.value / occurrences.toDouble()
        }
    }
}