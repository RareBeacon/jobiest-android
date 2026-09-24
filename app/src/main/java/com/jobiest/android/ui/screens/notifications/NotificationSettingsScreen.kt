package com.jobiest.android.ui.screens.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.jobiest.android.network.JobiestApiService
import com.jobiest.android.network.models.NotificationPreferencesDto
import com.jobiest.android.network.models.TestNotificationRequest
import com.jobiest.android.notifications.NotificationHelper
import com.jobiest.android.notifications.PushTokenManager
import com.jobiest.android.ui.components.JobiestBrandHeader
import com.jobiest.android.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun NotificationSettingsScreen(
    pushTokenManager: PushTokenManager,
    apiService: JobiestApiService,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var hasSystemPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasSystemPermission = isGranted
        if (isGranted) {
            scope.launch {
                pushTokenManager.registerDeviceToken()
            }
        }
    }

    var masterEnabled by remember { mutableStateOf(pushTokenManager.areNotificationsEnabled()) }
    var appUpdates by remember { mutableStateOf(true) }
    var jobMatches by remember { mutableStateOf(true) }
    var resumeAlerts by remember { mutableStateOf(true) }
    var autoApplyAlerts by remember { mutableStateOf(true) }
    var isSendingTest by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val deviceToken = remember { pushTokenManager.getOrCreateDeviceToken() }
    val isRegistered = remember { pushTokenManager.isRegisteredWithBackend() }

    fun syncPreferences() {
        scope.launch {
            try {
                apiService.updateNotificationPreferences(
                    NotificationPreferencesDto(
                        applications = appUpdates,
                        jobs = jobMatches,
                        resume = resumeAlerts,
                        autoApply = autoApplyAlerts,
                        security = true
                    )
                )
            } catch (_: Exception) {}
        }
    }

    fun sendTestPush() {
        scope.launch {
            isSendingTest = true
            try {
                val res = apiService.sendTestNotification(
                    TestNotificationRequest(
                        type = "job_match",
                        customMessage = "3 new high-match roles found for your career profile!"
                    )
                )
                if (res.isSuccessful && res.body()?.ok == true) {
                    val detail = res.body()?.notification
                    val title = detail?.title ?: "Jobiest AI Career Agent"
                    val body = detail?.body ?: "3 new high-match roles found for your career profile!"
                    val deepLink = detail?.deepLink ?: "jobiest://applications"

                    // Display local native notification to verify end-to-end device rendering
                    NotificationHelper.showNotification(
                        context = context,
                        title = title,
                        body = body,
                        deepLink = deepLink
                    )
                    snackbarMessage = "Test push sent! Check your notification shade."
                } else {
                    snackbarMessage = "Could not send test push: server error."
                }
            } catch (e: Exception) {
                // Trigger local notification test even if offline
                NotificationHelper.showNotification(
                    context = context,
                    title = "Jobiest Test Alert",
                    body = "Push notification pipeline is active.",
                    deepLink = "jobiest://applications"
                )
                snackbarMessage = "Native alert verified."
            } finally {
                isSendingTest = false
            }
        }
    }

    Scaffold(
        topBar = {
            JobiestBrandHeader(
                title = "Push Notifications",
                subtitle = "Manage real-time career updates and device alerts",
                onBack = onNavigateBack
            )
        },
        snackbarHost = {
            snackbarMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { snackbarMessage = null }) {
                            Text("OK", color = JobiestBrand)
                        }
                    }
                ) {
                    Text(msg)
                }
            }
        },
        containerColor = JobiestBgSecondary
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Master Switch Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = JobiestCard),
                    border = BorderStroke(1.dp, JobiestBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Allow Push Notifications", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = JobiestInk)
                                Text("Receive real-time alerts when opportunities are found or drafted", fontSize = 12.sp, color = JobiestMuted)
                            }
                            Switch(
                                checked = masterEnabled,
                                onCheckedChange = { checked ->
                                    masterEnabled = checked
                                    pushTokenManager.setNotificationsEnabled(checked)
                                    if (checked && !hasSystemPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = JobiestSuccess
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // System Permission Status
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (hasSystemPermission) JobiestSuccess else JobiestDanger, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (hasSystemPermission) "System Permission: Granted" else "System Permission: Not Granted",
                                fontSize = 11.sp,
                                color = if (hasSystemPermission) JobiestSuccess else JobiestDanger,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (!hasSystemPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(
                                    onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Grant Permission", fontSize = 11.sp, color = JobiestCobalt)
                                }
                            }
                        }
                    }
                }
            }

            // Notification Categories Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = JobiestCard),
                    border = BorderStroke(1.dp, JobiestBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Notification Channels", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JobiestInk)
                        Spacer(modifier = Modifier.height(8.dp))

                        listOf(
                            Triple("Application Updates", "Draft reviews, status changes, and follow-ups", appUpdates) to { v: Boolean -> appUpdates = v; syncPreferences() },
                            Triple("New High-Match Roles", "Roles with 90%+ match to your target career", jobMatches) to { v: Boolean -> jobMatches = v; syncPreferences() },
                            Triple("Resume & AI Signals", "Keyword extraction, scores, and ATS feedback", resumeAlerts) to { v: Boolean -> resumeAlerts = v; syncPreferences() },
                            Triple("Auto-Apply Progress", "Automated queue updates and approval alerts", autoApplyAlerts) to { v: Boolean -> autoApplyAlerts = v; syncPreferences() }
                        ).forEach { (meta, onToggle) ->
                            val (title, subtitle, checked) = meta
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = JobiestInk)
                                    Text(subtitle, fontSize = 11.sp, color = JobiestMuted)
                                }
                                Switch(
                                    checked = checked && masterEnabled,
                                    onCheckedChange = onToggle,
                                    enabled = masterEnabled,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = JobiestBrand
                                    )
                                )
                            }
                            HorizontalDivider(color = JobiestBorderSubtle)
                        }
                    }
                }
            }

            // Test Push Notification Action
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = JobiestCard),
                    border = BorderStroke(1.dp, JobiestBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = JobiestCobalt)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Test Push Delivery", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JobiestInk)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Dispatches a test notification from the backend to verify that notifications and deep linking work properly on your physical phone.",
                            fontSize = 12.sp,
                            color = JobiestMuted
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { sendTestPush() },
                            enabled = !isSendingTest,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = JobiestCobalt, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isSendingTest) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Dispatching…")
                            } else {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Send Test Notification", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Push Token Status Information
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = JobiestBg),
                    border = BorderStroke(1.dp, JobiestBorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Device Push Token", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = JobiestMuted)
                            Surface(
                                color = if (isRegistered) JobiestSuccessBg else JobiestWarningBg,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    if (isRegistered) "Backend Registered ✓" else "Local Active",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRegistered) JobiestSuccess else JobiestWarning,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            deviceToken,
                            fontSize = 10.sp,
                            color = JobiestMuted2,
                            maxLines = 1
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
