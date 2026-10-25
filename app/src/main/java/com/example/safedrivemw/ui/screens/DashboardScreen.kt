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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safedrivemw.R
import com.example.safedrivemw.db.LicenceApplicationRecord
import com.example.safedrivemw.db.SafeDriveDatabase

enum class DashboardTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    NEW_LICENCE("New Licence", Icons.Default.Badge),
    RENEWAL("Renewal", Icons.Default.Autorenew),
    SCHOOLS("Schools", Icons.Default.School),
    PROFILE("Profile", Icons.Default.Person)
}

enum class ActivePortal {
    CANDIDATE,
    RTD_OFFICER,
    DRIVING_SCHOOL_ADMIN,
    SYSTEM_ADMIN,
    APPLICATION_DETAILS,
    NOTIFICATIONS,
    CODE_BOOK
}

@Composable
fun DashboardScreen(
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { SafeDriveDatabase.getInstance(context) }

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

    var renewalLicenceNo by remember { mutableStateOf("") }
    var renewalDoc1 by remember { mutableStateOf("") }
    var renewalDoc2 by remember { mutableStateOf("") }
    var renewalSubmitted by remember { mutableStateOf(false) }

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
                topBar = {
                    Column {
                        // Topmost Live Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E3A8A))
                                .padding(vertical = 4.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🇲🇼 REPUBLIC OF MALAWI • DRTSS Official Portal • 🟢 LIVE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Main Header Card
                        Surface(
                            color = Color(0xFF1A3A6B),
                            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_malawi_road_traffic_logo),
                                            contentDescription = "Logo",
                                            modifier = Modifier
                                                .size(38.dp)
                                                .background(Color.White, CircleShape)
                                                .padding(2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("SafeDrive.mw", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
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
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
                                            }
                                            Text("Road Traffic & Safety Services", color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
                                        }
                                    }

                                    IconButton(onClick = { currentPortal = ActivePortal.NOTIFICATIONS }) {
                                        Box {
                                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(Color(0xFFDC2626), CircleShape)
                                                    .align(Alignment.TopEnd),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("3", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Header Tagline Motto
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✔ “Learn. Drive. Licence. Own the Road”",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                bottomBar = {
                    Surface(
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DashboardTab.entries.forEach { tab ->
                                val isSelected = selectedTab == tab
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable {
                                            selectedTab = tab
                                            if (tab == DashboardTab.PROFILE) {
                                                currentPortal = ActivePortal.CANDIDATE
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    if (isSelected) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFF1A3A6B),
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = tab.title,
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                                                    .size(20.dp)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.title,
                                            tint = Color.Black,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                    Text(
                                        text = tab.title,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(0xFF1A3A6B) else Color.Black
                                    )
                                }
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
                    when (selectedTab) {
                        DashboardTab.HOME -> {
                            // 1. Welcome & Status Overview Card
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("Welcome back,", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                                Text("Chisomo Bandason", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(20.dp),
                                                color = Color(0xFFEFF6FF)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(6.dp)
                                                            .background(Color(0xFF2563EB), CircleShape)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Verified ID", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // 3 Stat Boxes
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            StatBox(
                                                title = "Applications",
                                                value = "3 Active",
                                                valueColor = Color.Black,
                                                modifier = Modifier.weight(1f)
                                            )
                                            StatBox(
                                                title = "Next Test",
                                                value = "Nov 18",
                                                valueColor = Color(0xFF2563EB),
                                                modifier = Modifier.weight(1f)
                                            )
                                            StatBox(
                                                title = "Digital Pass",
                                                value = "Ready",
                                                valueColor = Color(0xFF2563EB),
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                            }

                            // 2. QUICK SERVICES
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("QUICK SERVICES", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        "View All",
                                        color = Color(0xFF2563EB),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.clickable { selectedTab = DashboardTab.NEW_LICENCE }
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    QuickServiceCard(
                                        title = "Book Test",
                                        icon = Icons.Default.Event,
                                        modifier = Modifier.weight(1f)
                                    ) { selectedTab = DashboardTab.NEW_LICENCE }

                                    QuickServiceCard(
                                        title = "Renew",
                                        icon = Icons.Default.Autorenew,
                                        modifier = Modifier.weight(1f)
                                    ) { selectedTab = DashboardTab.RENEWAL }

                                    QuickServiceCard(
                                        title = "Schools",
                                        icon = Icons.Default.School,
                                        modifier = Modifier.weight(1f)
                                    ) { selectedTab = DashboardTab.SCHOOLS }

                                    QuickServiceCard(
                                        title = "Code Book",
                                        icon = Icons.AutoMirrored.Filled.MenuBook,
                                        modifier = Modifier.weight(1f)
                                    ) { currentPortal = ActivePortal.CODE_BOOK }
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                            }

                            // 3. Your Active Licence Applications
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Your Active Licence Applications", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFEFF6FF)
                                        ) {
                                            Text(
                                                text = "3",
                                                color = Color(0xFF2563EB),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text("Updated 10m ago", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                // Application Card 1
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedApplication = db.getAllApplications().firstOrNull()
                                            currentPortal = ActivePortal.APPLICATION_DETAILS
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = Color(0xFFEFF6FF),
                                                    modifier = Modifier.size(40.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(22.dp))
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text("Class B (Light Motor)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                    Text("Ref: APP-2026-001", color = Color.Gray, fontSize = 11.sp)
                                                    Text("📍 Lilongwe Motor School", color = Color.Gray, fontSize = 11.sp)
                                                }
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(16.dp),
                                                color = Color(0xFFEFF6FF)
                                            ) {
                                                Text("● Under Review", color = Color(0xFF2563EB), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("✔ Theory Passed", color = Color(0xFF2563EB), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("📅 Practical: 18 Nov", color = Color(0xFF2563EB), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LinearProgressIndicator(
                                            progress = { 0.7f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp),
                                            color = Color(0xFF1A3A6B),
                                            trackColor = Color(0xFFE2E8F0),
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Application Card 2
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedApplication = db.getAllApplications().getOrNull(1)
                                            currentPortal = ActivePortal.APPLICATION_DETAILS
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = Color(0xFFEFF6FF),
                                                    modifier = Modifier.size(40.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(22.dp))
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text("Class C1 (Medium Goods)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                    Text("Ref: APP-2026-002", color = Color.Gray, fontSize = 11.sp)
                                                    Text("🔄 Direct Professional Renewal", color = Color.Gray, fontSize = 11.sp)
                                                }
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(16.dp),
                                                color = Color(0xFFEFF6FF)
                                            ) {
                                                Text("📄 Doc Verification", color = Color(0xFF2563EB), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Medical Certificate pending", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = {
                                                    selectedApplication = db.getAllApplications().getOrNull(1)
                                                    currentPortal = ActivePortal.APPLICATION_DETAILS
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B)),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Text("Upload", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        DashboardTab.NEW_LICENCE -> {
                            item {
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
                                                            containerColor = if (hasSchool) Color(0xFF1A3A6B) else Color(0xFFE2E8F0)
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
                                                            containerColor = if (!hasSchool) Color(0xFF1A3A6B) else Color(0xFFE2E8F0)
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
                                                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1A3A6B), modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Need a registered school?", color = Color(0xFF1A3A6B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B))
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
                                            value = "Chisomo Banda",
                                            onValueChange = {},
                                            label = { Text("Full Applicant Name") },
                                            colors = blackTextFieldColors,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))

                                        OutlinedTextField(
                                            value = "NRB-987654321-MW",
                                            onValueChange = {},
                                            label = { Text("National Identity Number (NRB)") },
                                            trailingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color(0xFF1D4ED8)) },
                                            colors = blackTextFieldColors,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    4 -> {
                                        Text("4. Document Upload", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text("Tap 'Browse File' to upload documents from your device.", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
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
                                                Text("Applicant: Chisomo Banda", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                        Text("Previous", color = Color(0xFF1A3A6B), fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Button(
                                        onClick = {
                                            if (stepNumber < 5) {
                                                stepNumber++
                                            } else {
                                                selectedTab = DashboardTab.HOME
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B)),
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
                        DashboardTab.RENEWAL -> {
                            item {
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
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Step 1: Enter Expired Licence Reference", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(10.dp))

                                        OutlinedTextField(
                                            value = renewalLicenceNo,
                                            onValueChange = { renewalLicenceNo = it },
                                            label = { Text("Licence Number (e.g. MW-2022-99881)") },
                                            colors = blackTextFieldColors,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))

                                        DocumentUploadRow(
                                            title = "Expired Licence Photo Upload",
                                            initialFileName = renewalDoc1,
                                            onDocumentUploaded = { renewalDoc1 = it }
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))

                                        DocumentUploadRow(
                                            title = "Medical Assessment Report",
                                            initialFileName = renewalDoc2,
                                            onDocumentUploaded = { renewalDoc2 = it }
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                            onClick = { renewalSubmitted = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                        ) {
                                            Text("Submit Renewal Request to RTD", color = Color.White, fontWeight = FontWeight.Bold)
                                        }

                                        if (renewalSubmitted) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text("✓ Renewal application submitted successfully! Reference: REN-2026-9041", color = Color(0xFF1D4ED8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                        DashboardTab.SCHOOLS -> {
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
                        DashboardTab.PROFILE -> {
                            item {
                                ProfileScreen(
                                    onBack = { selectedTab = DashboardTab.HOME },
                                    onNavigateToPaymentHistory = { selectedTab = DashboardTab.HOME },
                                    onLogout = onLogout
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(title: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF1F5F9)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun QuickServiceCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFEFF6FF),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = title, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
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
            containerColor = if (isSelected) Color(0xFF1A3A6B).copy(alpha = 0.1f) else Color(0xFFF1F5F9)
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
                    Text(fee, color = Color(0xFF1A3A6B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
    var fileName by remember { mutableStateOf(initialFileName) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val segment = it.lastPathSegment?.substringAfterLast("/") ?: "Document_Attached.pdf"
            fileName = segment
            onDocumentUploaded(segment)
        }
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
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
                color = if (fileName.isNotBlank()) Color(0xFFDBEAFE) else Color(0xFF1A3A6B),
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
                        completed || active -> Color(0xFF1A3A6B)
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
            Text(price, color = Color(0xFF1A3A6B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun CodeBookScreen(onBack: () -> Unit = {}) {
    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Surface(
                color = Color(0xFF1A3A6B),
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B)),
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
