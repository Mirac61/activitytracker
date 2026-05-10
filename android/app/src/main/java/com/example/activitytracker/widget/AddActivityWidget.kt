package com.example.activitytracker.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.action.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.size
import com.example.activitytracker.MainActivity
import com.example.activitytracker.R


// Based on Android official documentation:
// https://developer.android.com/develop/ui/views/appwidgets

class AddActivityWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            AddActivityWidgetContent()
        }
    }


}


@Composable
private fun AddActivityWidgetContent() {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF5C7A50))
            .cornerRadius(12.dp)
            .clickable(
                actionStartActivity<MainActivity>(
                    parameters = actionParametersOf(
                        ActionParameters.Key<Boolean>("openAdd") to true
                    )
                )
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_widget_add),
            contentDescription = "Add Activity",
            modifier = GlanceModifier.size(48.dp)
        )
    }
}