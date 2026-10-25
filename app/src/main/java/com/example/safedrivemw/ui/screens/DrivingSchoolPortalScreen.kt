package com.example.safedrivemw.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
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
import com.example.safedrivemw.db.SafeDriveDatabase

@Composable
fun DrivingSchoolPortalScreen(
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { SafeDriveDatabase.getInstance(context) }
    var schoolList by remember { mutableStateOf(db.getAllDrivingSchools()) }

    var selectedSchoolPrice by remember { mutableStateOf("MWK 150,000") }
    var isEditingPrice by remember { mutableStateOf(false) }

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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Driving School Portal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Manage Training Fees, Categories & Applicant Requests", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
                Text("Registered Driving Schools Overview", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Update course fees in MWK and review student requests", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(schoolList) { school ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(school.name, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("📍 ${school.location}", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFDBEAFE)
                            ) {
                                Text(school.rating, color = Color(0xFF1E40AF), fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Categories: ${school.categoriesOffered}", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Contact: ${school.phone}", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Training Fee: ${school.priceMwk}", color = Color(0xFF1A3A6B), fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { isEditingPrice = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Update MWK Fees", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (isEditingPrice) {
        AlertDialog(
            onDismissRequest = { isEditingPrice = false },
            title = { Text("Update Driving School Training Fee", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = selectedSchoolPrice,
                    onValueChange = { selectedSchoolPrice = it },
                    label = { Text("Fee in MWK") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        isEditingPrice = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B))
                ) {
                    Text("Save Changes", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditingPrice = false }) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DrivingSchoolPortalScreenPreview() {
    DrivingSchoolPortalScreen()
}
