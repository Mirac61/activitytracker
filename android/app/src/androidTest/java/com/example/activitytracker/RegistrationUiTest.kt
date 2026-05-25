package com.example.activitytracker

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.activitytracker.ui.screens.register.RegistrationScreen
import org.junit.Rule
import org.junit.Test

class RegistrationUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testInvalidEmailShowsError() {
        //Load registration screen
        composeTestRule.setContent {
            RegistrationScreen(onRegistrationComplete = {})
        }

        //Input invalid email
        composeTestRule.onNodeWithTag("E-Mail")
            .performTextInput("falsche-email")

        //Input password
        composeTestRule.onNodeWithTag("Passwort")
            .performTextInput("password123")

        //Test if validation message popped up
        composeTestRule.onNodeWithText("Ungültige Emailadresse")
            .assertIsDisplayed()

        //Test if the button is deactivated
        composeTestRule.onNodeWithText("Registrieren")
            .assertIsNotEnabled()
    }
}