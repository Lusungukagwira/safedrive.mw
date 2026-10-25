package com.example.safedrivemw.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Compare
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
import com.example.safedrivemw.db.DrivingSchoolRecord
import com.example.safedrivemw.db.SafeDriveDatabase

@Composable
fun SchoolComparisonScreen(
    onBack: () -> Unit = {},
    onRequestSchool: (DrivingSchoolRecord) -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { SafeDriveDatabase.getInstance(context) }
    val allSchools = remember { db.getAllDrivingSchools() }

    var selectedSchoolIds by remember { mutableStateOf(setOf(allSchools.firstOrNull()?.id ?: "", allSchools.getOrNull(1)?.id ?: "")) }
    var errorMessage by remember { mutableStateOf("") }
    var isCompared by remember { mutableStateOf(true) }

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
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Compare Driving Schools", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Side-by-side comparison of fees, locations & categories", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                Text("Select Schools to Compare", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Check at least 2 accredited schools to generate comparison matrix", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))

                allSchools.forEach { school ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedSchoolIds.contains(school.id),
                            onCheckedChange = { checked ->
                                selectedSchoolIds = if (checked) {
                                    selectedSchoolIds + school.id
                                } else {
                                    selectedSchoolIds - school.id
                                }
                                errorMessage = ""
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(school.name, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("📍 ${school.location} • Fee: ${school.priceMwk}", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                        }
                    }
                }

                if (errorMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage, color = Color(0xFFDC2626), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedSchoolIds.size < 2) {
                            errorMessage = "Please select at least two schools to compare"
                            isCompared = false
                        } else {
                            errorMessage = ""
                            isCompared = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Compare, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compare Selected Schools", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (isCompared && selectedSchoolIds.size >= 2) {
                val comparedSchools = allSchools.filter { selectedSchoolIds.contains(it.id) }

                item {
                    Text("Side-by-Side Comparison Matrix", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        comparedSchools.forEach { school ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(school.name, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("FEE:", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(school.priceMwk, color = Color(0xFF1A3A6B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("LOCATION:", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(school.location, color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("CATEGORIES:", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(school.categoriesOffered, color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("RATING:", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(school.rating, color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = { onRequestSchool(school) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B)),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Request Enrolment", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SchoolComparisonScreenPreview() {
    SchoolComparisonScreen()
}
