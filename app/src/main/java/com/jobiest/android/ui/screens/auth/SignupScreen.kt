package com.jobiest.android.ui.screens.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobiest.android.BuildConfig
import com.jobiest.android.data.repository.AuthRepository
import com.jobiest.android.ui.components.*
import com.jobiest.android.ui.theme.*
import kotlinx.coroutines.launch

val COUNTRY_CODES = listOf(
    "+234" to "🇳🇬 +234 (Nigeria)",
    "+233" to "🇬🇭 +233 (Ghana)",
    "+254" to "🇰🇪 +254 (Kenya)",
    "+27" to "🇿🇦 +27 (South Africa)",
    "+44" to "🇬🇧 +44 (UK)",
    "+1" to "🇺🇸 +1 (US/Canada)"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    authRepository: AuthRepository,
    onNavigateToLogin: () -> Unit,
    onSignupSubmitted: (String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedCountryCode by remember { mutableStateOf("+234") }
    var localPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var countryDropdownOpen by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val hasLength = password.length >= 8
    val hasNumber = password.any { it.isDigit() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }

    fun launchOAuth(provider: String) {
        val authUrl = "${BuildConfig.SUPABASE_AUTH_URL}authorize?provider=$provider&redirect_to=jobiest://auth/callback"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
        context.startActivity(intent)
    }

    Scaffold(
        containerColor = JobiestBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            JobiestBrandHeader(markSize = 28.dp, fontSizeSp = 26)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Create your account",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = JobiestInk,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "Start applying with your verified facts.",
                fontSize = 15.sp,
                color = JobiestMuted,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Social Sign In Buttons
            GoogleSignInButton(
                onClick = { launchOAuth("google") },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(10.dp))

            LinkedInSignInButton(
                onClick = { launchOAuth("linkedin_oidc") },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = JobiestBorder)
                Text(
                    text = "  or register with email  ",
                    fontSize = 13.sp,
                    color = JobiestMuted2
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = JobiestBorder)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error alert
            if (errorMessage != null) {
                Surface(
                    color = JobiestDangerBg,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JobiestDangerBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        color = JobiestDanger,
                        fontSize = 13.5.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Full Name Field
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it; errorMessage = null },
                label = { Text("Full name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JobiestCobalt,
                    unfocusedBorderColor = JobiestBorder,
                    focusedLabelColor = JobiestCobalt,
                    unfocusedLabelColor = JobiestMuted,
                    focusedTextColor = JobiestInk,
                    unfocusedTextColor = JobiestInk
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Phone Field with Country Code Picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.width(110.dp)) {
                    OutlinedTextField(
                        value = selectedCountryCode,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Select country",
                                modifier = Modifier.clickable { countryDropdownOpen = true }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JobiestBorder,
                            unfocusedBorderColor = JobiestBorder,
                            focusedTextColor = JobiestInk,
                            unfocusedTextColor = JobiestInk
                        )
                    )
                    DropdownMenu(
                        expanded = countryDropdownOpen,
                        onDismissRequest = { countryDropdownOpen = false }
                    ) {
                        COUNTRY_CODES.forEach { (code, label) ->
                            DropdownMenuItem(
                                text = { Text(label, fontSize = 14.sp) },
                                onClick = {
                                    selectedCountryCode = code
                                    countryDropdownOpen = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = localPhone,
                    onValueChange = { localPhone = it.filter { char -> char.isDigit() }; errorMessage = null },
                    label = { Text("Phone number") },
                    placeholder = { Text("801 234 5678", color = JobiestMuted2) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JobiestCobalt,
                        unfocusedBorderColor = JobiestBorder,
                        focusedLabelColor = JobiestCobalt,
                        unfocusedLabelColor = JobiestMuted,
                        focusedTextColor = JobiestInk,
                        unfocusedTextColor = JobiestInk
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text("Email address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JobiestCobalt,
                    unfocusedBorderColor = JobiestBorder,
                    focusedLabelColor = JobiestCobalt,
                    unfocusedLabelColor = JobiestMuted,
                    focusedTextColor = JobiestInk,
                    unfocusedTextColor = JobiestInk
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showPassword) "Hide" else "Show",
                            tint = JobiestMuted
                        )
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JobiestCobalt,
                    unfocusedBorderColor = JobiestBorder,
                    focusedLabelColor = JobiestCobalt,
                    unfocusedLabelColor = JobiestMuted,
                    focusedTextColor = JobiestInk,
                    unfocusedTextColor = JobiestInk
                )
            )

            // Password Rules Checklist (matching web validation)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PasswordRuleItem("At least 8 characters", hasLength)
                PasswordRuleItem("At least one number", hasNumber)
                PasswordRuleItem("At least one special character", hasSpecial)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    val name = fullName.trim().replace("\\s+".toRegex(), " ")
                    val digits = localPhone.filter { it.isDigit() }
                    if (name.length < 2) {
                        errorMessage = "Please enter your full name."
                        return@Button
                    }
                    if (digits.length < 7 || digits.length > 12) {
                        errorMessage = "Please enter a valid phone number."
                        return@Button
                    }
                    if (!hasLength || !hasNumber || !hasSpecial) {
                        errorMessage = "Password does not meet the security requirements."
                        return@Button
                    }
                    val fullPhone = "$selectedCountryCode$digits"

                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        val result = authRepository.serverSignup(name, email.trim(), fullPhone, password)
                        isLoading = false
                        if (result.isSuccess) {
                            onSignupSubmitted(email.trim().lowercase())
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Could not create account."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = JobiestInk,
                    contentColor = JobiestBg
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = JobiestBg, strokeWidth = 2.dp)
                } else {
                    Text("Create account", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account? ", color = JobiestMuted, fontSize = 14.sp)
                Text(
                    text = "Sign in",
                    color = JobiestCobalt,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Composable
private fun PasswordRuleItem(label: String, passed: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = if (passed) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (passed) JobiestSuccess else JobiestMuted2,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            fontSize = 12.5.sp,
            color = if (passed) JobiestSuccess else JobiestMuted2
        )
    }
}
