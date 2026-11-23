package com.example.safedrivemw

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.safedrivemw.ui.screens.*
import com.example.safedrivemw.ui.theme.SafedrivemwTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SafeDriveUiTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysHeaderAndFields() {
        composeTestRule.setContent {
            SafedrivemwTheme {
                LoginScreen()
            }
        }

        // Verify branding and input fields display
        composeTestRule.onNodeWithText("SafeDrive.mw").assertExists()
        composeTestRule.onNodeWithText("Welcome back").assertExists()
        composeTestRule.onNodeWithText("Email address").assertExists()
        composeTestRule.onNodeWithText("Password").assertExists()
        composeTestRule.onAllNodesWithText("Sign In").onFirst().assertExists()
    }

    @Test
    fun registerScreen_displaysFieldsAndEligibility() {
        composeTestRule.setContent {
            SafedrivemwTheme {
                RegisterScreen()
            }
        }

        // Verify registration screen fields display
        composeTestRule.onNodeWithText("Create an account").assertExists()
        composeTestRule.onNodeWithText("Full Name").assertExists()
        composeTestRule.onNodeWithText("Email address").assertExists()
        composeTestRule.onNodeWithText("Phone Number").assertExists()
        composeTestRule.onNodeWithText("Date of Birth (DD/MM/YYYY)").assertExists()
        composeTestRule.onAllNodesWithText("Register").onFirst().assertExists()
    }

    @Test
    fun trackingScreen_displaysApplicationStatus() {
        composeTestRule.setContent {
            SafedrivemwTheme {
                TrackingScreen()
            }
        }

        // Verify tracking screen displays header and search
        composeTestRule.onNodeWithText("Application Status & Tracking").assertExists()
        composeTestRule.onNodeWithText("Active Applications").assertExists()
    }

    @Test
    fun paymentScreen_displaysSummaryAndMethods() {
        composeTestRule.setContent {
            SafedrivemwTheme {
                PaymentScreen(amountMwk = "MWK 20,000", applicationType = "New Licence Statutory Fee")
            }
        }

        // Verify payment checkout UI displays
        composeTestRule.onNodeWithText("MWK 20,000").assertExists()
        composeTestRule.onNodeWithText("Airtel Money").assertExists()
        composeTestRule.onNodeWithText("TNM Mpamba").assertExists()
        composeTestRule.onNodeWithText("Digital Banking").assertExists()
    }

    @Test
    fun splashScreen_displaysBranding() {
        composeTestRule.setContent {
            SafedrivemwTheme {
                SplashScreen()
            }
        }

        // Verify splash screen displays branding
        composeTestRule.onNodeWithText("SafeDrive.mw").assertExists()
        composeTestRule.onNodeWithText("Directorate of Road Traffic & Safety Services").assertExists()
    }
}
