package com.example.safedrivemw.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PaymentReceiptItem(
    val txRef: String,
    val amountMwk: String,
    val paymentMethod: String,
    val date: String,
    val purpose: String
)

@Composable
fun PaymentHistoryScreen(
    onBack: () -> Unit = {}
) {
    val historyList = listOf(
        PaymentReceiptItem("TXN-MW-2026-884920", "MWK 20,000", "Airtel Money (0999 123 456)", "01 March 2026", "New Licence Statutory Application Fee"),
        PaymentReceiptItem("TXN-MW-2026-441029", "MWK 15,000", "TNM Mpamba (0888 123 456)", "15 February 2026", "Licence Renewal Fee"),
        PaymentReceiptItem("TXN-MW-2025-110293", "MWK 25,000", "Digital Banking (National Bank)", "10 December 2025", "Driving School Training Booking Fee")
    )

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
                        Text("Payment History & Receipts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Road Traffic Directorate • Transaction Archive", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                Text("Statutory Payment Receipts", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Official digital transaction receipts issued by RTD Malawi", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(historyList.size) { index ->
                val receipt = historyList[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = Color(0xFF1A3A6B))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(receipt.txRef, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Text(receipt.amountMwk, color = Color(0xFF1A3A6B), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Purpose: ${receipt.purpose}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Paid via: ${receipt.paymentMethod}", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                        Text("Date: ${receipt.date}", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentHistoryScreenPreview() {
    PaymentHistoryScreen()
}
