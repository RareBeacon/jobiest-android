package com.jobiest.android.ui.screens.jobs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobiest.android.data.repository.JobRepository
import com.jobiest.android.network.models.JobDto
import com.jobiest.android.ui.components.EmptyView
import com.jobiest.android.ui.components.JobiestBrandHeader
import com.jobiest.android.ui.components.LoadingView
import com.jobiest.android.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun JobSearchScreen(
    jobRepository: JobRepository,
    onJobClick: (JobDto) -> Unit,
    onNavigateToSavedJobs: () -> Unit,
    onApplyWithAi: (JobDto) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All roles") }
    var selectedSource by remember { mutableStateOf<String?>(null) }
    var jobs by remember { mutableStateOf<List<JobDto>>(emptyList()) }
    var savedJobIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val filterPills = listOf("All roles", "90%+ match", "Remote", "New this week")
    val sourcePills = listOf("greenhouse", "lever", "ashby", "workable")

    fun search() {
        scope.launch {
            isLoading = true
            errorMessage = null
            val result = jobRepository.searchJobs(
                query = searchQuery.ifBlank { null },
                source = selectedSource
            )
            isLoading = false
            if (result.isSuccess) {
                val list = result.getOrNull()?.jobs ?: emptyList()
                jobs = when (selectedFilter) {
                    "90%+ match" -> list.filter { (it.matchScore ?: 85) >= 90 }
                    "Remote" -> list.filter { (it.location ?: "").contains("remote", ignoreCase = true) }
                    else -> list
                }
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Failed to load opportunities"
            }
        }
    }

    LaunchedEffect(selectedFilter, selectedSource) {
        search()
    }

    Scaffold(
        topBar = {
            JobiestBrandHeader(
                title = "Discover Roles",
                subtitle = "Ranked against your career goals and evidence",
                actions = {
                    IconButton(onClick = onNavigateToSavedJobs) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Saved Jobs",
                            tint = JobiestInk
                        )
                    }
                }
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // Search Bar
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = JobiestCard),
                    border = BorderStroke(1.dp, JobiestBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = JobiestMuted)
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search roles, companies, or skills", fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = JobiestInk)
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = ""; search() }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = JobiestMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                        Button(
                            onClick = { search() },
                            colors = ButtonDefaults.buttonColors(containerColor = JobiestInk, contentColor = Color.White),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Search", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Filter Pills (Horizontal Scroll)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterPills.forEach { pill ->
                        FilterChip(
                            selected = selectedFilter == pill,
                            onClick = { selectedFilter = pill },
                            label = { Text(pill, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = JobiestBrandSoft,
                                selectedLabelColor = JobiestInk,
                                containerColor = JobiestCard
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedFilter == pill,
                                borderColor = if (selectedFilter == pill) JobiestBrand else JobiestBorder
                            )
                        )
                    }

                    sourcePills.forEach { src ->
                        FilterChip(
                            selected = selectedSource == src,
                            onClick = { selectedSource = if (selectedSource == src) null else src },
                            label = { Text(src.replaceFirstChar { it.uppercase() }, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = JobiestBrandSoft,
                                selectedLabelColor = JobiestInk,
                                containerColor = JobiestCard
                            )
                        )
                    }
                }
            }

            // High-Leverage Search Insight Card
            item {
                Surface(
                    color = JobiestBrandSoft,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, JobiestBrand)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(JobiestBrand, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = JobiestInk, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Highest-Leverage Match Insight", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JobiestInk)
                            Text(
                                "Roles matching your SaaS systems leadership yield a 94% fit confidence. Your evidence matches current openings.",
                                fontSize = 11.sp,
                                color = JobiestInkSecondary
                            )
                        }
                    }
                }
            }

            // Results count
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${jobs.size} Opportunities Found",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = JobiestInk
                    )
                    TextButton(onClick = { search() }) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = JobiestCobalt)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Refresh", fontSize = 12.sp, color = JobiestCobalt)
                    }
                }
            }

            // Job List
            if (isLoading) {
                item { LoadingView("Loading curated opportunities…") }
            } else if (jobs.isEmpty()) {
                item {
                    EmptyView(
                        title = "No roles match your filter",
                        subtitle = "Try adjusting your search terms or clearing the source filter."
                    )
                }
            } else {
                items(jobs, key = { it.id }) { job ->
                    val isSaved = savedJobIds.contains(job.id) || job.isSaved
                    val matchScore = job.matchScore ?: 88
                    val fitReason = job.fitReason ?: "Strong overlap in systems design, cross-functional execution, and product craft."

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onJobClick(job) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = JobiestCard),
                        border = BorderStroke(1.dp, JobiestBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Top Row: Company badge, name, save button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(JobiestLogoNavy, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            (job.displayCompany.firstOrNull() ?: 'J').uppercase(),
                                            color = JobiestBrand,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(job.displayCompany, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JobiestInk)
                                        Text(job.location ?: "Remote", fontSize = 11.sp, color = JobiestMuted)
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            if (isSaved) {
                                                jobRepository.unsaveJob(job.id)
                                                savedJobIds = savedJobIds - job.id
                                                snackbarMessage = "Removed ${job.displayTitle} from saved jobs."
                                            } else {
                                                jobRepository.saveJob(job.id)
                                                savedJobIds = savedJobIds + job.id
                                                snackbarMessage = "Saved ${job.displayTitle} to your shortlist."
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Save",
                                        tint = if (isSaved) JobiestDanger else JobiestMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(job.displayTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = JobiestInk)

                            Spacer(modifier = Modifier.height(8.dp))

                            // Match Score Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("$matchScore%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (matchScore >= 90) JobiestSuccess else JobiestInk)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("match", fontSize = 11.sp, color = JobiestMuted)
                                Spacer(modifier = Modifier.width(10.dp))
                                LinearProgressIndicator(
                                    progress = { matchScore / 100f },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(5.dp),
                                    color = if (matchScore >= 90) JobiestSuccess else JobiestBrand,
                                    trackColor = JobiestBorderSubtle
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Why this fits you
                            Surface(
                                color = JobiestBgSecondary,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, JobiestBorderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = JobiestBrand, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Why this fits you", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                                        Text(fitReason, fontSize = 11.sp, color = JobiestMuted, lineHeight = 16.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Actions: View details & Apply with AI
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { onJobClick(job) }) {
                                    Text("View Details", fontSize = 12.sp, color = JobiestCobalt)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ArrowOutward, contentDescription = null, modifier = Modifier.size(12.dp), tint = JobiestCobalt)
                                }

                                Button(
                                    onClick = { onApplyWithAi(job) },
                                    colors = ButtonDefaults.buttonColors(containerColor = JobiestBrand, contentColor = JobiestInk),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Apply with AI", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
