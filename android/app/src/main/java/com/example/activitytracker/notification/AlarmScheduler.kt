package com.example.activitytracker.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.activitytracker.data.local.entity.ReminderEntity
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    fun scheduleReminder(reminder: ReminderEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextOccurrence(reminder),
            buildPendingIntent(reminder.requestCode)
        )
    }

    fun cancelReminder(reminder: ReminderEntity) {
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            .cancel(buildPendingIntent(reminder.requestCode))
    }

    fun cancelLegacyAlarm() { // deletes old static alarm from previous version
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            .cancel(buildPendingIntent(0))
    }

    private fun buildPendingIntent(requestCode: Int): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("reminder_request_code", requestCode)
        }
        return PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun nextOccurrence(reminder: ReminderEntity): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, reminder.hour)
            set(Calendar.MINUTE, reminder.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (!reminder.isDaily) set(Calendar.DAY_OF_WEEK, reminder.dayOfWeek)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(if (reminder.isDaily) Calendar.DAY_OF_MONTH else Calendar.WEEK_OF_YEAR, 1)
            }
        }
        return calendar.timeInMillis
    }
}
