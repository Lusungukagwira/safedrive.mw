package com.example.safedrivemw.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safedrivemw.R

enum class ApplicantType {
    NEW_APPLICANT,
    RENEW_LICENCE
}

@Composable
fun ApplicantTypeScreen(
    onTypeSelected: (ApplicantType) -> Unit = {}
) {
    var selectedType by remember { mutableStateOf<ApplicantType?>(null) }
    var errorMessage by remember { mutableStateOf("") }

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
                    Image(
                        painter = painterResource(id = R.drawable.ic_malawi_road_traffic_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, CircleShape)
                            .padding(1.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("SafeDrive.mw", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Select Application Path • Directorate of Road Traffic", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Select Applicant Type", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Choose your driving licence application pathway", style = MaterialTheme.typography.bodyMedium, color = Color.Black, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedType = ApplicantType.NEW_APPLICANT
                        errorMessage = ""
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedType == ApplicantType.NEW_APPLICANT) Color(0xFF1A3A6B).copy(alpha = 0.1f) else Color(0xFFF1F5F9)
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedType == ApplicantType.NEW_APPLICANT,
                        onClick = {
                            selectedType = ApplicantType.NEW_APPLICANT
                            errorMessage = ""
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF1A3A6B), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("New Applicant", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("First-time driving licence application with school selection", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedType = ApplicantType.RENEW_LICENCE
                        errorMessage = ""
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedType == ApplicantType.RENEW_LICENCE) Color(0xFF1A3A6B).copy(alpha = 0.1f) else Color(0xFFF1F5F9)
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedType == ApplicantType.RENEW_LICENCE,
                        onClick = {
                            selectedType = ApplicantType.RENEW_LICENCE
                            errorMessage = ""
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Autorenew, contentDescription = null, tint = Color(0xFF1A3A6B), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Renew License", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Existing drivers renewing an expired licence", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }

            if (errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(errorMessage, color = Color(0xFFDC2626), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (selectedType == null) {
                        errorMessage = "Please select an applicant type"
                    } else {
                        onTypeSelected(selectedType!!)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B))
            ) {
                Text("Continue →", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ApplicantTypeScreenPreview() {
    ApplicantTypeScreen()
}
