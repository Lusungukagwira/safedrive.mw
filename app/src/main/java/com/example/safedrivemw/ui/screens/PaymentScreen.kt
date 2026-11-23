package com.example.safedrivemw.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safedrivemw.R
import com.google.firebase.firestore.FirebaseFirestore
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
    userEmail: String = "lusungu.kagwira@safedrive.mw",
    userPhone: String = "+265 999 123 456",
    onBack: () -> Unit = {},
    onPaymentSuccess: (transactionRef: String) -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var mobileNumber by remember { mutableStateOf(userPhone) }
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
                .imePadding()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF163B70))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("PAYMENT SUMMARY", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(amountMwk, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text(applicationType, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Payable to: Road Traffic Directorate (Malawi)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            if (receiptTxRef != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("✓ Payment Successful!", color = Color(0xFF065F46), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Transaction Ref: $receiptTxRef", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Amount Paid: $amountMwk", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Payment Method: ${selectedMethod?.title}", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Date & Time: ${Date()}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Email & SMS Notification Delivery Notice
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFD1FAE5)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("📧 Payment receipt emailed to $userEmail", color = Color(0xFF065F46), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("📱 SMS payment confirmation sent to $userPhone", color = Color(0xFF065F46), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { onPaymentSuccess(receiptTxRef!!) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
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
                    Spacer(modifier = Modifier.height(6.dp))

                    PaymentMethodOption(
                        title = "Airtel Money",
                        subtitle = "Fast mobile money payment",
                        logoResId = R.drawable.ic_airtel_money,
                        isSelected = selectedMethod == PaymentMethod.AIRTEL_MONEY
                    ) {
                        selectedMethod = PaymentMethod.AIRTEL_MONEY
                        errorMessage = ""
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    PaymentMethodOption(
                        title = "TNM Mpamba",
                        subtitle = "TNM mobile wallet checkout",
                        logoResId = R.drawable.ic_tnm_mpamba,
                        isSelected = selectedMethod == PaymentMethod.TNM_MPAMBA
                    ) {
                        selectedMethod = PaymentMethod.TNM_MPAMBA
                        errorMessage = ""
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    PaymentMethodOption(
                        title = "Digital Banking",
                        subtitle = "Direct bank transfer / card checkout",
                        icon = Icons.Default.AccountBalance,
                        isSelected = selectedMethod == PaymentMethod.DIGITAL_BANKING
                    ) {
                        selectedMethod = PaymentMethod.DIGITAL_BANKING
                        errorMessage = ""
                    }
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
                    }
                }

                if (errorMessage.isNotBlank()) {
                    item {
                        Text(errorMessage, color = Color(0xFFDC2626), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    Button(
                        onClick = {
                            val cleanNumber = mobileNumber.replace(" ", "").replace("+", "")
                            when {
                                selectedMethod == null -> {
                                    errorMessage = "Please select a payment method"
                                }
                                (selectedMethod == PaymentMethod.AIRTEL_MONEY || selectedMethod == PaymentMethod.TNM_MPAMBA) && mobileNumber.isBlank() -> {
                                    errorMessage = "Please enter your mobile money number"
                                }
                                (selectedMethod == PaymentMethod.AIRTEL_MONEY || selectedMethod == PaymentMethod.TNM_MPAMBA) && (cleanNumber.length < 9 || !cleanNumber.all { it.isDigit() }) -> {
                                    errorMessage = "Enter a valid mobile number"
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163B70))
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
                        val txId = "TXN-MW-2026-${(100000..999999).random()}"
                        receiptTxRef = txId

                        // Save payment record & log to Firebase Firestore Database via Repository
                        try {
                            val repo = com.example.safedrivemw.data.FirebaseRepository()
                            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "user_demo"
                            val payment = com.example.safedrivemw.data.PaymentTransaction(
                                paymentId = txId,
                                userId = uid,
                                txRef = txId,
                                paymentMethod = selectedMethod?.title ?: "Mobile Money",
                                amountPaid = amountMwk,
                                paymentDate = System.currentTimeMillis(),
                                paymentStatus = "SUCCESSFUL",
                                relatedApplicationId = "APP-2026-004",
                                userEmail = userEmail,
                                userPhone = userPhone,
                                confirmationDetails = "Approved via Mobile Money Gateway"
                            )
                            repo.recordPayment(context, payment)
                        } catch (_: Throwable) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163B70))
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
    icon: ImageVector? = null,
    logoResId: Int? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF163B70).copy(alpha = 0.1f) else Color(0xFFF1F5F9)
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

            if (logoResId != null) {
                Image(
                    painter = painterResource(id = logoResId),
                    contentDescription = title,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else if (icon != null) {
                Icon(icon, contentDescription = null, tint = Color(0xFF163B70), modifier = Modifier.size(28.dp))
            }

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
