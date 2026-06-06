package com.example.activitytracker.premission

import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest


class PermissionHandler(
    private val activity: ComponentActivity,
    private val onAllGranted: () -> Unit
) {
    private val locationRequest = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
            onAllGranted()
        } else {
            Toast.makeText(activity, "Standort wird benötigt", Toast.LENGTH_SHORT).show()
        }
    }

    private val notificationRequest = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(activity, "Benachrichtigungen deaktiviert", Toast.LENGTH_SHORT).show()
        }
        locationRequest.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            locationRequest.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }
}