package com.example.safedrivemw.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safedrivemw.db.LicenceApplicationRecord

@Composable
fun ApplicationDetailsScreen(
    application: LicenceApplicationRecord,
    onBack: () -> Unit = {}
) {
    var isCorrectionDialogVisible by remember { mutableStateOf(false) }

    val statusStages = listOf("Submitted", "Document Verification", "Under Review", "Approved", "Licence Issued")
    val currentStageIndex = when (application.status) {
        "Submitted" -> 0
        "Document Verification" -> 1
        "Under Review" -> 2
        "Approved" -> 3
        "Licence Issued" -> 4
        else -> 1
    }

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
                        Text("Application Status Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Ref: ${application.id} • RTD Malawi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3A6B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("APPLICANT NAME", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(application.applicantName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("CATEGORY", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(application.category, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("TYPE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(application.applicantType, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Text("Application Status Stage", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Real-time workflow progress tracking at RTD offices", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        statusStages.forEachIndexed { index, stage ->
                            val isCompleted = index <= currentStageIndex
                            val isCurrent = index == currentStageIndex

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            when {
                                                isCurrent || isCompleted -> Color(0xFF1A3A6B)
                                                else -> Color(0xFFCBD5E1)
                                            },
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted && !isCurrent) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    } else {
                                        Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = stage,
                                        color = Color.Black,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                    if (isCurrent) {
                                        Text("Current Stage in Progress", color = Color(0xFF1A3A6B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (index < statusStages.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 15.dp, top = 4.dp, bottom = 4.dp)
                                        .width(2.dp)
                                        .height(24.dp)
                                        .background(if (isCompleted) Color(0xFF1A3A6B) else Color(0xFFCBD5E1))
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (application.status == "Approved" || application.status == "Licence Issued") {
                item {
                    Text("Digital Licence Confirmation", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3A6B))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("REPUBLIC OF MALAWI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF1D4ED8)
                                ) {
                                    Text("OFFICIALLY VERIFIED", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(application.applicantName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Category: ${application.category}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Licence No: MW-2026-88492", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.QrCode, contentDescription = "QR Code", tint = Color.Black, modifier = Modifier.size(40.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Digital Verification QR Code", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Scan to verify licence at RTD Checkpoints", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            if (application.correctionNotes.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("RTD Action Required: Correction Requested", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(application.correctionNotes, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { isCorrectionDialogVisible = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Re-upload Corrected Documents", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (isCorrectionDialogVisible) {
        AlertDialog(
            onDismissRequest = { isCorrectionDialogVisible = false },
            title = { Text("Submit Document Correction", fontWeight = FontWeight.Bold) },
            text = { Text("Upload the requested updated document to resume RTD Officer verification.") },
            confirmButton = {
                Button(
                    onClick = { isCorrectionDialogVisible = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B))
                ) {
                    Text("Upload & Resubmit", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { isCorrectionDialogVisible = false }) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ApplicationDetailsScreenPreview() {
    ApplicationDetailsScreen(
        application = LicenceApplicationRecord(
            id = "APP-2026-001",
            userId = "usr_1",
            applicantName = "Chisomo Banda",
            applicantType = "NEW_APPLICANT",
            category = "Class B (Light Motor)",
            schoolName = "Lilongwe Motor School",
            status = "Approved",
            submittedDate = "2026-03-01",
            correctionNotes = "Please re-upload clearer medical fitness certificate copy."
        )
    )
}
