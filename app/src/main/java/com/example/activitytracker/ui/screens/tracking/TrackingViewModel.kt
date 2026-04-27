package com.example.activitytracker.ui.screens.tracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.activitytracker.data.local.dao.ActivityDao
import com.example.activitytracker.data.local.entity.ActivityEntry
import com.example.activitytracker.data.repository.ActivityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TrackingViewModel (private val repository: ActivityRepository) : ViewModel(){

    var name  = MutableLiveData<String>()

    val activityEntry: LiveData<List<ActivityEntry>> = repository.getAll.asLiveData()

    fun saveActivity(name: String) {
        if (name.isBlank()) return

        viewModelScope.launch {
            repository.insert(
                ActivityEntry(
                    name = name.trim(),
                    createdAt = System.currentTimeMillis(),
                    userId = null
                )
            )
        }
    }
}


class ActivityEntryModelFactory(private val repository: ActivityRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackingViewModel::class.java))
            return TrackingViewModel(repository) as T

        throw IllegalArgumentException("Unknown Class for View Model")
    }
}