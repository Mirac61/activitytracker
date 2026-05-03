package com.example.activitytracker

import com.example.activitytracker.domain.StreakLogic
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.Clock
import java.time.ZoneId

class StreakLogicTest {

    private val today = LocalDate.of(2026, 5, 3)
    private val clock = Clock.fixed(
        today.atStartOfDay(ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault()
    )

    @Test
    fun whenNoEntriesThenStreakIsZero() {
        val result = StreakLogic.calculateStreak(dates = emptyList(), clock = clock)
        assertEquals(0, result)
    }

    @Test
    fun whenTodayHasEntryThenStreakIsOne() {
        val result = StreakLogic.calculateStreak(dates = listOf(today), clock = clock)
        assertEquals(1, result)
    }

    @Test
    fun whenTodayHasManyEntriesThenStreakIsOne() {
        val dates = listOf(today, today, today)
        val result = StreakLogic.calculateStreak(dates = dates, clock = clock)
        assertEquals(1, result)
    }

    @Test
    fun whenYesterdayHasEntryAndTodayNoneThenStreakIsOne() {
        val yesterday = today.minusDays(1)
        val dates = listOf(yesterday)
        val result = StreakLogic.calculateStreak(dates = dates, clock = clock)
        assertEquals(1, result)
    }

    @Test
    fun whenYesterdayAndTodayHasEntryThenStreakIsTwo() {
        val yesterday = today.minusDays(1)
        val dates = listOf(today, yesterday)
        val result = StreakLogic.calculateStreak(dates = dates, clock = clock)
        assertEquals(2, result)
    }

    @Test
    fun whenTwoDaysAgoAndYesterdayHasEntryThenStreakIsThree() {
        val twoDaysAgo = today.minusDays(2)
        val yesterday = today.minusDays(1)
        val dates = listOf(today, yesterday, twoDaysAgo)
        val result = StreakLogic.calculateStreak(dates = dates, clock = clock)
        assertEquals(3, result)
    }

    @Test
    fun whenTwoDaysAgoAndNotYesterdayAndTodayHasEntryThenStreakIsOne() {
        val twoDaysAgo = today.minusDays(2)
        val dates = listOf(today, twoDaysAgo)
        val result = StreakLogic.calculateStreak(dates = dates, clock = clock)
        assertEquals(1, result)
    }

    @Test
    fun whenTwoDaysAgoAndNotYesterdayAndNotTodayHasEntryThenStreakIsZero() {
        val twoDaysAgo = today.minusDays(2)
        val dates = listOf(twoDaysAgo)
        val result = StreakLogic.calculateStreak(dates = dates, clock = clock)
        assertEquals(0, result)
    }

    @Test
    fun whenTwoDaysAgoAndYesterdayButNotTodayThenStreakIsTwo() {
        val twoDaysAgo = today.minusDays(2)
        val yesterday = today.minusDays(1)
        val dates = listOf(twoDaysAgo, yesterday)
        val result = StreakLogic.calculateStreak(dates = dates, clock = clock)
        assertEquals(2, result)
    }

    @Test
    fun whenOnlyOldEntriesThenStreakIsZero() {
        val threeDaysAgo = today.minusDays(3)
        val fourDaysAgo = today.minusDays(4)
        val dates = listOf(threeDaysAgo, fourDaysAgo)
        val result = StreakLogic.calculateStreak(dates = dates, clock = clock)
        assertEquals(0, result)
    }

    @Test
    fun whenFutureDatesHasEntryThenIsIgnored() {
        val tomorrow = today.plusDays(1)
        val twoDaysLater = today.plusDays(2)
        val dates = listOf(tomorrow, twoDaysLater)
        val result = StreakLogic.calculateStreak(dates = dates, clock = clock)
        assertEquals(0, result)
    }

    @Test
    fun whenLongStreakBrokenThenOnlyRecentStreakCounts() {
        val nineDaysAgo = today.minusDays(9)
        val eightDaysAgo = today.minusDays(8)
        val sevenDaysAgo = today.minusDays(7)
        val sixDaysAgo = today.minusDays(6)
        val fiveDaysAgo = today.minusDays(5)
        val twoDaysAgo = today.minusDays(2)
        val yesterday = today.minusDays(1)
        val dates = listOf(
            nineDaysAgo,
            eightDaysAgo,
            sevenDaysAgo,
            sixDaysAgo,
            fiveDaysAgo,
            twoDaysAgo,
            yesterday,
            today
        )
        val result = StreakLogic.calculateStreak(dates = dates, clock = clock)
        assertEquals(3, result)
    }

    @Test
    fun whenAt2359ThenStreakCountsFromToday() {
        val lateNightClock = Clock.fixed(
            today.atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault()
        )
        val yesterday = today.minusDays(1)
        val dates = listOf(
            yesterday,
            today
        )
        val result = StreakLogic.calculateStreak(dates = dates, clock = lateNightClock)
        assertEquals(2, result)
    }

    @Test
    fun whenAt0001ThenStreakCountsFromYesterday() {
        val nextDayClock = Clock.fixed(
            today.plusDays(1).atTime(0, 1).atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault()
        )
        val yesterday = today.minusDays(1)
        val dates = listOf(
            yesterday,
            today
        )
        val result = StreakLogic.calculateStreak(dates = dates, clock = nextDayClock)
        assertEquals(2, result)
    }
}