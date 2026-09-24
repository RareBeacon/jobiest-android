package com.jobiest.android.ui.screens.applications

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
import com.jobiest.android.data.repository.ApplicationRepository
import com.jobiest.android.network.models.ApplicationDto
import com.jobiest.android.ui.components.EmptyView
import com.jobiest.android.ui.components.JobiestBrandHeader
import com.jobiest.android.ui.components.LoadingView
import com.jobiest.android.ui.theme.*
import kotlinx.coroutines.launch

val STATUS_LABELS = mapOf(
    "PREPARING" to "Preparing",
    "AWAITING_APPROVAL" to "Needs approval",
    "APPROVED" to "Approved",
    "SUBMITTED" to "Submitted",
    "INTERVIEW" to "Interview",
    "REJECTED" to "Rejected",
    "WITHDRAWN" to "Withdrawn",
    "FAILED" to "Failed",
    "DRAFT" to "Draft",
    "QUEUED" to "Queued",
    "AWAITING_VERIFICATION" to "Checking send",
    "AWAITING_USER_INPUT" to "Needs you"
)

@Composable
fun ApplicationsScreen(
    applicationRepository: ApplicationRepository,
    onStartNewApplication: () -> Unit,
    onSelectApplication: (String) -> Unit = {}
) {
    var applications by remember { mutableStateOf<List<ApplicationDto>>(emptyList()) }
    var priorityIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All applications") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val filterPills = listOf("All applications", "90%+ fit", "Needs attention", "Priority")

    fun load() {
        scope.launch {
            isLoading = true
            errorMessage = null
            val res = applicationRepository.getApplications()
            isLoading = false
            if (res.isSuccess) {
                applications = res.getOrNull()?.applications ?: emptyList()
            } else {
                errorMessage = res.exceptionOrNull()?.message ?: "Failed to load applications"
            }
        }
    }

    LaunchedEffect(Unit) {
        load()
    }

    val filteredApps = remember(applications, searchQuery, selectedFilter, priorityIds) {
        applications.filter { app ->
            val company = app.displayJob?.company ?: ""
            val title = app.displayJob?.title ?: ""
            val matchesQuery = "$company $title".contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                "90%+ fit" -> (app.fitScore ?: 88) >= 90
                "Needs attention" -> app.status.contains("APPROVAL", ignoreCase = true) || app.status.contains("DRAFT", ignoreCase = true)
                "Priority" -> priorityIds.contains(app.id)
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    Scaffold(
        topBar = {
            JobiestBrandHeader(
                title = "Application Tracker",
                subtitle = "Keep every opportunity moving with automated follow-ups",
                actions = {
                    IconButton(onClick = { load() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = JobiestInk)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onStartNewApplication,
                containerColor = JobiestBrand,
                contentColor = JobiestInk,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Application", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
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

            // Summary bar (Opportunities, Need attention, Interviews, Momentum)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = JobiestCard),
                    border = BorderStroke(1.dp, JobiestBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${applications.size} active", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                                Text("opportunities", fontSize = 11.sp, color = JobiestMuted)
                            }
                            Column {
                                val needsReview = applications.count { it.status.contains("APPROVAL", ignoreCase = true) }
                                Text("$needsReview need", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (needsReview > 0) JobiestDanger else JobiestInk)
                                Text("your attention", fontSize = 11.sp, color = JobiestMuted)
                            }
                            Column {
                                val interviews = applications.count { it.status.contains("INTERVIEW", ignoreCase = true) }
                                Text("$interviews ahead", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = JobiestCobalt)
                                Text("interviews", fontSize = 11.sp, color = JobiestMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = JobiestBorderSubtle)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Weekly momentum", fontSize = 11.sp, color = JobiestMuted, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(8.dp))
                            LinearProgressIndicator(
                                progress = { 0.68f },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(5.dp),
                                color = JobiestSuccess,
                                trackColor = JobiestBorderSubtle
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("68%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = JobiestSuccess)
                        }
                    }
                }
            }

            // Search Bar & Filter Pills
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = JobiestCard),
                    border = BorderStroke(1.dp, JobiestBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = JobiestMuted, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by company or role", fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = JobiestInk)
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = JobiestMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Filter Pills
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
                }
            }

            // Application Cards List
            if (isLoading) {
                item { LoadingView("Loading application pipeline…") }
            } else if (filteredApps.isEmpty()) {
                item {
                    EmptyView(
                        title = "No applications found",
                        subtitle = "Start an application or save a role from Discover to begin tracking."
                    )
                }
            } else {
                items(filteredApps, key = { it.id }) { app ->
                    val company = app.displayJob?.company ?: "Company"
                    val title = app.displayJob?.title ?: "Position"
                    val isPriority = priorityIds.contains(app.id)
                    val fitScore = app.fitScore ?: 91
                    val statusText = STATUS_LABELS[app.status] ?: app.status

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectApplication(app.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = JobiestCard),
                        border = BorderStroke(1.dp, JobiestBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(JobiestLogoNavy, RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            (company.firstOrNull() ?: 'J').uppercase(),
                                            color = JobiestBrand,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(company, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = JobiestInk)
                                        Text(app.updatedAt ?: "Active today", fontSize = 11.sp, color = JobiestMuted)
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        priorityIds = if (isPriority) priorityIds - app.id else priorityIds + app.id
                                        snackbarMessage = if (isPriority) "Removed priority for $company" else "Marked $company as priority"
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isPriority) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Priority",
                                        tint = if (isPriority) JobiestBrand else JobiestMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = JobiestInk)

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = when {
                                        statusText.contains("approval", ignoreCase = true) -> JobiestDangerBg
                                        statusText.contains("interview", ignoreCase = true) -> JobiestSuccessBg
                                        else -> JobiestBgSecondary
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        when {
                                            statusText.contains("approval", ignoreCase = true) -> JobiestDangerBorder
                                            statusText.contains("interview", ignoreCase = true) -> JobiestSuccessBorder
                                            else -> JobiestBorderSubtle
                                        }
                                    )
                                ) {
                                    Text(
                                        text = statusText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            statusText.contains("approval", ignoreCase = true) -> JobiestDanger
                                            statusText.contains("interview", ignoreCase = true) -> JobiestSuccess
                                            else -> JobiestInk
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("AI fit ", fontSize = 11.sp, color = JobiestMuted)
                                    Text("$fitScore%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .background(JobiestBrandSoft, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("AI", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(56.dp)) }
        }
    }
}
