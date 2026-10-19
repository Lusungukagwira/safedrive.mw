package com.example.safedrivemw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.safedrivemw.ui.screens.DashboardScreen
import com.example.safedrivemw.ui.screens.LoginScreen
import com.example.safedrivemw.ui.screens.RegisterScreen
import com.example.safedrivemw.ui.screens.SplashScreen
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
                                currentScreen = "dashboard"
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
                                currentScreen = "dashboard"
                            },
                            onNavigateToLogin = {
                                currentScreen = "login"
                            }
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
