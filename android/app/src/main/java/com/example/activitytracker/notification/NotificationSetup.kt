package com.example.activitytracker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.activitytracker.MainActivity
import com.example.activitytracker.R
import android.Manifest

const val CHANNEL_REMINDER_ID = "activity_tracker_channel"
const val CHANNEL_WEATHER_ID = "weather_api_channel"
const val NOTIFICATION_ID_WEATHER = 1

// contains the Android Notification setup
class NotificationSetup(private val context: Context) {

    fun createNotificationChannel() {
            val channel = NotificationChannel(
                CHANNEL_REMINDER_ID,
                "Erinnerung",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)

            val weatherChannel = NotificationChannel(
                CHANNEL_WEATHER_ID, "Wetterbedingte Benachrichtigung", NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(weatherChannel)
    }
    fun sendWeatherNotification() {
        showNotification(NOTIFICATION_ID_WEATHER, CHANNEL_WEATHER_ID, "Die Sonne scheint!", "Perfekte Voraussetzung für einen Spaziergang")
    }

    fun showNotification(id: Int, channelId: String, title: String, text: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) return
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_running_man)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notification)
    }
}