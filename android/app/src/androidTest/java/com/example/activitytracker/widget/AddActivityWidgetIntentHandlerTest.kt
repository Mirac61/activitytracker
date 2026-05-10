package com.example.activitytracker.widget

import android.content.Intent
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AddActivityWidgetIntentHandlerTest {

    @Test
    fun shouldOpenAddActivity_returnsTrue_whenOpenAddIsTrue() {
        val intent = Intent().putExtra(AddActivityWidgetIntentHandler.EXTRA_OPEN_ADD, true)

        val result = AddActivityWidgetIntentHandler.shouldOpenAddActivity(intent)

        assertTrue(result)
    }

    @Test
    fun shouldOpenAddActivity_returnsFalse_whenExtraIsMissing() {
        val intent = Intent()

        val result = AddActivityWidgetIntentHandler.shouldOpenAddActivity(intent)

        assertFalse(result)
    }

    @Test
    fun shouldOpenAddActivity_returnsFalse_whenIntentIsNull() {
        val result = AddActivityWidgetIntentHandler.shouldOpenAddActivity(null)

        assertFalse(result)
    }
}