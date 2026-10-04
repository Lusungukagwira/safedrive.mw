package com.example.safedrivemw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.safedrivemw.ui.screens.LoginScreen
import com.example.safedrivemw.ui.theme.SafedrivemwTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafedrivemwTheme {
                // Track current screen state for a smooth transition upon login
                var isLoggedIn by remember { mutableStateOf(false) }

                if (!isLoggedIn) {
                    LoginScreen(
                        onLogin = { email, role ->
                            // Handle authentication logic or viewmodel call here
                        },
                        onLoggedIn = {
                            isLoggedIn = true
                        }
                    )
                } else {
                    // Placeholder for your DashboardScreen once logged in
                    // DashboardScreen(user = ..., onLogout = { isLoggedIn = false })
                }
            }
        }
    }
}