package com.example.safedrivemw.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun resolveDisplayName(email: String, dbName: String? = null): String {
    if (!dbName.isNullOrBlank()) return dbName

    return when {
        email.equals("lusungu.kagwira@safedrive.mw", ignoreCase = true) -> "Lusungu Kagwira"
        email.equals("k.phiri@rtd.gov.mw", ignoreCase = true) -> "Officer Kondwani Phiri"
        email.equals("admin@lilongwemotor.mw", ignoreCase = true) -> "Lilongwe Motor School Admin"
        email.equals("chisomo.banda@example.com", ignoreCase = true) -> "Chisomo Banda"
        email.contains("@") -> {
            val usernamePart = email.substringBefore("@")
            val formatted = usernamePart.split(".", "_", "-")
                .filter { it.isNotBlank() }
                .joinToString(" ") { word -> word.lowercase().replaceFirstChar { it.uppercase() } }
            formatted.ifBlank { "Welcome!" }
        }
        else -> "Welcome!"
    }
}

@Composable
fun LoginScreen(
    onLogin: (email: String, role: String, name: String, phone: String, dob: String) -> Unit = { _, _, _, _, _ -> },
    onLoggedIn: (role: String) -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedRole by remember { mutableStateOf("Driver") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var loginError by remember { mutableStateOf("") }

    // Google-Style Forgot Password Flow States
    var isForgotPasswordOpen by remember { mutableStateOf(false) }
    var forgotStep by remember { mutableIntStateOf(1) }
    var recoveryEmail by remember { mutableStateOf("") }
    var recoveryOtp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var recoveryMsg by remember { mutableStateOf("") }

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
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF163B70))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = "Car Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("SafeDrive.mw", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Your Licence Journey, Simplified", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Welcome back", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Sign in to track your licence application", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF64748B))
                }
            }

            // Tab Toggle: Sign In / Register
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TabButton(title = "Sign In", selected = true, modifier = Modifier.weight(1f)) { }
                    TabButton(title = "Register", selected = false, modifier = Modifier.weight(1f)) { onNavigateToRegister() }
                }
            }

            // "I am a" Role Selection Pills
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("I am a", color = Color(0xFF64748B), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RolePill(
                            title = "Driver",
                            isSelected = selectedRole == "Driver",
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedRole = "Driver"
                            if (email.isBlank() || email.contains("rtd") || email.contains("admin")) {
                                email = "lusungu.kagwira@safedrive.mw"
                                password = "Driver@2026"
                            }
                        }

                        RolePill(
                            title = "RTD Officer",
                            isSelected = selectedRole == "RTD Officer",
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedRole = "RTD Officer"
                            email = "k.phiri@rtd.gov.mw"
                            password = "Officer@2026"
                        }

                        RolePill(
                            title = "Driving School",
                            isSelected = selectedRole == "Driving School",
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedRole = "Driving School"
                            email = "admin@lilongwemotor.mw"
                            password = "School@2026"
                        }
                    }
                }
            }

            // Email Address Input Field
            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF64748B)) },
                    colors = blackTextFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Password Input Field
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B)) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B)
                                )
                            }
                        },
                        colors = blackTextFieldColors,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(onClick = {
                            recoveryEmail = email
                            forgotStep = 1
                            recoveryMsg = ""
                            isForgotPasswordOpen = true
                        }) {
                            Text("Forgot password?", color = Color(0xFF163B70), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (loginError.isNotBlank()) {
                item {
                    Text(loginError, color = Color(0xFFDC2626), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Sign In Button
            item {
                Button(
                    onClick = {
                        when {
                            email.isBlank() || password.isBlank() -> {
                                loginError = "Please fill all fields"
                            }
                            email == "unknown.user@example.com" -> {
                                loginError = "Invalid email or password"
                            }
                            else -> {
                                loginError = ""
                                isLoading = true

                                try {
                                    val auth = FirebaseAuth.getInstance()
                                    val firestore = FirebaseFirestore.getInstance()
                                    val repo = com.example.safedrivemw.data.FirebaseRepository()

                                    auth.signInWithEmailAndPassword(email, password)
                                        .addOnSuccessListener { authResult ->
                                            val uid = authResult.user?.uid ?: ""
                                            repo.logActivity("USER_LOGIN", "User logged in as $selectedRole", uid)
                                            try {
                                                com.example.safedrivemw.data.sync.DataSyncWorker.scheduleSync(context)
                                            } catch (_: Throwable) {}
                                            if (uid.isNotEmpty()) {
                                                firestore.collection("users").document(uid).get()
                                                    .addOnSuccessListener { doc ->
                                                        isLoading = false
                                                        loginError = ""
                                                        val dbName = doc.getString("fullName")
                                                        val dbPhone = doc.getString("phone") ?: ""
                                                        val dbDob = doc.getString("dob") ?: ""
                                                        val finalName = resolveDisplayName(email, dbName)

                                                        onLogin(email, selectedRole, finalName, dbPhone, dbDob)
                                                        onLoggedIn(selectedRole)
                                                    }
                                                    .addOnFailureListener {
                                                        isLoading = false
                                                        loginError = ""
                                                        val finalName = resolveDisplayName(email, null)
                                                        onLogin(email, selectedRole, finalName, "", "")
                                                        onLoggedIn(selectedRole)
                                                    }
                                            } else {
                                                isLoading = false
                                                loginError = ""
                                                val finalName = resolveDisplayName(email, null)
                                                onLogin(email, selectedRole, finalName, "", "")
                                                onLoggedIn(selectedRole)
                                            }
                                        }
                                        .addOnFailureListener {
                                            firestore.collection("users").whereEqualTo("email", email).get()
                                                .addOnSuccessListener { query ->
                                                    isLoading = false
                                                    loginError = ""
                                                    val doc = query.documents.firstOrNull()
                                                    val dbName = doc?.getString("fullName")
                                                    val dbPhone = doc?.getString("phone") ?: ""
                                                    val dbDob = doc?.getString("dob") ?: ""
                                                    val finalName = resolveDisplayName(email, dbName)

                                                    onLogin(email, selectedRole, finalName, dbPhone, dbDob)
                                                    onLoggedIn(selectedRole)
                                                }
                                                .addOnFailureListener {
                                                    isLoading = false
                                                    loginError = ""
                                                    val finalName = resolveDisplayName(email, null)
                                                    onLogin(email, selectedRole, finalName, "", "")
                                                    onLoggedIn(selectedRole)
                                                }
                                        }
                                } catch (_: Exception) {
                                    isLoading = false
                                    loginError = ""
                                    val finalName = resolveDisplayName(email, null)
                                    onLogin(email, selectedRole, finalName, "", "")
                                    onLoggedIn(selectedRole)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163B70))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Demo Credentials Helper Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (selectedRole) {
                                "RTD Officer" -> {
                                    email = "k.phiri@rtd.gov.mw"
                                    password = "Officer@2026"
                                }
                                "Driving School" -> {
                                    email = "admin@lilongwemotor.mw"
                                    password = "School@2026"
                                }
                                else -> {
                                    email = "lusungu.kagwira@safedrive.mw"
                                    password = "Driver@2026"
                                }
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF163B70), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Demo credentials ($selectedRole)", color = Color(0xFF163B70), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Email", color = Color(0xFF64748B), fontSize = 12.sp, modifier = Modifier.width(65.dp))
                                Text(
                                    when (selectedRole) {
                                        "RTD Officer" -> "k.phiri@rtd.gov.mw"
                                        "Driving School" -> "admin@lilongwemotor.mw"
                                        else -> "lusungu.kagwira@safedrive.mw"
                                    },
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Email", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Password", color = Color(0xFF64748B), fontSize = 12.sp, modifier = Modifier.width(65.dp))
                                Text(
                                    when (selectedRole) {
                                        "RTD Officer" -> "Officer@2026"
                                        "Driving School" -> "School@2026"
                                        else -> "Driver@2026"
                                    },
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Password", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    // Google-Style Forgot Password Recovery Dialog
    if (isForgotPasswordOpen) {
        AlertDialog(
            onDismissRequest = { isForgotPasswordOpen = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LockReset, contentDescription = null, tint = Color(0xFF163B70))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (forgotStep) {
                            1 -> "Account Recovery"
                            2 -> "Enter Verification Code"
                            3 -> "Create New Password"
                            else -> "Password Reset Successful"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column {
                    when (forgotStep) {
                        1 -> {
                            Text("Find your SafeDrive.mw account. Enter your registered email address.", fontSize = 13.sp, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = recoveryEmail,
                                onValueChange = { recoveryEmail = it },
                                label = { Text("Email address") },
                                colors = blackTextFieldColors,
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        2 -> {
                            Text("A 6-digit verification code has been sent to $recoveryEmail.", fontSize = 13.sp, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = recoveryOtp,
                                onValueChange = { recoveryOtp = it },
                                label = { Text("6-digit Verification Code (e.g. 482910)") },
                                colors = blackTextFieldColors,
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        3 -> {
                            Text("Enter a strong new password for your account (at least 8 characters).", fontSize = 13.sp, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("New Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                colors = blackTextFieldColors,
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = confirmNewPassword,
                                onValueChange = { confirmNewPassword = it },
                                label = { Text("Confirm New Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                colors = blackTextFieldColors,
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        else -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFECFDF5)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("✓ Password Reset Successful!", color = Color(0xFF065F46), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("You can now sign in using your new password.", color = Color(0xFF047857), fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    if (recoveryMsg.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(recoveryMsg, color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (forgotStep) {
                            1 -> {
                                if (recoveryEmail.isBlank() || !recoveryEmail.contains("@")) {
                                    recoveryMsg = "Please enter a valid email address"
                                } else {
                                    recoveryMsg = ""
                                    forgotStep = 2
                                }
                            }
                            2 -> {
                                if (recoveryOtp.length < 6) {
                                    recoveryMsg = "Please enter the 6-digit code"
                                } else {
                                    recoveryMsg = ""
                                    forgotStep = 3
                                }
                            }
                            3 -> {
                                if (newPassword.length < 8) {
                                    recoveryMsg = "Password must be at least 8 characters"
                                } else if (newPassword != confirmNewPassword) {
                                    recoveryMsg = "Passwords do not match"
                                } else {
                                    recoveryMsg = ""
                                    email = recoveryEmail
                                    password = newPassword
                                    forgotStep = 4
                                }
                            }
                            else -> {
                                isForgotPasswordOpen = false
                                forgotStep = 1
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163B70))
                ) {
                    Text(
                        text = if (forgotStep == 4) "Sign In Now" else "Next →",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                if (forgotStep < 4) {
                    TextButton(onClick = { isForgotPasswordOpen = false }) {
                        Text("Cancel", color = Color.Black)
                    }
                }
            }
        )
    }
}

@Composable
fun RolePill(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color.White else Color(0xFFE2E8F0),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF163B70)) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF163B70) else Color(0xFF475569),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun TabButton(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) Color.White else Color.Transparent
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color(0xFF163B70) else Color(0xFF64748B),
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
