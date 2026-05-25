package com.example.activitytracker.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.activitytracker.MainActivity
import com.example.activitytracker.R
import com.example.activitytracker.domain.StreakLogic
import java.time.LocalDate
import com.example.activitytracker.data.local.AppDatabase
import kotlinx.coroutines.flow.first
import androidx.datastore.preferences.core.Preferences
import androidx.glance.layout.Box


class StreakWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition
    companion object {
        val DATA_KEY_DATES = stringSetPreferencesKey("widget_active_dates")
        val SIZE_1X1 = DpSize(60.dp, 60.dp)
        val SIZE_2X1 = DpSize(130.dp, 60.dp)
        val SIZE_3X1 = DpSize(200.dp, 60.dp)
        val SIZE_4X1 = DpSize(270.dp, 60.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(SIZE_1X1, SIZE_2X1, SIZE_3X1, SIZE_4X1)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val fallbackDates = AppDatabase
            .getInstance(context)
            .activityDao()
            .getDates()
            .first()
            .map { it.toString() }
            .toSet()

        provideContent {
            val prefs = currentState<Preferences>()

            val dateStrings = prefs[DATA_KEY_DATES]
                ?.takeIf { it.isNotEmpty() }
                ?: fallbackDates

            val activeDays = dateStrings.map { LocalDate.parse(it) }.toSet()
            val streak = StreakLogic.calculateStreak(activeDays.toList())

            StreakWidgetContent(
                streak = streak,
                activeDays = activeDays
            )
        }
    }
}

@Composable
private fun StreakWidgetContent(
    streak: Int,
    activeDays: Set<LocalDate>
) {
    val size = LocalSize.current

    val visibleDays = StreakWidgetLogic.getVisibleDays(size.width.value.toInt())

    val visibleLogo = when (visibleDays) {
        1 -> 20.dp
        3 -> 22.dp
        5 -> 24.dp
        else -> 26.dp
    }
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFFEDEDED))
            .cornerRadius(12.dp)
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .clickable(
                actionStartActivity<MainActivity>(
                    parameters = actionParametersOf(
                        ActionParameters.Key<Boolean>(
                            AddActivityWidgetIntentHandler.EXTRA_OPEN_ADD
                        ) to true
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$streak",
                style = TextStyle(
                    fontSize = when (visibleDays) {
                        1 -> 16.sp
                        3 -> 18.sp
                        5 -> 18.sp
                        else -> 18.sp
                    },
                )
            )
            Spacer(modifier = GlanceModifier.size(8.dp))
            Image(
                provider = ImageProvider(R.drawable.ic_running_man),
                contentDescription = "App Logo",
                modifier = GlanceModifier.size(visibleLogo)
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        StreakDaysRow(
            activeDays = activeDays,
            visibleDays = visibleDays
        )
    }
}


@Composable
private fun StreakDaysRow(
    activeDays: Set<LocalDate>,
    visibleDays: Int
) {
    val today = LocalDate.now()

    val days = StreakWidgetLogic.getDisplayedDays(today, visibleDays)

    val labels = mapOf(
        1 to "Mo",
        2 to "Di",
        3 to "Mi",
        4 to "Do",
        5 to "Fr",
        6 to "Sa",
        7 to "So"
    )

    val dayBoxSize = when (visibleDays) {
        1 -> 42.dp
        3 -> 38.dp
        5 -> 36.dp
        else -> 34.dp
    }

    val dayIconSize = when (visibleDays) {
        1 -> 30.dp
        3 -> 30.dp
        5 -> 28.dp
        else -> 26.dp
    }

    val horizontalPadding = when (visibleDays) {
        1 -> 0.dp
        3 -> 4.dp
        5 -> 1.dp
        else -> 1.dp
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        days.forEach { day ->
            val hasActivity = activeDays.contains(day)
            val isToday = day == today

            Column(
                modifier = GlanceModifier.padding(horizontal = horizontalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(dayBoxSize)
                        .then(
                            if (isToday) {
                                GlanceModifier
                                    .background(Color(0x33FF7043))
                                    .cornerRadius(dayBoxSize / 2)
                            } else {
                                GlanceModifier
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(
                            if (hasActivity) {
                                R.drawable.ic_streak_on
                            } else {
                                R.drawable.ic_streak_off
                            }
                        ),
                        contentDescription = null,
                        modifier = GlanceModifier.size(dayIconSize)
                    )
                }

                Spacer(modifier = GlanceModifier.height(4.dp))

                Text(
                    text = labels[day.dayOfWeek.value] ?: "",
                    style = TextStyle(
                        fontSize = when (visibleDays) {
                            1 -> 12.sp
                            3 -> 12.sp
                            5 -> 12.sp
                            else -> 13.sp
                        }
                    )
                )
            }
        }
    }
}