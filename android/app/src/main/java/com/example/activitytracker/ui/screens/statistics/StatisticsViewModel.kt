package com.example.activitytracker.ui.screens.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.activitytracker.data.repository.IActivityRepository
import com.example.activitytracker.domain.StatisticsLogic
import com.example.activitytracker.domain.StreakLogic
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class StatisticsViewModel(
    private val repository: IActivityRepository,
) : ViewModel() {

    private val allDates: LiveData<List<LocalDate>> = repository.getDates.asLiveData()

    val longestStreak: LiveData<Int> = allDates.map { dates -> StreakLogic.calculateLongestStreak(dates) }

    private val selectedMonth = MutableLiveData(YearMonth.now())

    private val filteredDates: LiveData<List<LocalDate>> =
        selectedMonth.switchMap { month ->
            allDates.map { dates ->
                dates.filter { YearMonth.from(it) == month }
            }
        }

    val averagePerDay: LiveData<Double> =
        filteredDates.map { StatisticsLogic.averagePerDay(it) }

    val mostActiveWeekday: LiveData<DayOfWeek?> =
        filteredDates.map { StatisticsLogic.mostActiveWeekday(it) }

    val sumByWeekday: LiveData<Map<DayOfWeek, Int>> =
        filteredDates.map { StatisticsLogic.sumByWeekday(it) }

    val averageByWeekday: LiveData<Map<DayOfWeek, Double>> =
        selectedMonth.switchMap { month ->
            filteredDates.map { dates ->
                StatisticsLogic.averageByWeekday(dates, month)
            }
        }

    fun selectMonth(month: YearMonth) {
        selectedMonth.value = month
    }
}

class StatisticsViewModelFactory(
    private val repository: IActivityRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}