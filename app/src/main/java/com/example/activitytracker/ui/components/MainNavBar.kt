package com.example.activitytracker.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.activitytracker.ui.main.AppDestinations

@Composable
fun MainNavBar(
    currentDestination: AppDestinations,
    onNavigate: (AppDestinations) -> Unit
) {
    NavigationBar {
        AppDestinations.entries.forEach { destination ->
            NavigationBarItem(
                selected = destination == currentDestination,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        painter = painterResource(destination.icon),
                        contentDescription = destination.label
                    )
                }
            )
        }
    }
}