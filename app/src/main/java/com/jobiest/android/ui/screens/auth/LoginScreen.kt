package com.jobiest.android.ui.screens.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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

@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    onNavigateToSignup: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToVerify: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Official Jobiest Brand Header
            JobiestBrandHeader(markSize = 28.dp, fontSizeSp = 26)

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Welcome back",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = JobiestInk,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "Let's get your next application moving.",
                fontSize = 15.sp,
                color = JobiestMuted,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Social Authentication Buttons (Google & LinkedIn matching web components)
            GoogleSignInButton(
                onClick = { launchOAuth("google") },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinkedInSignInButton(
                onClick = { launchOAuth("linkedin_oidc") },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = JobiestBorder)
                Text(
                    text = "  or sign in with email  ",
                    fontSize = 13.sp,
                    color = JobiestMuted2
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = JobiestBorder)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error alert
            if (errorMessage != null) {
                Surface(
                    color = JobiestDangerBg,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JobiestDangerBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = errorMessage!!,
                            color = JobiestDanger,
                            fontSize = 13.5.sp,
                            lineHeight = 18.sp
                        )
                        if (errorMessage!!.contains("verify your email", ignoreCase = true)) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Enter verification code →",
                                color = JobiestCobalt,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    onNavigateToVerify(email.trim())
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

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
                    unfocusedTextColor = JobiestInk,
                    focusedContainerColor = JobiestBg,
                    unfocusedContainerColor = JobiestBg
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

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
                            contentDescription = if (showPassword) "Hide password" else "Show password",
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
                    unfocusedTextColor = JobiestInk,
                    focusedContainerColor = JobiestBg,
                    unfocusedContainerColor = JobiestBg
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Forgot password?",
                    color = JobiestCobalt,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onNavigateToForgotPassword() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign In Button
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter both email and password."
                        return@Button
                    }
                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        val result = authRepository.login(email.trim(), password)
                        isLoading = false
                        if (result.isSuccess) {
                            onLoginSuccess()
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Sign in failed"
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
                    Text("Sign in", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up Footer Link
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account? ", color = JobiestMuted, fontSize = 14.sp)
                Text(
                    text = "Sign up",
                    color = JobiestCobalt,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToSignup() }
                )
            }
        }
    }
}
