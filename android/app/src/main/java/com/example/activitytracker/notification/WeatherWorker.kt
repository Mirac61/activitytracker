package com.example.activitytracker.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.activitytracker.data.remote.RetrofitClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.time.LocalDate
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class WeatherWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
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
        val location: Location? = try {
            client.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            ).await()
        } catch (e: Exception) { null }

        if (location == null) return Result.retry()

        val response = withContext(Dispatchers.IO) {
            RetrofitClient.weatherApi.getWeather(location.latitude, location.longitude).execute()
        }

            if (response.isSuccessful) {
                val weather = response.body()?.string() ?: ""

                if (weather.contains("Sunny", ignoreCase = true)) {
                    val helper = NotificationSetup(applicationContext)
                    helper.sendWeatherNotification()

                    prefs.edit { putString("last_success_date", today) }
                }
            }

        return Result.success()
    }
}