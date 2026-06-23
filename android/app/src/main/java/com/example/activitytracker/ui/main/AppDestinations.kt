package com.example.activitytracker.ui.main

import com.example.activitytracker.R

enum class AppDestinations(
    val label: String,
    val icon: Int
) {
    FRIENDS("Freunde", R.drawable.ic_friends),
    HOME("Home", R.drawable.round_home_24),
    TRACKING("Tracking", R.drawable.ic_add),
    SETTINGS("Settings", 0),
    REGISTER("Register", 0),
    LOGIN("Login", 0),
    SPLASH("Splash", 0)
}