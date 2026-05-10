package com.example.activitytracker.ui.main

import com.example.activitytracker.R

enum class AppDestinations(
    val label: String,
    val icon: Int
) {
    FRIENDS("Freunde", R.drawable.ic_friends),
    HOME("Home", R.drawable.round_home_24),
    TRACKING("Tracking", R.drawable.ic_add),

    //placeholder to test Registration screen until full implementation
    REGISTER("Register", R.drawable.ic_google_logo)
}