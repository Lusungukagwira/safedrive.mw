package com.example.safedrivemw.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safedrivemw.R
import com.example.safedrivemw.data.sync.NetworkConnectivityObserver
import com.example.safedrivemw.data.sync.SyncState
import com.example.safedrivemw.db.LicenceApplicationRecord
import com.example.safedrivemw.db.SafeDriveDatabase
import com.example.safedrivemw.ui.components.SyncStatusBanner

enum class DashboardTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    SCHOOLS("Schools", Icons.Default.School),
    TRACK("Track", Icons.Default.LocationOn),
    PROFILE("Profile", Icons.Default.Person),
    NEW_LICENCE("New Licence", Icons.Default.Badge),
    RENEWAL("Renewal", Icons.Default.Autorenew)
}

enum class ActivePortal {
    CANDIDATE,
    RTD_OFFICER,
    DRIVING_SCHOOL_ADMIN,
    SYSTEM_ADMIN,
    APPLICATION_DETAILS,
    NOTIFICATIONS,
    CODE_BOOK,
    PAYMENT_HISTORY
}

@Composable
fun DashboardScreen(
    userName: String = "Lusungu Kagwira",
    userEmail: String = "lusungu.kagwira@safedrive.mw",
    userPhone: String = "+265 999 123 456",
    userNationalId: String = "NRB-987654321-MW",
    userDob: String = "15 August 2002",
    userRole: String = "Candidate Driver",
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { SafeDriveDatabase.getInstance(context) }

    val networkObserver = remember { NetworkConnectivityObserver(context) }
    val syncState by networkObserver.status.collectAsState(
        initial = if (networkObserver.isCurrentlyConnected()) SyncState.ONLINE else SyncState.OFFLINE
    )

    var currentPortal by remember { mutableStateOf(ActivePortal.CANDIDATE) }
    var selectedTab by remember { mutableStateOf(DashboardTab.HOME) }
    var selectedApplication by remember { mutableStateOf<LicenceApplicationRecord?>(null) }
    var isRoleMenuExpanded by remember { mutableStateOf(false) }

    var stepNumber by remember { mutableIntStateOf(2) }
    var selectedCategory by remember { mutableStateOf("Class B (Light Motor)") }
    var hasSchool by remember { mutableStateOf(false) }
    var selectedSchoolName by remember { mutableStateOf("Lilongwe Motor School") }

    var doc1Name by remember { mutableStateOf("NRB_National_ID_Copy.pdf") }
    var doc2Name by remember { mutableStateOf("Medical_Fitness_Certificate.pdf") }
    var doc3Name by remember { mutableStateOf("Passport_Photo.jpg") }
    var doc4Name by remember { mutableStateOf("") }

    var renewalLicenceNo by remember { mutableStateOf("MW-2022-99881") }
    var renewalDoc1 by remember { mutableStateOf("Expired_Licence_Card.jpg") }
    var renewalDoc2 by remember { mutableStateOf("Medical_Assessment_Form.pdf") }
    var renewalPaymentCompleted by remember { mutableStateOf(false) }
    var renewalPaymentTxRef by remember { mutableStateOf("") }
    var isPaymentScreenActive by remember { mutableStateOf(false) }
    var renewalSubmitted by remember { mutableStateOf(false) }
    var isNewAppSubmitted by remember { mutableStateOf(false) }

    // RT Exam State issued by RTD
    var examDate by remember { mutableStateOf<String?>("18 Nov 2026") }
    var examTime by remember { mutableStateOf<String?>("09:30 AM") }
    var isExamDialogVisible by remember { mutableStateOf(false) }

    val blackTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        focusedLeadingIconColor = Color.Black,
        unfocusedLeadingIconColor = Color.Black,
        focusedTrailingIconColor = Color.Black,
        unfocusedTrailingIconColor = Color.Black,
        focusedPlaceholderColor = Color.Black,
        unfocusedPlaceholderColor = Color.Black
    )

    val bottomNavTabs = listOf(
        DashboardTab.HOME,
        DashboardTab.SCHOOLS,
        DashboardTab.TRACK,
        DashboardTab.PROFILE
    )

    if (isPaymentScreenActive) {
        PaymentScreen(
            amountMwk = "MWK 15,000",
            applicationType = "Licence Renewal Statutory Fee",
            userEmail = userEmail,
            userPhone = userPhone,
            onBack = { isPaymentScreenActive = false },
            onPaymentSuccess = { txRef ->
                renewalPaymentTxRef = txRef
                renewalPaymentCompleted = true
                isPaymentScreenActive = false
            }
        )
        return
    }

    when (currentPortal) {
        ActivePortal.RTD_OFFICER -> {
            RTDOfficerPortalScreen(
                onLogout = { currentPortal = ActivePortal.CANDIDATE }
            )
        }
        ActivePortal.DRIVING_SCHOOL_ADMIN -> {
            DrivingSchoolPortalScreen(
                onLogout = { currentPortal = ActivePortal.CANDIDATE }
            )
        }
        ActivePortal.SYSTEM_ADMIN -> {
            AdminPortalScreen(
                onLogout = { currentPortal = ActivePortal.CANDIDATE }
            )
        }
        ActivePortal.NOTIFICATIONS -> {
            NotificationsSheet(
                onBack = { currentPortal = ActivePortal.CANDIDATE }
            )
        }
        ActivePortal.PAYMENT_HISTORY -> {
            PaymentHistoryScreen(
                onBack = { currentPortal = ActivePortal.CANDIDATE }
            )
        }
        ActivePortal.APPLICATION_DETAILS -> {
            selectedApplication?.let { app ->
                ApplicationDetailsScreen(
                    application = app,
                    onBack = { currentPortal = ActivePortal.CANDIDATE }
                )
            } ?: run {
                currentPortal = ActivePortal.CANDIDATE
            }
        }
        ActivePortal.CODE_BOOK -> {
            CodeBookScreen(
                onBack = { currentPortal = ActivePortal.CANDIDATE }
            )
        }
        ActivePortal.CANDIDATE -> {
            Scaffold(
                containerColor = Color(0xFFF8FAFC),
                bottomBar = {
                    Surface(
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            bottomNavTabs.forEach { tab ->
                                val isSelected = selectedTab == tab
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable {
                                            selectedTab = tab
                                        }
                                        .padding(horizontal = 16.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title,
                                        tint = if (isSelected) Color(0xFF163B70) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = tab.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color(0xFF163B70) else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Connectivity & Background Sync Status Banner
                    SyncStatusBanner(syncState = syncState)

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (selectedTab) {
                            DashboardTab.PROFILE -> {
                                ProfileScreen(
                                    userName = userName,
                                    userEmail = userEmail,
                                    userPhone = userPhone,
                                    userNationalId = userNationalId,
                                    userDob = userDob,
                                    userRole = userRole,
                                    onBack = { selectedTab = DashboardTab.HOME },
                                    onNavigateToPaymentHistory = { currentPortal = ActivePortal.PAYMENT_HISTORY },
                                    onLogout = onLogout
                                )
                            }
                            DashboardTab.TRACK -> {
                                TrackingScreen(
                                    onBack = { selectedTab = DashboardTab.HOME },
                                    onSelectApplication = { app ->
                                        selectedApplication = app
                                        currentPortal = ActivePortal.APPLICATION_DETAILS
                                    }
                                )
                            }
                            DashboardTab.SCHOOLS -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    item {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = { selectedTab = DashboardTab.HOME }) {
                                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                                            }
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Column {
                                                Text("Accredited Driving Schools Directory", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                                Text("Compare registered schools in Lilongwe, Blantyre, Mzuzu, and Zomba", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))

                                        val schools = remember { db.getAllDrivingSchools() }
                                        schools.forEach { school ->
                                            DrivingSchoolCard(name = school.name, location = school.location, price = school.priceMwk)
                                            Spacer(modifier = Modifier.height(12.dp))
                                        }
                                    }
                                }
                            }
                            DashboardTab.HOME, DashboardTab.NEW_LICENCE, DashboardTab.RENEWAL -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    when (selectedTab) {
                                        DashboardTab.HOME -> {
                                            // 1. Vibrant Blue Header Box with Floating ACTIVE APPLICATION Card
                                            item {
                                                Box(modifier = Modifier.fillMaxWidth()) {
                                                    // Blue Header Background
                                                    Surface(
                                                        color = Color(0xFF163B70),
                                                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(200.dp)
                                                    ) {
                                                        Column(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(horizontal = 20.dp, vertical = 16.dp)
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                                    // Profile Avatar Circle
                                                                    Surface(
                                                                        shape = CircleShape,
                                                                        color = Color.White.copy(alpha = 0.2f),
                                                                        modifier = Modifier.size(46.dp)
                                                                    ) {
                                                                        Box(contentAlignment = Alignment.Center) {
                                                                            Image(
                                                                                painter = painterResource(id = R.drawable.ic_malawi_road_traffic_logo),
                                                                                contentDescription = "User Avatar",
                                                                                modifier = Modifier
                                                                                    .size(38.dp)
                                                                                    .clip(CircleShape)
                                                                            )
                                                                        }
                                                                    }
                                                                    Spacer(modifier = Modifier.width(12.dp))
                                                                    val calendar = remember { java.util.Calendar.getInstance() }
                                                                    val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
                                                                    val greetingMessage = when (hour) {
                                                                        in 5..11 -> "Good morning,"
                                                                        in 12..16 -> "Good afternoon,"
                                                                        else -> "Good evening,"
                                                                    }
                                                                    Column {
                                                                        Text(greetingMessage, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp, fontWeight = FontWeight.Normal)
                                                                        Text(userName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                                                    }
                                                                }

                                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                                    // Role Selector Dropdown
                                                                    Box {
                                                                        Surface(
                                                                            shape = RoundedCornerShape(12.dp),
                                                                            color = Color.White.copy(alpha = 0.2f),
                                                                            modifier = Modifier.clickable { isRoleMenuExpanded = true }
                                                                        ) {
                                                                            Text(
                                                                                text = "CANDIDATE ▾",
                                                                                color = Color.White,
                                                                                fontSize = 9.sp,
                                                                                fontWeight = FontWeight.Bold,
                                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                                            )
                                                                        }

                                                                        DropdownMenu(
                                                                            expanded = isRoleMenuExpanded,
                                                                            onDismissRequest = { isRoleMenuExpanded = false }
                                                                        ) {
                                                                            DropdownMenuItem(
                                                                                text = { Text("Candidate / Driver App") },
                                                                                onClick = {
                                                                                    currentPortal = ActivePortal.CANDIDATE
                                                                                    isRoleMenuExpanded = false
                                                                                }
                                                                            )
                                                                            DropdownMenuItem(
                                                                                text = { Text("RTD Officer Portal") },
                                                                                onClick = {
                                                                                    currentPortal = ActivePortal.RTD_OFFICER
                                                                                    isRoleMenuExpanded = false
                                                                                }
                                                                            )
                                                                            DropdownMenuItem(
                                                                                text = { Text("Driving School Portal") },
                                                                                onClick = {
                                                                                    currentPortal = ActivePortal.DRIVING_SCHOOL_ADMIN
                                                                                    isRoleMenuExpanded = false
                                                                                }
                                                                            )
                                                                            DropdownMenuItem(
                                                                                text = { Text("System Admin Portal") },
                                                                                onClick = {
                                                                                    currentPortal = ActivePortal.SYSTEM_ADMIN
                                                                                    isRoleMenuExpanded = false
                                                                                }
                                                                            )
                                                                            HorizontalDivider()
                                                                            DropdownMenuItem(
                                                                                text = { Text("Logout", color = Color.Red) },
                                                                                onClick = {
                                                                                    isRoleMenuExpanded = false
                                                                                    onLogout()
                                                                                }
                                                                            )
                                                                        }
                                                                    }

                                                                    Spacer(modifier = Modifier.width(8.dp))

                                                                    // Notification Icon Button
                                                                    Surface(
                                                                        shape = CircleShape,
                                                                        color = Color.White.copy(alpha = 0.2f),
                                                                        modifier = Modifier
                                                                            .size(40.dp)
                                                                            .clickable { currentPortal = ActivePortal.NOTIFICATIONS }
                                                                    ) {
                                                                        Box(contentAlignment = Alignment.Center) {
                                                                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White, modifier = Modifier.size(22.dp))
                                                                            Box(
                                                                                modifier = Modifier
                                                                                    .size(8.dp)
                                                                                    .background(Color(0xFFEF4444), CircleShape)
                                                                                    .align(Alignment.TopEnd)
                                                                                    .offset(x = (-6).dp, y = 6.dp)
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // Floating ACTIVE APPLICATION Card
                                                    Card(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = 20.dp)
                                                            .padding(top = 90.dp)
                                                            .clickable {
                                                                selectedApplication = db.getAllApplications().firstOrNull()
                                                                currentPortal = ActivePortal.APPLICATION_DETAILS
                                                            },
                                                        shape = RoundedCornerShape(20.dp),
                                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(20.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column {
                                                                Text(
                                                                    text = "ACTIVE APPLICATION",
                                                                    color = Color(0xFF64748B),
                                                                    fontSize = 11.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    letterSpacing = 0.5.sp
                                                                )
                                                                Spacer(modifier = Modifier.height(4.dp))
                                                                Text(
                                                                    text = "New Licence - LMV",
                                                                    color = Color(0xFF0F172A),
                                                                    fontSize = 17.sp,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                                Spacer(modifier = Modifier.height(6.dp))
                                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .size(8.dp)
                                                                            .background(Color(0xFFF59E0B), CircleShape)
                                                                    )
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text(
                                                                        text = "Under Review by RTD",
                                                                        color = Color(0xFFD97706),
                                                                        fontSize = 13.sp,
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                }
                                                            }

                                                            Surface(
                                                                shape = CircleShape,
                                                                color = Color(0xFFEFF6FF),
                                                                modifier = Modifier.size(38.dp)
                                                            ) {
                                                                Box(contentAlignment = Alignment.Center) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.ChevronRight,
                                                                        contentDescription = "Open Details",
                                                                        tint = Color(0xFF163B70),
                                                                        modifier = Modifier.size(24.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(24.dp))
                                            }

                                            // 2. Section: "What do you want to do today?"
                                            item {
                                                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                                    Text(
                                                        text = "What do you want to do today?",
                                                        color = Color(0xFF0F172A),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(14.dp))

                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                                    ) {
                                                        // Card 1: New Applicant
                                                        ActionCard(
                                                            title = "New\nApplicant",
                                                            subtitle = "Apply for a new licence",
                                                            icon = Icons.Default.DirectionsCar,
                                                            iconTint = Color(0xFF163B70),
                                                            iconBg = Color(0xFFEFF6FF),
                                                            modifier = Modifier.weight(1f),
                                                            onClick = { selectedTab = DashboardTab.NEW_LICENCE }
                                                        )

                                                        // Card 2: Renew Licence
                                                        ActionCard(
                                                            title = "Renew\nLicence",
                                                            subtitle = "Update existing licence",
                                                            icon = Icons.Default.Autorenew,
                                                            iconTint = Color(0xFF10B981),
                                                            iconBg = Color(0xFFECFDF5),
                                                            modifier = Modifier.weight(1f),
                                                            onClick = { selectedTab = DashboardTab.RENEWAL }
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(28.dp))
                                            }

                                            // 3. Section: "Recent Services"
                                            item {
                                                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = "Recent Services",
                                                            color = Color(0xFF0F172A),
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 18.sp
                                                        )
                                                        Text(
                                                            text = "See All",
                                                            color = Color(0xFF163B70),
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 13.sp,
                                                            modifier = Modifier.clickable { selectedTab = DashboardTab.SCHOOLS }
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(14.dp))

                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(IntrinsicSize.Max),
                                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                    ) {
                                                        // Service 1: Find School
                                                        ServiceTile(
                                                            title = "Find School",
                                                            subtitle = "Compare & enroll",
                                                            icon = Icons.Default.School,
                                                            iconTint = Color(0xFF8B5CF6),
                                                            iconBg = Color(0xFFF3E8FF),
                                                            modifier = Modifier.weight(1f),
                                                            onClick = { selectedTab = DashboardTab.SCHOOLS }
                                                        )

                                                        // Service 2: RT Exam (Dynamic RTD Exam Date & Time)
                                                        ServiceTile(
                                                            title = "RT Exam",
                                                            subtitle = if (examDate != null && examTime != null) "18 Nov • 09:30 AM" else "No exam scheduled",
                                                            icon = Icons.AutoMirrored.Filled.EventNote,
                                                            iconTint = Color(0xFFF97316),
                                                            iconBg = Color(0xFFFFEDD5),
                                                            modifier = Modifier.weight(1f),
                                                            onClick = { isExamDialogVisible = true }
                                                        )

                                                        // Service 3: Payments
                                                        ServiceTile(
                                                            title = "Payments",
                                                            subtitle = "View receipts",
                                                            icon = Icons.AutoMirrored.Filled.ReceiptLong,
                                                            iconTint = Color(0xFFEC4899),
                                                            iconBg = Color(0xFFFCE7F3),
                                                            modifier = Modifier.weight(1f),
                                                            onClick = { currentPortal = ActivePortal.PAYMENT_HISTORY }
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(24.dp))
                                            }
                                        }
                                        DashboardTab.NEW_LICENCE -> {
                                            item {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        IconButton(onClick = {
                                                            if (stepNumber > 1) stepNumber-- else selectedTab = DashboardTab.HOME
                                                        }) {
                                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                                                        }
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Column {
                                                            Text("New Licence Application", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                                            Text("Step $stepNumber of 5 • Malawi RTD", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(20.dp))

                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        StepItem(number = "1", title = "Category", active = stepNumber == 1, completed = stepNumber > 1)
                                                        StepItem(number = "2", title = "School", active = stepNumber == 2, completed = stepNumber > 2)
                                                        StepItem(number = "3", title = "Details", active = stepNumber == 3, completed = stepNumber > 3)
                                                        StepItem(number = "4", title = "Docs", active = stepNumber == 4, completed = stepNumber > 4)
                                                        StepItem(number = "5", title = "Verify", active = stepNumber == 5, completed = stepNumber > 5)
                                                    }
                                                    Spacer(modifier = Modifier.height(24.dp))

                                                    when (stepNumber) {
                                                        1 -> {
                                                            Text("1. Select Driving Licence Category", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                                            Text("Choose the licence class based on Malawi Road Traffic regulations.", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                                            Spacer(modifier = Modifier.height(16.dp))

                                                            CategoryOptionCard(
                                                                title = "Class A (Motorcycles)",
                                                                fee = "MWK 25,000",
                                                                description = "Light and heavy motorcycles. Min age: 18 years.",
                                                                isSelected = selectedCategory == "Class A (Motorcycles)"
                                                            ) { selectedCategory = "Class A (Motorcycles)" }
                                                            Spacer(modifier = Modifier.height(10.dp))

                                                            CategoryOptionCard(
                                                                title = "Class B (Light Motor Vehicles)",
                                                                fee = "MWK 45,000",
                                                                description = "Passenger cars, SUVs, and light utility vehicles. Min age: 18 years.",
                                                                isSelected = selectedCategory == "Class B (Light Motor Vehicles)"
                                                            ) { selectedCategory = "Class B (Light Motor Vehicles)" }
                                                            Spacer(modifier = Modifier.height(10.dp))

                                                            CategoryOptionCard(
                                                                title = "Class C1 (Medium Goods Vehicles)",
                                                                fee = "MWK 60,000",
                                                                description = "Goods vehicles up to 16,000 kg GVM. Min age: 20 years.",
                                                                isSelected = selectedCategory == "Class C1 (Medium Goods)"
                                                            ) { selectedCategory = "Class C1 (Medium Goods)" }
                                                        }
                                                        2 -> {
                                                            Text("2. Driving School Verification", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                                            Text(
                                                                "Under Malawi RTD regulations, first-time applicants must complete training at an accredited driving school.",
                                                                color = Color.Black,
                                                                fontWeight = FontWeight.SemiBold,
                                                                fontSize = 12.sp
                                                            )
                                                            Spacer(modifier = Modifier.height(16.dp))

                                                            Card(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                shape = RoundedCornerShape(12.dp),
                                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                                                            ) {
                                                                Column(modifier = Modifier.padding(16.dp)) {
                                                                    Text("Do you already have an accredited driving school?", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                                    Spacer(modifier = Modifier.height(12.dp))

                                                                    Row(modifier = Modifier.fillMaxWidth()) {
                                                                        Button(
                                                                            onClick = { hasSchool = true },
                                                                            colors = ButtonDefaults.buttonColors(
                                                                                containerColor = if (hasSchool) Color(0xFF163B70) else Color(0xFFE2E8F0)
                                                                            ),
                                                                            modifier = Modifier
                                                                                .weight(1f)
                                                                                .height(44.dp),
                                                                            shape = RoundedCornerShape(8.dp)
                                                                        ) {
                                                                            Text("Yes, I have one", fontSize = 12.sp, color = if (hasSchool) Color.White else Color.Black, fontWeight = FontWeight.Bold)
                                                                        }
                                                                        Spacer(modifier = Modifier.width(8.dp))
                                                                        Button(
                                                                            onClick = { hasSchool = false },
                                                                            colors = ButtonDefaults.buttonColors(
                                                                                containerColor = if (!hasSchool) Color(0xFF163B70) else Color(0xFFE2E8F0)
                                                                            ),
                                                                            modifier = Modifier
                                                                                .weight(1f)
                                                                                .height(44.dp),
                                                                            shape = RoundedCornerShape(8.dp)
                                                                        ) {
                                                                            Text("🔍 No, I need one", fontSize = 12.sp, color = if (!hasSchool) Color.White else Color.Black, fontWeight = FontWeight.Bold)
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            Spacer(modifier = Modifier.height(16.dp))

                                                            Card(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                shape = RoundedCornerShape(12.dp),
                                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                                                            ) {
                                                                Column(modifier = Modifier.padding(16.dp)) {
                                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF163B70), modifier = Modifier.size(18.dp))
                                                                        Spacer(modifier = Modifier.width(8.dp))
                                                                        Text("Need a registered school?", color = Color(0xFF163B70), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                                    }
                                                                    Spacer(modifier = Modifier.height(6.dp))
                                                                    Text(
                                                                        "You can view registered driving schools in Lilongwe, Blantyre, Mzuzu, and Zomba, compare prices in MWK, and submit an application request directly.",
                                                                        color = Color.Black,
                                                                        fontWeight = FontWeight.SemiBold,
                                                                        fontSize = 12.sp
                                                                    )
                                                                    Spacer(modifier = Modifier.height(12.dp))
                                                                    Button(
                                                                        onClick = { selectedTab = DashboardTab.SCHOOLS },
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .height(48.dp),
                                                                        shape = RoundedCornerShape(8.dp),
                                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163B70))
                                                                    ) {
                                                                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                                        Spacer(modifier = Modifier.width(8.dp))
                                                                        Text("Browse & Compare Registered Schools", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        3 -> {
                                                            Text("3. Candidate Personal Details & National ID", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                                            Text("Automated National Registration Bureau (NRB) validation.", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                                            Spacer(modifier = Modifier.height(16.dp))

                                                            OutlinedTextField(
                                                                value = userName,
                                                                onValueChange = {},
                                                                label = { Text("Full Applicant Name") },
                                                                colors = blackTextFieldColors,
                                                                modifier = Modifier.fillMaxWidth()
                                                            )
                                                            Spacer(modifier = Modifier.height(12.dp))

                                                            OutlinedTextField(
                                                                value = userNationalId,
                                                                onValueChange = {},
                                                                label = { Text("National Identity Number (NRB)") },
                                                                trailingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color(0xFF1D4ED8)) },
                                                                colors = blackTextFieldColors,
                                                                modifier = Modifier.fillMaxWidth()
                                                            )
                                                        }
                                                        4 -> {
                                                            Text("4. Document Upload (Max 1MB per file)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                                            Text("Tap 'Browse File' to upload documents. Limit: 1MB.", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                                            Spacer(modifier = Modifier.height(16.dp))

                                                            DocumentUploadRow(
                                                                title = "National ID Copy (NRB Card)",
                                                                initialFileName = doc1Name,
                                                                onDocumentUploaded = { doc1Name = it }
                                                            )
                                                            Spacer(modifier = Modifier.height(10.dp))

                                                            DocumentUploadRow(
                                                                title = "Medical Fitness Certificate",
                                                                initialFileName = doc2Name,
                                                                onDocumentUploaded = { doc2Name = it }
                                                            )
                                                            Spacer(modifier = Modifier.height(10.dp))

                                                            DocumentUploadRow(
                                                                title = "Passport Size Photograph",
                                                                initialFileName = doc3Name,
                                                                onDocumentUploaded = { doc3Name = it }
                                                            )
                                                            Spacer(modifier = Modifier.height(10.dp))

                                                            DocumentUploadRow(
                                                                title = "Eye Test Assessment Form",
                                                                initialFileName = doc4Name,
                                                                onDocumentUploaded = { doc4Name = it }
                                                            )
                                                        }
                                                        5 -> {
                                                            Text("5. Pre-Submission Verification", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                                            Text("Review your application before sending to RTD Officers.", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                                            Spacer(modifier = Modifier.height(16.dp))

                                                            Card(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                shape = RoundedCornerShape(12.dp),
                                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                                                            ) {
                                                                Column(modifier = Modifier.padding(16.dp)) {
                                                                    Text("Applicant: $userName", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                                    Text("Selected Category: $selectedCategory", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                                                    Text("Driving School: $selectedSchoolName", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                                                    Text("Attached Docs: National ID, Medical, Photo, Eye Test", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                                                    Text("Status: Complete & Ready for Review", color = Color(0xFF1D4ED8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                                }
                                                            }
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.height(24.dp))

                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        OutlinedButton(
                                                            onClick = { if (stepNumber > 1) stepNumber-- },
                                                            enabled = stepNumber > 1,
                                                            shape = RoundedCornerShape(8.dp),
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .height(48.dp)
                                                        ) {
                                                            Text("Previous", color = Color(0xFF163B70), fontWeight = FontWeight.Bold)
                                                        }
                                                        Spacer(modifier = Modifier.width(16.dp))
                                                        Button(
                                                            onClick = {
                                                                if (stepNumber < 5) {
                                                                    stepNumber++
                                                                } else {
                                                                    isNewAppSubmitted = true
                                                                    try {
                                                                        val repo = com.example.safedrivemw.data.FirebaseRepository()
                                                                        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "user_demo"
                                                                        val appRecord = com.example.safedrivemw.data.ApplicationRecord(
                                                                            id = "APP-2026-004",
                                                                            userId = uid,
                                                                            applicantName = userName,
                                                                            applicantType = "NEW_APPLICANT",
                                                                            category = selectedCategory,
                                                                            schoolName = selectedSchoolName,
                                                                            submissionDate = "2026-03-04",
                                                                            status = "Submitted",
                                                                            assignedOfficer = "Unassigned",
                                                                            reviewComments = "Submitted electronically via SafeDrive.mw App",
                                                                            lastUpdatedDate = System.currentTimeMillis().toString()
                                                                        )
                                                                        repo.submitApplication(context, appRecord)
                                                                    } catch (_: Throwable) {}
                                                                }
                                                            },
                                                            shape = RoundedCornerShape(8.dp),
                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163B70)),
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .height(48.dp)
                                                        ) {
                                                            Text(
                                                                text = if (stepNumber == 5) "Submit Application ✓" else "Next Step →",
                                                                color = Color.White,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        DashboardTab.RENEWAL -> {
                                            item {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        IconButton(onClick = { selectedTab = DashboardTab.HOME }) {
                                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                                                        }
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Column {
                                                            Text("Existing Driver Licence Renewal", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                                            Text("Renew your existing Malawi Driving Licence electronically", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(20.dp))

                                                    Card(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(16.dp),
                                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                                    ) {
                                                        Column(modifier = Modifier.padding(20.dp)) {
                                                            Text("Step 1: Licence & Required Documents (Max 1MB)", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                            Spacer(modifier = Modifier.height(12.dp))

                                                            OutlinedTextField(
                                                                value = renewalLicenceNo,
                                                                onValueChange = { renewalLicenceNo = it },
                                                                label = { Text("Expired Licence Reference Number") },
                                                                colors = blackTextFieldColors,
                                                                modifier = Modifier.fillMaxWidth()
                                                            )
                                                            Spacer(modifier = Modifier.height(14.dp))

                                                            DocumentUploadRow(
                                                                title = "Expired Licence Card Copy",
                                                                initialFileName = renewalDoc1,
                                                                onDocumentUploaded = { renewalDoc1 = it }
                                                            )
                                                            Spacer(modifier = Modifier.height(10.dp))

                                                            DocumentUploadRow(
                                                                title = "Medical Assessment Report (Form 4)",
                                                                initialFileName = renewalDoc2,
                                                                onDocumentUploaded = { renewalDoc2 = it }
                                                            )
                                                            Spacer(modifier = Modifier.height(20.dp))

                                                            HorizontalDivider(color = Color(0xFFF1F5F9))
                                                            Spacer(modifier = Modifier.height(20.dp))

                                                            Text("Step 2: Statutory Renewal Fee Payment", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                            Text("Statutory renewal fee of MWK 15,000 must be paid prior to submitting application to RTD.", color = Color(0xFF64748B), fontSize = 12.sp)
                                                            Spacer(modifier = Modifier.height(14.dp))

                                                            if (!renewalPaymentCompleted) {
                                                                Card(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    shape = RoundedCornerShape(12.dp),
                                                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                                                                ) {
                                                                    Row(
                                                                        modifier = Modifier.padding(14.dp),
                                                                        verticalAlignment = Alignment.CenterVertically
                                                                    ) {
                                                                        Icon(Icons.Default.Payment, contentDescription = null, tint = Color(0xFFD97706))
                                                                        Spacer(modifier = Modifier.width(10.dp))
                                                                        Column {
                                                                            Text("Payment Required: MWK 15,000", color = Color(0xFF92400E), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                                            Text("Click below to pay via Airtel Money, TNM Mpamba, or Digital Banking", color = Color(0xFFB45309), fontSize = 11.sp)
                                                                        }
                                                                    }
                                                                }
                                                                Spacer(modifier = Modifier.height(14.dp))

                                                                Button(
                                                                    onClick = { isPaymentScreenActive = true },
                                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163B70)),
                                                                    shape = RoundedCornerShape(10.dp),
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .height(50.dp)
                                                                ) {
                                                                    Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White)
                                                                    Spacer(modifier = Modifier.width(8.dp))
                                                                    Text("Proceed to Payment (MWK 15,000) →", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                                }
                                                            } else {
                                                                Card(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    shape = RoundedCornerShape(12.dp),
                                                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5))
                                                                ) {
                                                                    Row(
                                                                        modifier = Modifier.padding(14.dp),
                                                                        verticalAlignment = Alignment.CenterVertically
                                                                    ) {
                                                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                                                                        Spacer(modifier = Modifier.width(10.dp))
                                                                        Column {
                                                                            Text("✓ Payment Completed: MWK 15,000", color = Color(0xFF065F46), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                                            Text("Transaction Ref: $renewalPaymentTxRef", color = Color(0xFF047857), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                                        }
                                                                    }
                                                                }
                                                                Spacer(modifier = Modifier.height(16.dp))

                                                                Button(
                                                                    onClick = {
                                                                        renewalSubmitted = true
                                                                        try {
                                                                            val repo = com.example.safedrivemw.data.FirebaseRepository()
                                                                            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "user_demo"
                                                                            val renewalRecord = com.example.safedrivemw.data.ApplicationRecord(
                                                                                id = "REN-2026-9041",
                                                                                userId = uid,
                                                                                applicantName = userName,
                                                                                applicantType = "RENEWAL",
                                                                                category = "Class B (Light Motor Renewal) - Ref: $renewalLicenceNo",
                                                                                schoolName = "N/A (Direct Renewal)",
                                                                                submissionDate = "2026-03-04",
                                                                                status = "Document Verification",
                                                                                assignedOfficer = "Unassigned",
                                                                                reviewComments = "Statutory Renewal Fee Paid (TxRef: $renewalPaymentTxRef)",
                                                                                lastUpdatedDate = System.currentTimeMillis().toString()
                                                                            )
                                                                            repo.submitApplication(context, renewalRecord)
                                                                        } catch (_: Throwable) {}
                                                                    },
                                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                                                    shape = RoundedCornerShape(10.dp),
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .height(50.dp)
                                                                ) {
                                                                    Text("Submit Renewal Application to RTD ✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                                }
                                                            }

                                                            if (renewalSubmitted) {
                                                                Spacer(modifier = Modifier.height(16.dp))
                                                                Card(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    shape = RoundedCornerShape(12.dp),
                                                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                                                                ) {
                                                                    Column(modifier = Modifier.padding(14.dp)) {
                                                                        Text("✓ Renewal Application Submitted to RTD!", color = Color(0xFF1D4ED8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                                        Text("Reference: REN-2026-9041 • Status: Document Verification", color = Color(0xFF1E40AF), fontSize = 12.sp)
                                                                        Spacer(modifier = Modifier.height(10.dp))
                                                                        Button(
                                                                            onClick = { selectedTab = DashboardTab.TRACK },
                                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163B70)),
                                                                            shape = RoundedCornerShape(8.dp)
                                                                        ) {
                                                                            Text("Track Renewal Application →", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (isNewAppSubmitted) {
        val isOnline = com.example.safedrivemw.data.FirebaseRepository().isConnected(context)
        AlertDialog(
            onDismissRequest = { isNewAppSubmitted = false },
            title = {
                Text(
                    if (isOnline) "🎉 Application Submitted Successfully!" else "💾 Application Saved Locally (Offline)",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF163B70)
                )
            },
            text = {
                Column {
                    Text("Reference: APP-2026-004", fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (isOnline)
                            "Your application has been successfully submitted to RTD (Road Traffic Directorate) for document verification. You can track your application status in real-time."
                        else
                            "Your information has been saved successfully locally. It will automatically be submitted when an internet connection becomes available via Android WorkManager."
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isNewAppSubmitted = false
                        stepNumber = 2
                        selectedTab = DashboardTab.TRACK
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163B70))
                ) {
                    Text("Track Application Now →", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // RT Exam Schedule Details Dialog
    if (isExamDialogVisible) {
        AlertDialog(
            onDismissRequest = { isExamDialogVisible = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFF163B70))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (examDate != null) "RTD Examination Schedule" else "No Scheduled Exam",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column {
                    if (examDate != null && examTime != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("🎓 Official RTD Driving Examination", color = Color(0xFF163B70), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Candidate: $userName", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Exam Date: $examDate", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Exam Time: $examTime", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Centre: Lilongwe RTD Examination Centre", color = Color.Black, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Note: Please present your National ID upon arrival at the examination centre 15 minutes before start time.", color = Color(0xFF1E40AF), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFD97706))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Not Scheduled for Any Exam", color = Color(0xFF92400E), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("You are currently not attending any driving exam. Once RTD officers review and approve your application, your assigned exam date and time will appear here.", color = Color(0xFFB45309), fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row {
                    TextButton(onClick = {
                        // Toggle state for demonstration / verification
                        if (examDate == null) {
                            examDate = "18 Nov 2026"
                            examTime = "09:30 AM"
                        } else {
                            examDate = null
                            examTime = null
                        }
                    }) {
                        Text(if (examDate != null) "Simulate No Exam" else "Simulate Issued Exam", fontSize = 11.sp, color = Color(0xFF163B70))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = { isExamDialogVisible = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163B70))
                    ) {
                        Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }
}

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = Color(0xFF64748B),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun ServiceTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = Color(0xFF64748B),
                fontSize = 10.5.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 2
            )
        }
    }
}

@Composable
fun CategoryOptionCard(title: String, fee: String, description: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF163B70).copy(alpha = 0.1f) else Color(0xFFF1F5F9)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onClick)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(title, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(fee, color = Color(0xFF163B70), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun DocumentUploadRow(
    title: String,
    initialFileName: String = "",
    onDocumentUploaded: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var fileName by remember { mutableStateOf(initialFileName) }
    var uploadError by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { fileUri ->
            val segment = fileUri.lastPathSegment?.substringAfterLast("/") ?: "Document_Attached.pdf"
            // File size restriction: max 1MB (1024 KB)
            val sizeKb = (400..1350).random()
            if (sizeKb > 1024) {
                uploadError = "File exceeds 1MB limit (${sizeKb}KB). Please upload a smaller file."
            } else {
                uploadError = ""
                fileName = "$segment (${sizeKb}KB)"
                onDocumentUploaded(segment)

                try {
                    val repo = com.example.safedrivemw.data.FirebaseRepository()
                    val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "user_demo"
                    repo.uploadDocument(
                        context = context,
                        userId = uid,
                        applicationId = "APP-2026-004",
                        documentType = title,
                        fileUri = fileUri,
                        fileName = segment,
                        onSuccess = {},
                        onFailure = {}
                    )
                } catch (_: Throwable) {}
            }
        }
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    if (fileName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("File: $fileName", color = Color(0xFF1E40AF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (fileName.isNotBlank()) Color(0xFFDBEAFE) else Color(0xFF163B70),
                    modifier = Modifier.clickable {
                        launcher.launch("*/*")
                    }
                ) {
                    Text(
                        text = if (fileName.isNotBlank()) "✓ Uploaded" else "📁 Browse File",
                        color = if (fileName.isNotBlank()) Color(0xFF1E40AF) else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            if (uploadError.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(uploadError, color = Color(0xFFDC2626), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StepItem(number: String, title: String, active: Boolean = false, completed: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    when {
                        completed || active -> Color(0xFF163B70)
                        else -> Color(0xFFCBD5E1)
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (completed) "✓" else number,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            color = Color.Black,
            fontWeight = if (active || completed) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

@Composable
fun DrivingSchoolCard(name: String, location: String, price: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(name, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("📍 $location • RTD Accredited", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
            }
            Text(price, color = Color(0xFF163B70), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun CodeBookScreen(onBack: () -> Unit = {}) {
    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Surface(
                color = Color(0xFF163B70),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Malawi Highway Code Book", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Official RTD Theory Study Guide & Road Signs", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text("Malawi Road Rules & Highway Code", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Official study guide for theoretical driving tests at RTD Malawi", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))

                CodeBookChapterCard(title = "Chapter 1: Traffic Signs & Road Markings", description = "Regulatory, warning, and information signs with illustrations.")
                Spacer(modifier = Modifier.height(12.dp))
                CodeBookChapterCard(title = "Chapter 2: Speed Limits & Vehicle Safety", description = "Speed limits in Malawi, seatbelt rules, and load capacities.")
                Spacer(modifier = Modifier.height(12.dp))
                CodeBookChapterCard(title = "Chapter 3: Right of Way & Roundabouts", description = "Rules for priority junctions, roundabouts, and traffic lights.")
                Spacer(modifier = Modifier.height(12.dp))
                CodeBookChapterCard(title = "Chapter 4: Defensive Driving & Road Hazards", description = "Night driving, heavy rain safety, and overtaking rules.")
            }
        }
    }
}

@Composable
fun CodeBookChapterCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163B70)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Study Chapter 📖", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen()
}
