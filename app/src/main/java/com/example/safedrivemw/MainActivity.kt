package com.example.safedrivemw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.safedrivemw.data.local.AppRoomDatabase
import com.example.safedrivemw.data.sync.DataSyncWorker
import com.example.safedrivemw.data.sync.NetworkConnectivityObserver
import com.example.safedrivemw.data.sync.SyncState
import com.example.safedrivemw.ui.components.SyncStatusBanner
import com.example.safedrivemw.ui.screens.*
import com.example.safedrivemw.ui.theme.SafedrivemwTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Trigger WorkManager synchronization check on app launch
        try {
            DataSyncWorker.scheduleSync(this)
        } catch (_: Throwable) {}

        setContent {
            SafedrivemwTheme {
                val context = LocalContext.current
                val networkObserver = remember { NetworkConnectivityObserver(context) }
                val syncState by networkObserver.status.collectAsState(
                    initial = if (networkObserver.isCurrentlyConnected()) SyncState.ONLINE else SyncState.OFFLINE
                )
                val roomDb = remember { AppRoomDatabase.getInstance(context) }
                var pendingCount by remember { mutableIntStateOf(0) }

                LaunchedEffect(syncState) {
                    withContext(Dispatchers.IO) {
                        try {
                            val countRecords = roomDb.pendingSyncDao().getPendingCount()
                            val countDocs = roomDb.offlineDocumentDao().getPendingDocsCount()
                            pendingCount = countRecords + countDocs
                        } catch (_: Exception) {
                            pendingCount = 0
                        }
                    }
                }

                var currentScreen by remember { mutableStateOf("splash") }
                var userName by remember { mutableStateOf("Lusungu Kagwira") }
                var userEmail by remember { mutableStateOf("lusungu.kagwira@safedrive.mw") }
                var userPhone by remember { mutableStateOf("+265 999 123 456") }
                var userNationalId by remember { mutableStateOf("NRB-987654321-MW") }
                var userDob by remember { mutableStateOf("15 August 2002") }
                var userRole by remember { mutableStateOf("Driver") }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Global Sync Status Banner connected to real network & pending Room records
                    if (currentScreen != "splash") {
                        SyncStatusBanner(
                            syncState = syncState,
                            pendingCount = pendingCount
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
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
                                    onLogin = { email, role, name, phone, dob ->
                                        userEmail = email
                                        userRole = role
                                        userName = name.ifBlank { "Welcome!" }
                                        if (phone.isNotBlank()) userPhone = phone
                                        if (dob.isNotBlank()) userDob = dob
                                    },
                                    onLoggedIn = { role ->
                                        currentScreen = when (role) {
                                            "RTD Officer" -> "rtd_portal"
                                            "Driving School" -> "school_portal"
                                            else -> "dashboard"
                                        }
                                    },
                                    onNavigateToRegister = {
                                        currentScreen = "register"
                                    }
                                )
                            }
                            "register" -> {
                                RegisterScreen(
                                    onRegister = { name, email, phone, _, dob ->
                                        userName = name.ifBlank { "Welcome!" }
                                        userEmail = email
                                        userPhone = phone
                                        userDob = dob
                                    },
                                    onRegistered = {
                                        currentScreen = "login"
                                    },
                                    onNavigateToLogin = {
                                        currentScreen = "login"
                                    }
                                )
                            }
                            "rtd_portal" -> {
                                RTDOfficerPortalScreen(
                                    onLogout = { currentScreen = "login" }
                                )
                            }
                            "school_portal" -> {
                                DrivingSchoolPortalScreen(
                                    onLogout = { currentScreen = "login" }
                                )
                            }
                            "tracking" -> {
                                TrackingScreen(
                                    onBack = { currentScreen = "dashboard" }
                                )
                            }
                            "payment" -> {
                                PaymentScreen(
                                    amountMwk = "MWK 20,000",
                                    applicationType = "New Driving Licence Application Fee",
                                    userEmail = userEmail,
                                    userPhone = userPhone,
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
                                    userName = userName,
                                    userEmail = userEmail,
                                    userPhone = userPhone,
                                    userNationalId = userNationalId,
                                    userDob = userDob,
                                    userRole = userRole,
                                    onBack = { currentScreen = "dashboard" },
                                    onNavigateToPaymentHistory = { currentScreen = "payment_history" },
                                    onLogout = { currentScreen = "login" }
                                )
                            }
                            else -> {
                                DashboardScreen(
                                    userName = userName,
                                    userEmail = userEmail,
                                    userPhone = userPhone,
                                    userNationalId = userNationalId,
                                    userDob = userDob,
                                    userRole = userRole,
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
    }
}
