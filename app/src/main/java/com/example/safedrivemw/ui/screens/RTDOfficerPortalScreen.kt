package com.example.safedrivemw.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
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
fun RTDOfficerPortalScreen(
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { SafeDriveDatabase.getInstance(context) }
    var applicationsList by remember { mutableStateOf(db.getAllApplications()) }
    var selectedAppForReview by remember { mutableStateOf<LicenceApplicationRecord?>(null) }
    var correctionText by remember { mutableStateOf("") }
    var isCorrectionDialogVisible by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFEFF6FF),
        topBar = {
            Surface(
                color = Color(0xFF1A3A6B),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("RTD Officer Portal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Road Traffic Directorate • Application Processing Queue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    TextButton(onClick = onLogout) {
                        Text("Exit Portal", color = Color.White, fontWeight = FontWeight.Bold)
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDBEAFE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Officer Dashboard Overview", color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricBox(title = "Pending Review", value = "${applicationsList.count { it.status != "Approved" }}", color = Color(0xFF1A3A6B), bgColor = Color(0xFFE0F2FE))
                            MetricBox(title = "Approved Today", value = "${applicationsList.count { it.status == "Approved" }}", color = Color(0xFF1A3A6B), bgColor = Color(0xFFDBEAFE))
                            MetricBox(title = "Total Applications", value = "${applicationsList.size}", color = Color(0xFF1A3A6B), bgColor = Color(0xFFE0E7FF))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Text("Submitted Application Queue", color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Select an applicant to review uploaded documents and update status", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(applicationsList) { app ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(app.applicantName, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Ref: ${app.id} • ${app.category}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (app.status) {
                                    "Approved" -> Color(0xFFDBEAFE)
                                    "Under Review" -> Color(0xFFE0F2FE)
                                    else -> Color(0xFFE0E7FF)
                                }
                            ) {
                                Text(
                                    text = app.status,
                                    color = Color(0xFF1A3A6B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = {
                                    db.updateApplicationStatus(app.id, "Document Verification")
                                    applicationsList = db.getAllApplications()
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF1A3A6B))
                            ) {
                                Text("Verify Docs", color = Color(0xFF1A3A6B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    selectedAppForReview = app
                                    isCorrectionDialogVisible = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Request Info", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    db.updateApplicationStatus(app.id, "Approved")
                                    applicationsList = db.getAllApplications()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Approve ✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (isCorrectionDialogVisible && selectedAppForReview != null) {
        AlertDialog(
            onDismissRequest = { isCorrectionDialogVisible = false },
            title = { Text("Request Additional Info / Correction", fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A)) },
            text = {
                Column {
                    Text("Specify requirements or missing documents for ${selectedAppForReview?.applicantName}:", color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = correctionText,
                        onValueChange = { correctionText = it },
                        label = { Text("Correction Notes") },
                        placeholder = { Text("e.g. Please re-upload clearer Medical Fitness Report") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        db.updateApplicationStatus(selectedAppForReview!!.id, "Correction Required", correctionText)
                        applicationsList = db.getAllApplications()
                        isCorrectionDialogVisible = false
                        correctionText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B))
                ) {
                    Text("Send Request", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { isCorrectionDialogVisible = false }) {
                    Text("Cancel", color = Color(0xFF1A3A6B))
                }
            }
        )
    }
}

@Composable
fun MetricBox(title: String, value: String, color: Color, bgColor: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(title, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RTDOfficerPortalScreenPreview() {
    RTDOfficerPortalScreen()
}
