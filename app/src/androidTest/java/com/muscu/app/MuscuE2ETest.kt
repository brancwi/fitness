package com.muscu.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MuscuE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun dashboardIsDisplayed() {
        composeTestRule.onNodeWithTag("dashboard_title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Prochaine séance").assertIsDisplayed()

    }

    @Test
    fun navigateToProgramAndSeeWorkoutDays() {
        composeTestRule.onNodeWithText("Programme").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Mardi").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("program_title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mardi").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jeudi").assertIsDisplayed()
        composeTestRule.onNodeWithText("Samedi").assertIsDisplayed()
    }

    @Test
    fun startWorkoutFromDashboard() {
        composeTestRule.onNodeWithTag("start_workout_button").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("workout_progress").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("workout_progress").assertIsDisplayed()
    }

    @Test
    fun navigateToMeasurements() {
        composeTestRule.onNodeWithText("Mensurations").performClick()
        composeTestRule.onNodeWithTag("measurements_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("body_diagram_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("measurements_form_title").assertIsDisplayed()
    }

    @Test
    fun navigateToSettings() {
        composeTestRule.onNodeWithText("Réglages").performClick()
        composeTestRule.onNodeWithText("Profil").assertIsDisplayed()
        composeTestRule.onNodeWithTag("weightInput").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enregistrer").assertIsDisplayed()
    }
}
