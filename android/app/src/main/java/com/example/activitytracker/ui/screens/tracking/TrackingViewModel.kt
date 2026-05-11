package com.example.activitytracker.ui.screens.tracking

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.activitytracker.data.local.entity.ActivityEntity
import com.example.activitytracker.data.local.sync.SyncWorker
import com.example.activitytracker.data.repository.IActivityRepository
import com.example.activitytracker.domain.StreakLogic
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime

class TrackingViewModel(
    application: Application,
    private val repository: IActivityRepository
) : AndroidViewModel(application) {

    var name = MutableLiveData<String>()
    val activityEntity: LiveData<List<ActivityEntity>> = repository.getAll.asLiveData()
    private val context = getApplication<Application>().applicationContext

    // To be connected to the HomeScreen UI
    val dates: LiveData<List<LocalDate>> = repository.getDates.asLiveData()
    val streak = dates.map { StreakLogic.calculateStreak(it) }

    fun saveActivity(activityName: String, activityDate: LocalDate) {
        //If no activity name was given -> log the exception and cancel
        if (activityName.isBlank()) {
            android.util.Log.e("TrackingViewModel", "Name ist leer")
            return
        }

        //Call repository with name, createdAt and userId
        viewModelScope.launch {
            repository.insert(
                ActivityEntity(
                    activityName = activityName.trim(),
                    activityDate = activityDate,
                    createdAt = OffsetDateTime.now(),
                    userId = null
                )
            )
        }

        WorkManager.getInstance(context).enqueueUniqueWork(
            "sync",
            ExistingWorkPolicy.REPLACE,
            SyncWorker.startUpSyncWork()
        )
    }
}


class ActivityEntryModelFactory(
    private val application: Application,
    private val repository: IActivityRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackingViewModel::class.java))
            return TrackingViewModel(application, repository) as T

        throw IllegalArgumentException("Unknown Class for View Model")
    }
}