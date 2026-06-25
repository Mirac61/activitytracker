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
    onNavigate: (AppDestinations) -> Unit,
    onPlusClicked: () -> Unit
) {
    NavigationBar {
        AppDestinations.entries
            .filter { it != AppDestinations.LOGIN
                    && it != AppDestinations.REGISTER
                    && it != AppDestinations.SPLASH
                    && it != AppDestinations.SETTINGS }
            .forEach { destination ->
                NavigationBarItem(
                    selected = destination == currentDestination,
                    onClick = {
                        if (destination == AppDestinations.TRACKING) {
                            onPlusClicked()
                        } else {
                            onNavigate(destination)
                        }
                    },
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