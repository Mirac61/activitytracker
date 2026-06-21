package com.example.activitytracker

import com.example.activitytracker.domain.StatisticsLogic
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class StatisticsLogicTest {

    private val today = LocalDate.of(2026, 5, 3)

    @Test
    fun whenMultipleActivitiesThenCountsPerWeekday() {
        val yesterday = today.minusDays(1)
        val dates = listOf(today, today, yesterday)

        val result = StatisticsLogic.sumByWeekday(dates)

        assertEquals(2, result[today.dayOfWeek])
        assertEquals(1, result[yesterday.dayOfWeek])
    }

    @Test
    fun whenNoEntriesThenEmptyMap() {
        val result = StatisticsLogic.sumByWeekday(emptyList())
        assertEquals(emptyMap<java.time.DayOfWeek, Int>(), result)
    }

    @Test
    fun whenOneWeekdayHasMostThenReturnsThatWeekday() {
        val yesterday = today.minusDays(1)
        val dates = listOf(today, today, yesterday)  // today 2x, yesterday 1x

        val result = StatisticsLogic.mostActiveWeekday(dates)

        assertEquals(today.dayOfWeek, result)
    }

    @Test
    fun whenNoEntriesThenMostActiveWeekdayIsNull() {
        val result = StatisticsLogic.mostActiveWeekday(emptyList())
        assertEquals(null, result)
    }

    @Test
    fun whenActivitiesOnTwoDaysThenAveragePerDayIsCorrect() {
        val yesterday = today.minusDays(1)
        val dates = listOf(today, today, today, yesterday)

        val result = StatisticsLogic.averagePerDay(dates)

        assertEquals(2.0, result, 0.01)
    }

    @Test
    fun whenNoEntriesThenAveragePerDayIsZero() {
        val result = StatisticsLogic.averagePerDay(emptyList())
        assertEquals(0.0, result, 0.01)
    }

    @Test
    fun whenActivitiesOnSundayThenAverageIsSumDividedByOccurrences() {
        val dates = listOf(today, today, today)

        val result = StatisticsLogic.averageByWeekday(dates, YearMonth.of(2026, 5))

        // 3 activities / 5 sundays in Mai 2026 = 0.6
        assertEquals(0.6, result[today.dayOfWeek]!!, 0.01)
    }

    @Test
    fun whenNoEntriesThenAverageByWeekdayIsEmpty() {
        val result = StatisticsLogic.averageByWeekday(emptyList(), YearMonth.of(2026, 5))
        assertEquals(emptyMap<java.time.DayOfWeek, Double>(), result)
    }

}