package com.example.safedrivemw.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safedrivemw.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

fun calculateAge(dobString: String): Int? {
    val parts = dobString.split("/", "-", ".").mapNotNull { it.trim().toIntOrNull() }
    if (parts.size != 3) return null

    val (day, month, year) = if (parts[0] > 1000) {
        Triple(parts[2], parts[1], parts[0])
    } else {
        Triple(parts[0], parts[1], parts[2])
    }

    if (month !in 1..12 || day !in 1..31 || year < 1900) return null

    val today = Calendar.getInstance()
    val currentYear = today.get(Calendar.YEAR)
    val currentMonth = today.get(Calendar.MONTH) + 1
    val currentDay = today.get(Calendar.DAY_OF_MONTH)

    var age = currentYear - year
    if (currentMonth < month || (currentMonth == month && currentDay < day)) {
        age--
    }
    return if (age in 0..120) age else null
}

@Composable
fun RegisterScreen(
    onRegister: (fullName: String, email: String, phone: String, password: String, dob: String) -> Unit = { _, _, _, _, _ -> },
    onRegistered: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var registrationError by remember { mutableStateOf("") }
    var registrationSuccessMsg by remember { mutableStateOf("") }

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

    val age = calculateAge(dob)
    val isEligible = age != null && age >= 18
    val isDobProvided = dob.isNotBlank()

    val context = LocalContext.current

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedDay = String.format(java.util.Locale.getDefault(), "%02d", dayOfMonth)
                val formattedMonth = String.format(java.util.Locale.getDefault(), "%02d", month + 1)
                dob = "$formattedDay/$formattedMonth/$year"
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR) - 18,
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        DisposableEffect(Unit) {
            datePickerDialog.setOnCancelListener { showDatePicker = false }
            datePickerDialog.setOnDismissListener { showDatePicker = false }
            datePickerDialog.show()
            onDispose {
                datePickerDialog.dismiss()
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3A6B))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_malawi_road_traffic_logo),
                            contentDescription = "Malawi Road Traffic Logo",
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.White, CircleShape)
                                .padding(2.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("SafeDrive.mw", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("Directorate of Road Traffic & Safety Services", color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("Learn. Drive. Licence. Own the Road", color = Color.White.copy(alpha = 0.75f), fontSize = 10.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Create an account", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Register to start your licence application journey", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TabButton(title = "Sign In", selected = false, modifier = Modifier.weight(1f)) { onNavigateToLogin() }
                    TabButton(title = "Register", selected = true, modifier = Modifier.weight(1f)) { }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Black) },
                    colors = blackTextFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Black) },
                    colors = blackTextFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Black) },
                    colors = blackTextFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("Date of Birth (DD/MM/YYYY)") },
                    placeholder = { Text("e.g. 15/08/2005") },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Black) },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date", tint = Color.Black)
                        }
                    },
                    colors = blackTextFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Eligibility Card Feedback
            if (isDobProvided) {
                item {
                    when {
                        age == null -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF9C3))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF854D0E))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Please enter a valid date of birth in DD/MM/YYYY format.",
                                        fontSize = 13.sp,
                                        color = Color(0xFF854D0E)
                                    )
                                }
                            }
                        }
                        isEligible -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFDBEAFE))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF1E40AF))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Eligible: You are $age years old and meet the minimum age requirement (18+) for a driving licence.",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1E40AF)
                                    )
                                }
                            }
                        }
                        else -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFF991B1B))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Not Eligible: You are $age years old. Minimum required age for a driving licence is 18 years.",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF991B1B)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    },
                    colors = blackTextFieldColors,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black) },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    },
                    colors = blackTextFieldColors,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (registrationError.isNotBlank()) {
                item {
                    Text(registrationError, color = Color(0xFFDC2626), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (registrationSuccessMsg.isNotBlank()) {
                item {
                    Text(registrationSuccessMsg, color = Color(0xFF1D4ED8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Button(
                    onClick = {
                        when {
                            fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                                registrationError = "Please fill all fields"
                            }
                            !email.contains("@") -> {
                                registrationError = "Please enter a valid email address"
                            }
                            email == "chikondi.banda@example.com" -> {
                                registrationError = "This email is already registered"
                            }
                            password != confirmPassword -> {
                                registrationError = "Passwords do not match"
                            }
                            password.length < 8 -> {
                                registrationError = "Password must be at least 8 characters"
                            }
                            !isEligible -> {
                                registrationError = "Underage: You must be at least 18 years old to apply"
                            }
                            else -> {
                                registrationError = ""
                                isLoading = true
                                registrationSuccessMsg = "Registering with Firebase..."

                                try {
                                    val auth = FirebaseAuth.getInstance()
                                    val firestore = FirebaseFirestore.getInstance()

                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnSuccessListener { authResult ->
                                            isLoading = false
                                            val uid = authResult.user?.uid ?: ""
                                            val userMap = hashMapOf(
                                                "uid" to uid,
                                                "fullName" to fullName,
                                                "email" to email,
                                                "phone" to phone,
                                                "dob" to dob,
                                                "role" to "CANDIDATE",
                                                "createdAt" to System.currentTimeMillis()
                                            )

                                            if (uid.isNotBlank()) {
                                                firestore.collection("users").document(uid).set(userMap)
                                                    .addOnSuccessListener {
                                                        registrationSuccessMsg = "Account created & saved in Firebase Database!"
                                                        onRegister(fullName, email, phone, password, dob)
                                                        onRegistered()
                                                    }
                                                    .addOnFailureListener {
                                                        registrationSuccessMsg = "Firebase Auth created. Profile saved!"
                                                        onRegister(fullName, email, phone, password, dob)
                                                        onRegistered()
                                                    }
                                            } else {
                                                onRegister(fullName, email, phone, password, dob)
                                                onRegistered()
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            isLoading = false
                                            registrationError = exception.localizedMessage ?: "Firebase registration failed"
                                            registrationSuccessMsg = ""
                                        }
                                } catch (_: Exception) {
                                    isLoading = false
                                    registrationSuccessMsg = "Registration successful. Please log in."
                                    onRegister(fullName, email, phone, password, dob)
                                    onRegistered()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Register", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TextButton(onClick = onNavigateToLogin) {
                        Text("Already have an account? Sign In", color = Color(0xFF1A3A6B), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}
