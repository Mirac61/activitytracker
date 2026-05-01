package com.example.activitytracker

import com.example.activitytracker.domain.StreakLogic
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakLogicTest {

    @Test
    fun whenNoEntriesThenStreakIsZero() {
        val result = StreakLogic.calculateStreak(dates = emptyList())
        assertEquals(0, result)
    }

    @Test
    fun whenTodayHasEntryThenStreakIsOne() {
        val result = StreakLogic.calculateStreak(dates = listOf(LocalDate.now()))
        assertEquals(1, result)
    }

    @Test
    fun whenYesterdayHasEntryAndTodayNoneThenStreakOne() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val dates = listOf(yesterday)
        val result = StreakLogic.calculateStreak(dates = dates)
        assertEquals(1, result)
    }

    @Test
    fun whenYesterdayHasEntryThenStreakIsTwo() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val dates = listOf(today, yesterday)
        val result = StreakLogic.calculateStreak(dates = dates)
        assertEquals(2, result)
    }

    @Test
    fun whenTwoDaysAgoAndYesterdayHasEntryThenStreakIsThree() {
        val today = LocalDate.now()
        val twoDaysAgo = today.minusDays(2)
        val yesterday = today.minusDays(1)
        val dates = listOf(today, yesterday, twoDaysAgo)
        val result = StreakLogic.calculateStreak(dates = dates)
        assertEquals(3, result)
    }

    @Test
    fun whenTwoDaysAgoAndNotYesterdayHasEntryThenStreakIsOne() {
        val today = LocalDate.now()
        val twoDaysAgo = today.minusDays(2)
        val dates = listOf(today, twoDaysAgo)
        val result = StreakLogic.calculateStreak(dates = dates)
        assertEquals(1, result)
    }

    @Test
    fun whenOnlyOldEntriesThenStreakIsZero() {
        val today = LocalDate.now()
        val threeDaysAgo = today.minusDays(3)
        val fourDaysAgo = today.minusDays(4)
        val dates = listOf(threeDaysAgo, fourDaysAgo)
        val result = StreakLogic.calculateStreak(dates = dates)
        assertEquals(0, result)
    }
}