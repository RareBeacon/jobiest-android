package com.jobiest.android.ui.screens.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jobiest.android.data.repository.JobRepository
import com.jobiest.android.network.models.JobDto
import com.jobiest.android.ui.components.EmptyStateView
import com.jobiest.android.ui.components.JobCard
import com.jobiest.android.ui.components.LoadingView
import com.jobiest.android.ui.theme.JobiestNavy
import com.jobiest.android.ui.theme.JobiestNavyDark
import com.jobiest.android.ui.theme.TextPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedJobsScreen(
    jobRepository: JobRepository,
    onNavigateBack: () -> Unit,
    onJobClick: (JobDto) -> Unit
) {
    var savedJobs by remember { mutableStateOf<List<JobDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    fun load() {
        scope.launch {
            isLoading = true
            val res = jobRepository.getSavedJobs()
            isLoading = false
            if (res.isSuccess) {
                savedJobs = res.getOrNull()?.mapNotNull { it.job } ?: emptyList()
            }
        }
    }

    LaunchedEffect(Unit) {
        load()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Jobs", color = TextPrimary) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                LoadingView()
            } else if (savedJobs.isEmpty()) {
                EmptyStateView(
                    title = "No saved jobs",
                    subtitle = "Bookmark open roles while browsing to track them here."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(savedJobs) { job ->
                        JobCard(
                            job = job.copy(isSaved = true),
                            onClick = { onJobClick(job) },
                            onToggleSave = {
                                scope.launch {
                                    jobRepository.toggleSaveJob(job.id, true)
                                    savedJobs = savedJobs.filter { it.id != job.id }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
