package com.example.activitytracker.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.activitytracker.data.remote.RetrofitClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import java.time.LocalDate

class WeatherWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()

        if (prefs.getString("last_success_date", "") == today) {
            return Result.success()
        }

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }

        val client = LocationServices.getFusedLocationProviderClient(applicationContext)
        val location = try { Tasks.await(client.lastLocation) } catch (e: Exception) { null }

        if (location != null) {
            val response = RetrofitClient.weatherApi.getWeather(location.latitude, location.longitude).execute()

            if (response.isSuccessful) {
                val weather = response.body()?.string() ?: ""

                if (weather.contains("Sunny", ignoreCase = true)) {
                    val helper = NotificationHelper(applicationContext)
                    helper.sendWeatherNotification()

                    prefs.edit { putString("last_success_date", today) }
                }
            }
        }
        if (location == null) return Result.retry()

        return Result.success()
    }
}