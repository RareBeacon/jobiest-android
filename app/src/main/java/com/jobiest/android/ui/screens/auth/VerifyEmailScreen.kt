package com.jobiest.android.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobiest.android.data.repository.AuthRepository
import com.jobiest.android.ui.components.JobiestBrandHeader
import com.jobiest.android.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VerifyEmailScreen(
    email: String,
    authRepository: AuthRepository,
    onVerificationSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isResending by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }
    var resendCooldown by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    // Resend countdown timer
    LaunchedEffect(resendCooldown) {
        if (resendCooldown > 0) {
            delay(1000L)
            resendCooldown -= 1
        }
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
            JobiestBrandHeader(markSize = 28.dp, fontSizeSp = 26)

            Spacer(modifier = Modifier.height(36.dp))

            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(16.dp),
                color = JobiestBrandSoft
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = null,
                        tint = JobiestInk,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Check your email",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = JobiestInk,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "We sent a 6-digit confirmation code to\n$email",
                fontSize = 15.sp,
                color = JobiestMuted,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

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
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (infoMessage != null) {
                Surface(
                    color = JobiestSuccessBg,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JobiestSuccessBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = infoMessage!!,
                        color = JobiestSuccess,
                        fontSize = 13.5.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 6-digit code input
            OutlinedTextField(
                value = code,
                onValueChange = {
                    if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                        code = it
                        errorMessage = null
                    }
                },
                label = { Text("6-digit verification code") },
                placeholder = { Text("123456", color = JobiestMuted2) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
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

            Spacer(modifier = Modifier.height(24.dp))

            // Verify button
            Button(
                onClick = {
                    if (code.length != 6) {
                        errorMessage = "Please enter the complete 6-digit code."
                        return@Button
                    }
                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        val result = authRepository.verifyEmail(email, code)
                        isLoading = false
                        if (result.isSuccess) {
                            onVerificationSuccess()
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Verification failed."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = !isLoading && code.length == 6,
                colors = ButtonDefaults.buttonColors(
                    containerColor = JobiestInk,
                    contentColor = JobiestBg
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = JobiestBg, strokeWidth = 2.dp)
                } else {
                    Text("Verify & Continue", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Resend code action
            if (resendCooldown > 0) {
                Text(
                    text = "Resend code in ${resendCooldown}s",
                    color = JobiestMuted2,
                    fontSize = 14.sp
                )
            } else {
                Text(
                    text = if (isResending) "Sending..." else "Didn't receive a code? Resend",
                    color = JobiestCobalt,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        if (isResending) return@clickable
                        isResending = true
                        errorMessage = null
                        scope.launch {
                            val res = authRepository.resendVerificationCode(email)
                            isResending = false
                            if (res.isSuccess) {
                                infoMessage = res.getOrNull()
                                resendCooldown = 60
                            } else {
                                errorMessage = res.exceptionOrNull()?.message
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Back to sign in",
                color = JobiestMuted,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onBackToLogin() }
            )
        }
    }
}
