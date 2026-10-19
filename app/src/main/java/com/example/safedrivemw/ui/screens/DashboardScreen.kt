package com.example.safedrivemw.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safedrivemw.R

enum class DashboardTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    NEW_LICENCE("New Licence", Icons.Default.Badge),
    RENEWAL("Renewal", Icons.Default.Autorenew),
    SCHOOLS("Schools", Icons.Default.School)
}

@Composable
fun DashboardScreen(
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(DashboardTab.NEW_LICENCE) }
    var hasSchool by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF0B132B),
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF15803D))
                        .padding(vertical = 4.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "REPUBLIC OF MALAWI • Road Traffic Directorate (RTD) • Official Portal",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF111D4A))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_malawi_road_traffic_logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White, CircleShape)
                                .padding(1.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("SafeDrive.mw", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Licence Application & Verification", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF16A34A).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "👤 CANDIDATE",
                                color = Color(0xFF4ADE80),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(Color(0xFFDC2626), CircleShape)
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("3", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color(0xFF0A1128),
                tonalElevation = 8.dp
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
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            if (isSelected) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFD97706),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp, vertical = 6.dp)
                                            .size(22.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFFF59E0B) else Color.Gray
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
                DashboardTab.NEW_LICENCE -> {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("New Licence Application", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Step 2 of 5 • Malawi RTD", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StepItem(number = "1", title = "Category", completed = true)
                            StepItem(number = "2", title = "School", active = true)
                            StepItem(number = "3", title = "Details")
                            StepItem(number = "4", title = "Docs")
                            StepItem(number = "5", title = "Verify")
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        Text("2. Driving School Verification", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            "Under Malawi RTD regulations, first-time applicants must complete training at an accredited driving school.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF111D4A))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Do you already have an accredited driving school?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Button(
                                        onClick = { hasSchool = true },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (hasSchool) Color(0xFF16A34A) else Color(0xFF1E293B)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Yes, I have one", fontSize = 12.sp, color = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { hasSchool = false },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (!hasSchool) Color(0xFFD97706) else Color(0xFF1E293B)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("🔍 No, I need one", fontSize = 12.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Need a registered school?", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "You can view registered driving schools in Lilongwe, Blantyre, Mzuzu, and Zomba, compare prices in MWK, and submit an application request directly.",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { selectedTab = DashboardTab.SCHOOLS },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Browse & Compare Registered Schools", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = {},
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Previous", color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = {},
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Next Step →", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                DashboardTab.HOME -> {
                    item {
                        Text("Home Dashboard", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Welcome to SafeDrive.mw - Directorate of Road Traffic & Safety Services", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF111D4A)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Your Active Applications", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("• Class B Driving Licence (In Progress - Step 2)", color = Color(0xFF4ADE80), fontSize = 13.sp)
                            }
                        }
                    }
                }
                DashboardTab.RENEWAL -> {
                    item {
                        Text("Licence Renewal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Renew your existing Malawi Driving Licence", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Enter Licence Number (e.g. MW-12345)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Verify Licence for Renewal", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                DashboardTab.SCHOOLS -> {
                    item {
                        Text("Accredited Driving Schools", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Registered schools across Lilongwe, Blantyre, Mzuzu, and Zomba", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        DrivingSchoolCard(name = "Lilongwe Motor School", location = "Lilongwe", price = "MWK 150,000")
                        Spacer(modifier = Modifier.height(12.dp))
                        DrivingSchoolCard(name = "Blantyre Driving Academy", location = "Blantyre", price = "MWK 140,000")
                        Spacer(modifier = Modifier.height(12.dp))
                        DrivingSchoolCard(name = "Mzuzu Road Safety School", location = "Mzuzu", price = "MWK 135,000")
                    }
                }
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
                        completed -> Color(0xFF16A34A)
                        active -> Color(0xFFD97706)
                        else -> Color(0xFF334155)
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
            color = if (active || completed) Color.White else Color.Gray
        )
    }
}

@Composable
fun DrivingSchoolCard(name: String, location: String, price: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111D4A))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("📍 $location • RTD Accredited", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            }
            Text(price, color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen()
}
