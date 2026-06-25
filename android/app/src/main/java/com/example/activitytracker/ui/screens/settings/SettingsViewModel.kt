package com.example.activitytracker.ui.screens.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.activitytracker.data.local.entity.ReminderEntity
import com.example.activitytracker.data.repository.ReminderRepository
import com.example.activitytracker.notification.AlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class SettingsViewModel(
    application: Application,
    private val repository: ReminderRepository
) : AndroidViewModel(application) {

    private val scheduler = AlarmScheduler(application)

    val reminders: StateFlow<List<ReminderEntity>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            val prefs = application.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val seeded = prefs.getBoolean("default_reminder_seeded", false)
            if (!seeded) {
                val default = ReminderEntity(
                    dayOfWeek = Calendar.MONDAY,
                    hour = 12,
                    minute = 0,
                    title = "Zeit aktiv zu sein",
                    text = "Vergiss deine Ziele heute nicht!",
                    isDaily = true
                )
                val requestCode = repository.insert(default).toInt()
                scheduler.cancelLegacyAlarm()
                scheduler.scheduleReminder(default.copy(requestCode = requestCode))
                prefs.edit().putBoolean("default_reminder_seeded", true).apply()
            }
        }
    }

    fun addReminder(dayOfWeek: Int, hour: Int, minute: Int, title: String, text: String, isDaily: Boolean = false) {
        viewModelScope.launch {
            val reminder = ReminderEntity(dayOfWeek = dayOfWeek, hour = hour, minute = minute, title = title, text = text, isDaily = isDaily)
            val requestCode = repository.insert(reminder).toInt()
            scheduler.scheduleReminder(reminder.copy(requestCode = requestCode))
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.delete(reminder)
            scheduler.cancelReminder(reminder)
        }
    }
}

class SettingsViewModelFactory(
    private val application: Application,
    private val repository: ReminderRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(application, repository) as T
    }
}
