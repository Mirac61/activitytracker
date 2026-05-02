package com.example.activitytracker.ui.screens.tracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.activitytracker.data.local.entity.ActivityEntity
import com.example.activitytracker.data.repository.ActivityRepository
import com.example.activitytracker.data.repository.IActivityRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime

class TrackingViewModel (private val repository: IActivityRepository) : ViewModel(){

    var name  = MutableLiveData<String>()

    //val activityEntity: LiveData<List<ActivityEntity>> = repository.getAll.asLiveData() für zukünftige Tickets

    fun saveActivity(activityName: String, activityDate: LocalDate) {
        //If no activity name was given -> log the exception and cancel
        if (activityName.isBlank()){
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
    }
}


class ActivityEntryModelFactory(private val repository: IActivityRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackingViewModel::class.java))
            return TrackingViewModel(repository) as T

        throw IllegalArgumentException("Unknown Class for View Model")
    }
}