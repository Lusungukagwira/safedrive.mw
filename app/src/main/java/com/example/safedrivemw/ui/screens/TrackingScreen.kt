package com.example.safedrivemw.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safedrivemw.db.LicenceApplicationRecord
import com.example.safedrivemw.db.SafeDriveDatabase

@Composable
fun TrackingScreen(
    onBack: () -> Unit = {},
    onSelectApplication: (LicenceApplicationRecord) -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { SafeDriveDatabase.getInstance(context) }
    var applications by remember { mutableStateOf(db.getAllApplications()) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = applications.filter {
        it.id.contains(searchQuery, ignoreCase = true) ||
                it.applicantName.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true) ||
                it.status.contains(searchQuery, ignoreCase = true)
    }

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
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Application Status & Tracking", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Road Traffic Directorate • Live Workflow Status", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
            // Search / Filter Input Box
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by Application Ref or Category...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF163B70)) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    colors = blackTextFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Active Applications", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFEFF6FF)
                    ) {
                        Text(
                            text = "${filteredApps.size} Applications",
                            color = Color(0xFF163B70),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Application List Items
            items(filteredApps) { app ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onSelectApplication(app) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(app.category, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Ref: ${app.id}", color = Color(0xFF64748B), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text("School: ${app.schoolName}", color = Color(0xFF64748B), fontSize = 12.sp)
                                Text("Submitted: ${app.submittedDate}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = when (app.status) {
                                    "Approved", "Licence Issued" -> Color(0xFFECFDF5)
                                    "Under Review" -> Color(0xFFFEF3C7)
                                    else -> Color(0xFFEFF6FF)
                                }
                            ) {
                                Text(
                                    text = app.status,
                                    color = when (app.status) {
                                        "Approved", "Licence Issued" -> Color(0xFF10B981)
                                        "Under Review" -> Color(0xFFD97706)
                                        else -> Color(0xFF163B70)
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (app.correctionNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFEF2F2)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(app.correctionNotes, color = Color(0xFFDC2626), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Type: ${app.applicantType}", color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("View Tracking Details →", color = Color(0xFF163B70), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackingScreenPreview() {
    TrackingScreen()
}
