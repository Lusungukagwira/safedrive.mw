package com.example.safedrivemw.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    userName: String = "Lusungu Kagwira",
    userEmail: String = "lusungu.kagwira@safedrive.mw",
    userPhone: String = "+265 999 123 456",
    userNationalId: String = "NRB-987654321-MW",
    userDob: String = "15 August 2002",
    userRole: String = "Candidate Driver",
    onBack: () -> Unit = {},
    onNavigateToPaymentHistory: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var isLogoutDialogVisible by remember { mutableStateOf(false) }
    var isAuditLogVisible by remember { mutableStateOf(false) }
    var auditLogsList by remember { mutableStateOf<List<com.example.safedrivemw.data.ActivityLogEntry>>(emptyList()) }

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
                        Text("User Profile & Account", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("SafeDrive.mw • Republic of Malawi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
            // Profile Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF163B70))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            color = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF163B70),
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text(userEmail, color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "NRB VERIFIED APPLICANT ✓",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Personal Information Section
            item {
                Text("Personal Information", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ProfileInfoRow(
                            label = "Full Name",
                            value = userName,
                            icon = Icons.Default.Badge
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                        ProfileInfoRow(
                            label = "National Identity (NRB)",
                            value = userNationalId,
                            icon = Icons.Default.Verified
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                        ProfileInfoRow(
                            label = "Email Address",
                            value = userEmail,
                            icon = Icons.Default.Email
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                        ProfileInfoRow(
                            label = "Phone Number",
                            value = userPhone,
                            icon = Icons.Default.Phone
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                        ProfileInfoRow(
                            label = "Date of Birth",
                            value = userDob,
                            icon = Icons.Default.Cake
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                        ProfileInfoRow(
                            label = "Residential Address",
                            value = "Area 3, Lilongwe, Malawi",
                            icon = Icons.Default.Home
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                        ProfileInfoRow(
                            label = "Portal Role",
                            value = userRole,
                            icon = Icons.Default.AccountCircle
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Account Options Section
            item {
                Text("Account Options", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))

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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFEFF6FF),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                            contentDescription = null,
                                            tint = Color(0xFF163B70),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Payment Receipts", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                    Text("Past statutory fee payments & receipts", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }

                            Button(
                                onClick = onNavigateToPaymentHistory,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163B70)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("View History", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFEFF6FF),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = Color(0xFF163B70),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Firebase Audit Logs", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                    Text("Stored activity & transaction audit logs", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }

                            Button(
                                onClick = {
                                    val repo = com.example.safedrivemw.data.FirebaseRepository()
                                    val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "user_demo"
                                    repo.fetchActivityLogs(uid) { logs ->
                                        auditLogsList = logs
                                        isAuditLogVisible = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163B70)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Audit Logs", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { isLogoutDialogVisible = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }

    if (isLogoutDialogVisible) {
        AlertDialog(
            onDismissRequest = { isLogoutDialogVisible = false },
            title = { Text("Confirm Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out of SafeDrive.mw? Your active session will be cleared.") },
            confirmButton = {
                Button(
                    onClick = {
                        isLogoutDialogVisible = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Logout", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { isLogoutDialogVisible = false }) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }

    if (isAuditLogVisible) {
        AlertDialog(
            onDismissRequest = { isAuditLogVisible = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF163B70))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Firebase Activity & Audit Logs", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column {
                    Text("Stored records in Firestore 'activity_logs' collection:", fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(12.dp))

                    if (auditLogsList.isEmpty()) {
                        Text("No activity logs recorded yet. Perform an action (login, document upload, submission, payment) to record logs.", fontSize = 12.sp, color = Color.Gray)
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            items(auditLogsList.size) { index ->
                                val item = auditLogsList[index]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(item.action, fontWeight = FontWeight.Bold, color = Color(0xFF163B70), fontSize = 13.sp)
                                        Text(item.description, fontSize = 12.sp, color = Color.Black)
                                        Text("Ref ID: ${item.relatedRecordId}", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { isAuditLogVisible = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163B70))
                ) {
                    Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun ProfileInfoRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFEFF6FF),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF163B70),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text(value, color = Color(0xFF0F172A), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
