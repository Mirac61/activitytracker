package com.example.activitytracker.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import java.time.LocalDate

object ActivityWidgetUpdater {

    suspend fun updateAllWidgets(context: Context, dates: List<LocalDate>) {
        val appContext = context.applicationContext
        val dateStrings = dates.map { it.toString() }.toSet()

        val manager = GlanceAppWidgetManager(appContext)
        val glanceIds = manager.getGlanceIds(StreakWidget::class.java)

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = appContext,
                definition = PreferencesGlanceStateDefinition,
                glanceId = glanceId
            ) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[StreakWidget.DATA_KEY_DATES] = dateStrings
                }
            }

            StreakWidget().update(appContext, glanceId)
        }
    }
}