package com.example.activitytracker.widget

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakWidgetLogicTest {

    @Test
    fun getVisibleDays_returnsOne_forSmallWidth() {
        val result = StreakWidgetLogic.getVisibleDays(80)

        assertEquals(1, result)
    }

    @Test
    fun getVisibleDays_returnsThree_forMediumWidth() {
        val result = StreakWidgetLogic.getVisibleDays(130)

        assertEquals(3, result)
    }

    @Test
    fun getVisibleDays_returnsFive_forLargeWidth() {
        val result = StreakWidgetLogic.getVisibleDays(200)

        assertEquals(5, result)
    }

    @Test
    fun getVisibleDays_returnsSeven_forExtraLargeWidth() {
        val result = StreakWidgetLogic.getVisibleDays(270)

        assertEquals(7, result)
    }

    @Test
    fun getDisplayedDays_returnsLastThreeDaysIncludingToday() {
        val today = LocalDate.of(2026, 5, 23)

        val result = StreakWidgetLogic.getDisplayedDays(today, 3)

        assertEquals(
            listOf(
                LocalDate.of(2026, 5, 21),
                LocalDate.of(2026, 5, 22),
                LocalDate.of(2026, 5, 23)
            ),
            result
        )
    }

    @Test
    fun getDisplayedDays_returnsOnlyToday_whenVisibleDaysIsOne() {
        val today = LocalDate.of(2026, 5, 23)

        val result = StreakWidgetLogic.getDisplayedDays(today, 1)

        assertEquals(
            listOf(LocalDate.of(2026, 5, 23)),
            result
        )
    }
}