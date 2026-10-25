package com.example.safedrivemw.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Date

enum class PaymentMethod(val title: String) {
    AIRTEL_MONEY("Airtel Money"),
    TNM_MPAMBA("TNM Mpamba"),
    DIGITAL_BANKING("Digital Banking")
}

@Composable
fun PaymentScreen(
    amountMwk: String = "MWK 20,000",
    applicationType: String = "New Licence Statutory Fee",
    onBack: () -> Unit = {},
    onPaymentSuccess: (transactionRef: String) -> Unit = {}
) {
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var mobileNumber by remember { mutableStateOf("") }
    var pinText by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }
    var isPinPromptVisible by remember { mutableStateOf(false) }
    var receiptTxRef by remember { mutableStateOf<String?>(null) }

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
                        Text("Statutory Payment Screen", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Road Traffic Directorate • Fee Checkout", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("PAYMENT SUMMARY", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(amountMwk, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text(applicationType, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Payable to: Road Traffic Directorate (Malawi)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (receiptTxRef != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDBEAFE))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("✓ Payment Successful!", color = Color(0xFF1E40AF), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Transaction Ref: $receiptTxRef", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Amount Paid: $amountMwk", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Payment Method: ${selectedMethod?.title}", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Date & Time: ${Date()}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { onPaymentSuccess(receiptTxRef!!) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("View Application Status →", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("Select Payment Method", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    PaymentMethodOption(
                        title = "Airtel Money",
                        subtitle = "Fast mobile money payment",
                        icon = Icons.Default.PhoneAndroid,
                        isSelected = selectedMethod == PaymentMethod.AIRTEL_MONEY
                    ) {
                        selectedMethod = PaymentMethod.AIRTEL_MONEY
                        errorMessage = ""
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    PaymentMethodOption(
                        title = "TNM Mpamba",
                        subtitle = "TNM mobile wallet checkout",
                        icon = Icons.Default.PhoneAndroid,
                        isSelected = selectedMethod == PaymentMethod.TNM_MPAMBA
                    ) {
                        selectedMethod = PaymentMethod.TNM_MPAMBA
                        errorMessage = ""
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    PaymentMethodOption(
                        title = "Digital Banking",
                        subtitle = "Direct bank transfer / card checkout",
                        icon = Icons.Default.AccountBalance,
                        isSelected = selectedMethod == PaymentMethod.DIGITAL_BANKING
                    ) {
                        selectedMethod = PaymentMethod.DIGITAL_BANKING
                        errorMessage = ""
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                if (selectedMethod == PaymentMethod.AIRTEL_MONEY || selectedMethod == PaymentMethod.TNM_MPAMBA) {
                    item {
                        OutlinedTextField(
                            value = mobileNumber,
                            onValueChange = { mobileNumber = it },
                            label = { Text("Enter Mobile Money Number") },
                            placeholder = { Text("e.g. 0999 123 456") },
                            colors = blackTextFieldColors,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (errorMessage.isNotBlank()) {
                    item {
                        Text(errorMessage, color = Color(0xFFDC2626), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                item {
                    Button(
                        onClick = {
                            when {
                                selectedMethod == null -> {
                                    errorMessage = "Please select a payment method"
                                }
                                (selectedMethod == PaymentMethod.AIRTEL_MONEY || selectedMethod == PaymentMethod.TNM_MPAMBA) && mobileNumber.isBlank() -> {
                                    errorMessage = "Please enter your mobile money number"
                                }
                                else -> {
                                    errorMessage = ""
                                    isPinPromptVisible = true
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B))
                    ) {
                        Text("Pay $amountMwk", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    if (isPinPromptVisible) {
        AlertDialog(
            onDismissRequest = { isPinPromptVisible = false },
            title = { Text("Enter Mobile Money PIN", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Approve transaction of $amountMwk to RTD Malawi:")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = pinText,
                        onValueChange = { pinText = it },
                        label = { Text("PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isPinPromptVisible = false
                        receiptTxRef = "TXN-MW-2026-${(100000..999999).random()}"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B))
                ) {
                    Text("Approve Payment", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { isPinPromptVisible = false }) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }
}

@Composable
fun PaymentMethodOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1A3A6B).copy(alpha = 0.1f) else Color(0xFFF1F5F9)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onClick)
            Spacer(modifier = Modifier.width(12.dp))
            Icon(icon, contentDescription = null, tint = Color(0xFF1A3A6B), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    PaymentScreen()
}
