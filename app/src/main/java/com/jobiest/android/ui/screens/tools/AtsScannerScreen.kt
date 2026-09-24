package com.jobiest.android.ui.screens.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobiest.android.data.repository.CareerToolsRepository
import com.jobiest.android.network.models.AtsScanResponse
import com.jobiest.android.ui.components.JobiestButton
import com.jobiest.android.ui.components.JobiestTextField
import com.jobiest.android.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtsScannerScreen(
    careerToolsRepository: CareerToolsRepository,
    onNavigateBack: () -> Unit
) {
    var resumeText by remember { mutableStateOf("") }
    var jobDescText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<AtsScanResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ATS Resume Scanner", color = TextPrimary) },
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
            Text(
                text = "ATS Keyword & Gap Analysis",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Paste your resume and the target role description to check keyword alignment and ATS match score.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            JobiestTextField(
                value = resumeText,
                onValueChange = { resumeText = it; errorMessage = null },
                label = "Resume Text (Paste content)",
                placeholder = "Paste your full resume or summary text here...",
                singleLine = false,
                maxLines = 6,
                modifier = Modifier.height(140.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            JobiestTextField(
                value = jobDescText,
                onValueChange = { jobDescText = it; errorMessage = null },
                label = "Job Description (Paste content)",
                placeholder = "Paste the target job description and requirements...",
                singleLine = false,
                maxLines = 6,
                modifier = Modifier.height(140.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

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

            JobiestButton(
                text = "Run ATS Match Scan",
                isLoading = isLoading,
                enabled = resumeText.isNotBlank() && jobDescText.isNotBlank(),
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        val res = careerToolsRepository.scanAts(resumeText, jobDescText)
                        isLoading = false
                        if (res.isSuccess) {
                            scanResult = res.getOrNull()
                        } else {
                            errorMessage = res.exceptionOrNull()?.message ?: "Scan failed"
                        }
                    }
                }
            )

            if (scanResult != null) {
                val result = scanResult!!
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = JobiestCardBg),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(JobiestBorder)
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ATS Match Score",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "${result.score}%",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (result.score >= 75) JobiestGreen else if (result.score >= 50) JobiestGold else JobiestRed
                                )
                            )
                        }

                        LinearProgressIndicator(
                            progress = { result.score / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .padding(vertical = 4.dp),
                            color = if (result.score >= 75) JobiestGreen else if (result.score >= 50) JobiestGold else JobiestRed,
                            trackColor = JobiestBorder,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (result.matchedKeywords.isNotEmpty()) {
                            Text(
                                text = "Matched Keywords (${result.matchedKeywords.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = JobiestGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.matchedKeywords.joinToString(", "),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        if (result.missingKeywords.isNotEmpty()) {
                            Text(
                                text = "Missing Keywords (${result.missingKeywords.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = JobiestGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.missingKeywords.joinToString(", "),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
