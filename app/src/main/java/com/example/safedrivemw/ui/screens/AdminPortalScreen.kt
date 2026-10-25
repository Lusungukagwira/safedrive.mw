package com.example.safedrivemw.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminPortalScreen(
    onLogout: () -> Unit = {}
) {
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
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("System Admin Portal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("SafeDrive.mw Platform Management & Configurations", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
                Text("System Administration Controls", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Manage system actors, RTD Officers, and service configurations", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))

                AdminActionCard(title = "👥 User & Role Management", description = "Authorize RTD Officers, Driving School Admins, and system users.")
                Spacer(modifier = Modifier.height(12.dp))
                AdminActionCard(title = "🏫 Registered Driving Schools Config", description = "Approve, suspend, or configure accredited driving school listings.")
                Spacer(modifier = Modifier.height(12.dp))
                AdminActionCard(title = "📜 Statutory RTD Fees Schedule", description = "Update MWK fee schedules for Class A, B, C, D, and EB licences.")
                Spacer(modifier = Modifier.height(12.dp))
                AdminActionCard(title = "🔒 Security & System Audit Logs", description = "Inspect system activity logs, authentication records, and security audits.")
            }
        }
    }
}

@Composable
fun AdminActionCard(title: String, description: String) {
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
                Text("Manage", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminPortalScreenPreview() {
    AdminPortalScreen()
}
