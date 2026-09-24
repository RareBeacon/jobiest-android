package com.jobiest.android.ui.screens.applications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobiest.android.data.repository.ApplicationRepository
import com.jobiest.android.ui.components.JobiestButton
import com.jobiest.android.ui.components.JobiestTextField
import com.jobiest.android.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartApplicationScreen(
    applicationRepository: ApplicationRepository,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Link (Agent mode), 1: Manual Log
    var jobUrl by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var roleTitle by remember { mutableStateOf("") }
    var manualUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Start Application", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JobiestNavy)
            )
        },
        containerColor = JobiestNavyDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = JobiestNavy,
                contentColor = JobiestGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = JobiestGold
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("AI Agent Mode", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Manual Log", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (errorMessage != null) {
                Surface(
                    color = JobiestRed.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        color = JobiestRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (selectedTab == 0) {
                // AI Agent Mode
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = JobiestCardBg),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(JobiestBorder)
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Target Any Job Post",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Paste any employer career link (Greenhouse, Lever, LinkedIn, Workable, etc.). The agent will inspect requirements, tailor your resume, and prepare your draft for review.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        JobiestTextField(
                            value = jobUrl,
                            onValueChange = { jobUrl = it; errorMessage = null },
                            label = "Job Posting URL",
                            placeholder = "https://boards.greenhouse.io/company/jobs/..."
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        JobiestButton(
                            text = "Target with AI Agent",
                            isLoading = isLoading,
                            enabled = jobUrl.isNotBlank() && jobUrl.startsWith("http"),
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    errorMessage = null
                                    val res = applicationRepository.targetJob(jobUrl)
                                    isLoading = false
                                    if (res.isSuccess) {
                                        onSuccess()
                                    } else {
                                        errorMessage = res.exceptionOrNull()?.message ?: "Failed to target job"
                                    }
                                }
                            }
                        )
                    }
                }
            } else {
                // Manual Log Mode
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = JobiestCardBg),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(JobiestBorder)
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Log External Application",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Applied outside Jobiest? Record it here to keep your pipeline and stats synchronized.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        JobiestTextField(
                            value = companyName,
                            onValueChange = { companyName = it; errorMessage = null },
                            label = "Company Name",
                            placeholder = "e.g. Stripe, Paystack"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        JobiestTextField(
                            value = roleTitle,
                            onValueChange = { roleTitle = it; errorMessage = null },
                            label = "Job Title",
                            placeholder = "e.g. Senior Software Engineer"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        JobiestTextField(
                            value = manualUrl,
                            onValueChange = { manualUrl = it; errorMessage = null },
                            label = "Application URL",
                            placeholder = "https://..."
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        JobiestButton(
                            text = "Save to Applications",
                            isLoading = isLoading,
                            enabled = companyName.isNotBlank() && roleTitle.isNotBlank() && manualUrl.isNotBlank(),
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    errorMessage = null
                                    val res = applicationRepository.createManualApplication(
                                        company = companyName,
                                        title = roleTitle,
                                        url = manualUrl,
                                        status = "SUBMITTED"
                                    )
                                    isLoading = false
                                    if (res.isSuccess) {
                                        onSuccess()
                                    } else {
                                        errorMessage = res.exceptionOrNull()?.message ?: "Failed to log application"
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
