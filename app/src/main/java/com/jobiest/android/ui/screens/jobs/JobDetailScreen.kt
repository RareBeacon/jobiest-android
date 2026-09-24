package com.jobiest.android.ui.screens.jobs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobiest.android.data.repository.ApplicationRepository
import com.jobiest.android.data.repository.JobRepository
import com.jobiest.android.network.models.JobDto
import com.jobiest.android.ui.components.JobiestButton
import com.jobiest.android.ui.components.JobiestOutlinedButton
import com.jobiest.android.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    job: JobDto,
    jobRepository: JobRepository,
    applicationRepository: ApplicationRepository,
    onNavigateBack: () -> Unit,
    onApplicationCreated: () -> Unit
) {
    var isSaved by remember { mutableStateOf(job.isSaved) }
    var isApplying by remember { mutableStateOf(false) }
    var actionMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(job.company ?: "Job Details", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "${job.title} at ${job.company}")
                            putExtra(Intent.EXTRA_TEXT, "Check out this role on Jobiest: ${job.title} at ${job.company}\n${job.url ?: "https://jobiest.com"}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Job"))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextPrimary
                        )
                    }
                    IconButton(onClick = {
                        scope.launch {
                            val res = jobRepository.toggleSaveJob(job.id, isSaved)
                            if (res.isSuccess) isSaved = res.getOrDefault(!isSaved)
                        }
                    }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (isSaved) JobiestGold else TextSecondary
                        )
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
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = JobiestCardBg),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(JobiestBorder)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = job.title ?: "Untitled Role",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = job.company ?: "Unknown Company",
                        style = MaterialTheme.typography.titleMedium,
                        color = JobiestGold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!job.location.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = TextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = job.location, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }

                    if (!job.source.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Source: ${job.source.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = JobiestBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action notification
            if (actionMessage != null) {
                Surface(
                    color = JobiestGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = actionMessage!!,
                        color = JobiestGreen,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // CTAs
            JobiestButton(
                text = "Apply with Jobiest Agent",
                isLoading = isApplying,
                onClick = {
                    scope.launch {
                        isApplying = true
                        actionMessage = null
                        if (!job.url.isNullOrBlank()) {
                            val res = applicationRepository.targetJob(job.url)
                            isApplying = false
                            if (res.isSuccess) {
                                actionMessage = "Application queued! Your agent is tailoring your resume and cover letter."
                                onApplicationCreated()
                            } else {
                                actionMessage = "Failed to queue application: ${res.exceptionOrNull()?.message}"
                            }
                        } else {
                            isApplying = false
                            actionMessage = "Job link is missing from this posting."
                        }
                    }
                }
            )

            if (!job.url.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                JobiestOutlinedButton(
                    text = "Open Employer Page",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.url))
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Job Description
            Text(
                text = "About this role",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = job.description?.ifBlank { "No detailed description provided by the employer." }
                    ?: "No description available.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 22.sp
            )
        }
    }
}
