package com.jobiest.android.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobiest.android.data.repository.AuthRepository
import com.jobiest.android.data.repository.ProfileRepository
import com.jobiest.android.network.models.JobPreferencesDto
import com.jobiest.android.network.models.ProfileDto
import com.jobiest.android.ui.components.LoadingView
import com.jobiest.android.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    profileRepository: ProfileRepository,
    authRepository: AuthRepository,
    onNavigateToBilling: () -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    onSignedOut: () -> Unit
) {

    var profile by remember { mutableStateOf<ProfileDto?>(null) }
    var preferences by remember { mutableStateOf<JobPreferencesDto?>(null) }
    var completenessPercent by remember { mutableIntStateOf(80) }
    var isLoading by remember { mutableStateOf(true) }
    var isAutoMode by remember { mutableStateOf(false) }
    var showSignoutDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val pRes = profileRepository.getProfile()
        if (pRes.isSuccess) profile = pRes.getOrNull()

        val prefRes = profileRepository.getPreferences()
        if (prefRes.isSuccess) {
            preferences = prefRes.getOrNull()
            isAutoMode = preferences?.applicationMode == "auto"
        }

        val compRes = profileRepository.getCompleteness()
        if (compRes.isSuccess) completenessPercent = compRes.getOrNull()?.percent ?: 80

        isLoading = false
    }

    if (isLoading) {
        LoadingView("Loading profile…")
        return
    }

    Scaffold(
        containerColor = JobiestBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            Surface(
                color = JobiestBgSecondary,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, JobiestBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = profile?.fullName ?: "Candidate",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = JobiestInk
                    )
                    Text(
                        text = profile?.email ?: "",
                        fontSize = 14.sp,
                        color = JobiestMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Profile strength: $completenessPercent%",
                        fontSize = 12.5.sp,
                        color = JobiestCobalt,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { completenessPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = JobiestCobalt,
                        trackColor = JobiestBorder,
                    )
                }
            }

            // Application Mode Settings Card (Matching web AppShell and Settings)
            Surface(
                color = JobiestBg,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, JobiestBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "APPLICATION MODE",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = JobiestCobalt,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAutoMode) "Delegated Auto-Apply" else "Manual Approval Mode",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = JobiestInk
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isAutoMode)
                                    "Your AI agent applies within your configured rules. You are emailed after every submission."
                                else
                                    "Nothing is ever sent without your explicit approval.",
                                fontSize = 13.sp,
                                color = JobiestMuted,
                                lineHeight = 18.sp
                            )
                        }

                        Switch(
                            checked = isAutoMode,
                            onCheckedChange = { checked ->
                                isAutoMode = checked
                                val newMode = if (checked) "auto" else "approval"
                                scope.launch {
                                    profileRepository.setApplicationMode(newMode)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = JobiestBg,
                                checkedTrackColor = JobiestCobalt,
                                uncheckedThumbColor = JobiestMuted2,
                                uncheckedTrackColor = JobiestBorder
                            )
                        )
                    }
                }
            }

            // Quick Actions Card
            Surface(
                color = JobiestBg,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, JobiestBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.Notifications,
                        title = "Push Notifications & Alerts",
                        subtitle = "Real-time updates, background scans, and device alerts",
                        onClick = onNavigateToNotifications
                    )
                    HorizontalDivider(color = JobiestBorder)
                    SettingsRow(
                        icon = Icons.Default.CreditCard,
                        title = "Billing & Quotas",
                        subtitle = "Manage plan, document credits, and subscription",
                        onClick = onNavigateToBilling
                    )
                    HorizontalDivider(color = JobiestBorder)
                    SettingsRow(
                        icon = Icons.Default.Security,
                        title = "Security & Session",
                        subtitle = "Hardware-backed Keystore session protection",
                        onClick = {}
                    )
                    HorizontalDivider(color = JobiestBorder)
                    SettingsRow(
                        icon = Icons.Default.HelpOutline,
                        title = "Support & Help",
                        subtitle = "Open Jobiest support portal",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jobiest.com/help"))
                            context.startActivity(intent)
                        }
                    )
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

            // Sign out button
            OutlinedButton(
                onClick = { showSignoutDialog = true },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, JobiestDangerBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = JobiestDanger),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign out", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (showSignoutDialog) {
        AlertDialog(
            onDismissRequest = { showSignoutDialog = false },
            title = { Text("Sign out of Jobiest?", color = JobiestInk, fontWeight = FontWeight.Bold) },
            text = { Text("You will need to sign in again to access your applications and documents.", color = JobiestMuted) },
            confirmButton = {
                Button(
                    onClick = {
                        showSignoutDialog = false
                        authRepository.logout()
                        onSignedOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JobiestDanger)
                ) {
                    Text("Sign out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignoutDialog = false }) {
                    Text("Cancel", color = JobiestInk)
                }
            },
            containerColor = JobiestBg
        )
    }
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = JobiestCobalt,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp, color = JobiestInk)
            Text(text = subtitle, fontSize = 12.5.sp, color = JobiestMuted)
        }
    }
}
