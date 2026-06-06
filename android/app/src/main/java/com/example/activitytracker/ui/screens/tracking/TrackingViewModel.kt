package com.example.activitytracker.ui.screens.tracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import android.content.Context
import android.util.Log
import com.example.activitytracker.data.local.entity.ActivityEntity
import com.example.activitytracker.data.local.sync.SyncWorker
import com.example.activitytracker.data.repository.IActivityRepository
import com.example.activitytracker.domain.StreakLogic
import com.example.activitytracker.widget.ActivityWidgetUpdater
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime


class TrackingViewModel(
    private val repository: IActivityRepository,
    private val appContext: Context
) : ViewModel() {

    var name = MutableLiveData<String>()

    val activityEntity: LiveData<List<ActivityEntity>> = repository.getAll.asLiveData()

    val listOfActivityNames: LiveData<List<String>> = repository.getActivityNames.asLiveData()

    val dates: LiveData<List<LocalDate>> = repository.getDates.asLiveData()

    val streak: LiveData<Int> = dates.map { StreakLogic.calculateStreak(it) }

    fun saveActivity(activityName: String, activityDate: LocalDate) {
        if (activityName.isBlank()) {
            Log.e("TrackingViewModel", "Empty name")
            return
        }

        viewModelScope.launch {
            repository.insert(
                ActivityEntity(
                    activityName = activityName.trim(),
                    activityDate = activityDate,
                    createdAt = OffsetDateTime.now()
                    )
            )
            val currentDates = repository.getDates.first()
            ActivityWidgetUpdater.updateAllWidgets(appContext, currentDates)
            Log.d("TRACKER_WIDGET", "Saved for: $activityDate")

            WorkManager.getInstance(appContext).enqueueUniqueWork(
                "sync",
                ExistingWorkPolicy.KEEP,
                SyncWorker.buildSyncRequest()
            )
        }
    }

    fun updateActivity(activity: ActivityEntity) {
        if (activity.activityName.isBlank()) {
            Log.e("TrackingViewModel", "Empty name")
            return
        }

        viewModelScope.launch {
            repository.update(activity)


            val currentDates = repository.getDates.first()
            ActivityWidgetUpdater.updateAllWidgets(appContext, currentDates)

            WorkManager.getInstance(appContext).enqueueUniqueWork(
                "sync",
                ExistingWorkPolicy.KEEP,
                SyncWorker.buildSyncRequest()
            )

            Log.d(
                "TrackingViewModel",
                "Activity ${activity.id} got updated"
            )
        }
    }
}

class ActivityEntryModelFactory(
    private val repository: IActivityRepository,
    private val appContext: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackingViewModel(repository, appContext) as T
        }

        throw IllegalArgumentException("Unknown Class for View Model")
    }
}