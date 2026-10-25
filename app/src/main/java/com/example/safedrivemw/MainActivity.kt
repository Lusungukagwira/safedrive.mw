package com.example.safedrivemw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.safedrivemw.ui.screens.*
import com.example.safedrivemw.ui.theme.SafedrivemwTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafedrivemwTheme {
                var currentScreen by remember { mutableStateOf("splash") }

                when (currentScreen) {
                    "splash" -> {
                        SplashScreen(
                            onTimeout = {
                                currentScreen = "login"
                            }
                        )
                    }
                    "login" -> {
                        LoginScreen(
                            onLogin = { _ ->
                                // Handle login logic
                            },
                            onLoggedIn = {
                                currentScreen = "type_selection"
                            },
                            onNavigateToRegister = {
                                currentScreen = "register"
                            }
                        )
                    }
                    "register" -> {
                        RegisterScreen(
                            onRegister = { _, _, _, _, _ ->
                                // Handle registration logic
                            },
                            onRegistered = {
                                currentScreen = "type_selection"
                            },
                            onNavigateToLogin = {
                                currentScreen = "login"
                            }
                        )
                    }
                    "type_selection" -> {
                        ApplicantTypeScreen(
                            onTypeSelected = { type ->
                                when (type) {
                                    ApplicantType.NEW_APPLICANT -> currentScreen = "dashboard"
                                    ApplicantType.RENEW_LICENCE -> currentScreen = "dashboard"
                                }
                            }
                        )
                    }
                    "payment" -> {
                        PaymentScreen(
                            amountMwk = "MWK 20,000",
                            applicationType = "New Driving Licence Application Fee",
                            onBack = { currentScreen = "dashboard" },
                            onPaymentSuccess = { _ ->
                                currentScreen = "payment_history"
                            }
                        )
                    }
                    "payment_history" -> {
                        PaymentHistoryScreen(
                            onBack = { currentScreen = "dashboard" }
                        )
                    }
                    "school_comparison" -> {
                        SchoolComparisonScreen(
                            onBack = { currentScreen = "dashboard" },
                            onRequestSchool = { _ ->
                                currentScreen = "dashboard"
                            }
                        )
                    }
                    "profile" -> {
                        ProfileScreen(
                            onBack = { currentScreen = "dashboard" },
                            onNavigateToPaymentHistory = { currentScreen = "payment_history" },
                            onLogout = { currentScreen = "login" }
                        )
                    }
                    else -> {
                        DashboardScreen(
                            onLogout = {
                                currentScreen = "login"
                            }
                        )
                    }
                }
            }
        }
    }
}
