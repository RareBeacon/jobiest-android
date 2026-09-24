package com.jobiest.android.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobiest.android.data.repository.ApplicationRepository
import com.jobiest.android.data.repository.BillingRepository
import com.jobiest.android.data.repository.ProfileRepository
import com.jobiest.android.network.models.ApplicationDto
import com.jobiest.android.network.models.EntitlementDto
import com.jobiest.android.network.models.ProfileDto
import com.jobiest.android.ui.components.JobiestBrandHeader
import com.jobiest.android.ui.components.LoadingView
import com.jobiest.android.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    profileRepository: ProfileRepository,
    applicationRepository: ApplicationRepository,
    billingRepository: BillingRepository,
    onFindOpportunitiesClick: () -> Unit,
    onOpenAgentClick: () -> Unit,
    onViewApplicationsClick: () -> Unit,
    onResumeStudioClick: () -> Unit,
    onApplyWithAi: (String, String) -> Unit,
    onBillingClick: () -> Unit = {}
) {
    var profile by remember { mutableStateOf<ProfileDto?>(null) }
    var applications by remember { mutableStateOf<List<ApplicationDto>>(emptyList()) }
    var entitlement by remember { mutableStateOf<EntitlementDto?>(null) }
    var completenessPercent by remember { mutableIntStateOf(94) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var agentPaused by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun loadData() {
        scope.launch {
            isLoading = true
            errorMessage = null

            val pRes = profileRepository.getProfile()
            if (pRes.isSuccess) profile = pRes.getOrNull()

            val compRes = profileRepository.getCompleteness()
            if (compRes.isSuccess) completenessPercent = compRes.getOrNull()?.percent ?: 94

            val appRes = applicationRepository.getApplications()
            if (appRes.isSuccess) applications = appRes.getOrNull()?.applications ?: emptyList()

            val entRes = billingRepository.getEntitlements()
            if (entRes.isSuccess) entitlement = entRes.getOrNull()

            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    if (isLoading) {
        LoadingView("Loading your career command center…")
        return
    }

    val todayDateFormatted = remember {
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        sdf.format(Date())
    }

    val firstName = remember(profile) {
        profile?.fullName?.split(" ")?.firstOrNull() ?: "there"
    }

    Scaffold(
        topBar = {
            JobiestBrandHeader(
                title = "Jobiest",
                subtitle = "Your AI Career Agent",
                actions = {
                    IconButton(onClick = onOpenAgentClick) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI Agent", tint = JobiestBrand)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Page Heading with Date & Dynamic Greeting
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(todayDateFormatted, fontSize = 12.sp, color = JobiestMuted, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Good morning, $firstName ", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                        Text("✦", fontSize = 18.sp, color = JobiestBrand)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "You’re building momentum. Here’s what deserves your attention today.",
                        fontSize = 13.sp,
                        color = JobiestMuted,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onFindOpportunitiesClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = JobiestBrand, contentColor = JobiestInk),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Find Opportunities", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // 2. Signal Strip
            item {
                Surface(
                    color = JobiestBrandSoft,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, JobiestBrand)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
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
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Your AI agent found 18 new opportunities", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JobiestInk)
                            Text("3 are a 90%+ match for your career evidence.", fontSize = 11.sp, color = JobiestInkSecondary)
                        }
                        TextButton(
                            onClick = onFindOpportunitiesClick,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Review", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                            Icon(Icons.Default.ArrowOutward, contentDescription = null, modifier = Modifier.size(13.dp), tint = JobiestInk)
                        }
                    }
                }
            }

            // 3. AI Agent Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = JobiestLogoNavy)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (agentPaused) JobiestWarning else JobiestSuccess, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (agentPaused) "AI Agent Paused" else "AI Agent Active",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (agentPaused) JobiestWarning else JobiestSuccess
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Your career agent\nis working.", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Scanning 142 roles and tuning your path to the next right move.",
                            fontSize = 12.sp,
                            color = Color(0xFFBAC4D5),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats bar inside agent card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F3B82), RoundedCornerShape(8.dp))
                                .padding(vertical = 10.dp, horizontal = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("142", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("roles scanned", fontSize = 10.sp, color = Color(0xFFBAC4D5))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("18", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = JobiestBrand)
                                Text("new matches", fontSize = 10.sp, color = Color(0xFFBAC4D5))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("06", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("ready to apply", fontSize = 10.sp, color = Color(0xFFBAC4D5))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = onOpenAgentClick,
                                colors = ButtonDefaults.buttonColors(containerColor = JobiestBrand, contentColor = JobiestInk),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Open Agent", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowOutward, contentDescription = null, modifier = Modifier.size(13.dp))
                            }

                            OutlinedButton(
                                onClick = {
                                    agentPaused = !agentPaused
                                    snackbarMessage = if (agentPaused) "Agent paused." else "Agent resumed scanning."
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF335799)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Icon(
                                    imageVector = if (agentPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (agentPaused) "Resume" else "Pause", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 4. Four Metric Cards (2x2 Grid)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(
                            title = "Profile Strength",
                            value = "$completenessPercent%",
                            delta = "+8% this month",
                            icon = Icons.Default.TrackChanges,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "New Opportunities",
                            value = "18",
                            delta = "+6 since yesterday",
                            icon = Icons.Default.WorkOutline,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(
                            title = "Ready to Apply",
                            value = "06",
                            delta = "2 need your review",
                            icon = Icons.Default.CheckCircleOutline,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Response Lift",
                            value = "2.4×",
                            delta = "vs. untailored apps",
                            icon = Icons.Default.TrendingUp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 5. Recommended Roles (Curated for your path)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Curated for your path", fontSize = 11.sp, color = JobiestMuted, fontWeight = FontWeight.Bold)
                            Text("Recommended Roles", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                        }
                        TextButton(onClick = onFindOpportunitiesClick) {
                            Text("View All", fontSize = 12.sp, color = JobiestCobalt)
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(Icons.Default.ArrowOutward, contentDescription = null, modifier = Modifier.size(12.dp), tint = JobiestCobalt)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Curated Role 1
                    CuratedMatchCard(
                        company = "Airbnb",
                        role = "Senior Product Designer",
                        location = "San Francisco · Hybrid",
                        salary = "$168k–$202k",
                        match = 94,
                        reason = "Your UX systems experience and Figma craft map closely to this team’s needs.",
                        tags = listOf("Figma", "SaaS", "UX research"),
                        onApply = { onApplyWithAi("Airbnb", "Senior Product Designer") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Curated Role 2
                    CuratedMatchCard(
                        company = "Linear",
                        role = "Product Designer, Growth",
                        location = "Remote · US",
                        salary = "$145k–$185k",
                        match = 91,
                        reason = "Strong overlap in SaaS, experimentation, and shipping with lean product teams.",
                        tags = listOf("Growth", "B2B", "0→1"),
                        onApply = { onApplyWithAi("Linear", "Product Designer, Growth") }
                    )
                }
            }

            // 6. Recent Activity Timeline
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
                            Column {
                                Text("Live timeline", fontSize = 11.sp, color = JobiestMuted, fontWeight = FontWeight.Bold)
                                Text("Recent Activity", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        listOf(
                            Triple("Resume tailored for Airbnb", "12 min ago", true),
                            Triple("New role matched at Linear", "48 min ago", true),
                            Triple("Profile strength improved", "Yesterday", false)
                        ).forEach { (action, time, isDone) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(if (isDone) JobiestSuccessBg else JobiestBgSecondary, CircleShape)
                                        .border(1.dp, if (isDone) JobiestSuccess else JobiestBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = JobiestSuccess, modifier = Modifier.size(12.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(action, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = JobiestInk)
                                    Text(time, fontSize = 10.sp, color = JobiestMuted)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        TextButton(
                            onClick = onViewApplicationsClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Open Application Tracker", fontSize = 12.sp, color = JobiestCobalt, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowOutward, contentDescription = null, modifier = Modifier.size(12.dp), tint = JobiestCobalt)
                        }
                    }
                }
            }

            // 7. Career Tools Grid
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Compounding moves", fontSize = 11.sp, color = JobiestMuted, fontWeight = FontWeight.Bold)
                    Text("Career Tools", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onResumeStudioClick() },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = JobiestCard),
                        border = BorderStroke(1.dp, JobiestBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(JobiestBrandSoft, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = JobiestInk)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Resume Studio", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JobiestInk)
                                Text("Extract high-signal keywords and preview targeted revisions.", fontSize = 11.sp, color = JobiestMuted)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = JobiestMuted)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    delta: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(JobiestBgSecondary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = JobiestInk, modifier = Modifier.size(15.dp))
                }
                Text(delta, fontSize = 10.sp, color = JobiestSuccess, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
            Text(title, fontSize = 11.sp, color = JobiestMuted)
        }
    }
}

@Composable
private fun CuratedMatchCard(
    company: String,
    role: String,
    location: String,
    salary: String,
    match: Int,
    reason: String,
    tags: List<String>,
    onApply: () -> Unit
) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(JobiestLogoNavy, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            company.first().toString(),
                            fontWeight = FontWeight.Bold,
                            color = JobiestBrand,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(company, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JobiestInk)
                        Text(location, fontSize = 11.sp, color = JobiestMuted)
                    }
                }
                Text(salary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = JobiestInk)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(role, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = JobiestInk)

            Spacer(modifier = Modifier.height(8.dp))

            // Score Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$match%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JobiestSuccess)
                Spacer(modifier = Modifier.width(6.dp))
                Text("match", fontSize = 11.sp, color = JobiestMuted)
                Spacer(modifier = Modifier.width(8.dp))
                LinearProgressIndicator(
                    progress = { match / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp),
                    color = JobiestSuccess,
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
                        Text(reason, fontSize = 11.sp, color = JobiestMuted, lineHeight = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tags and CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    tags.forEach { tag ->
                        Surface(
                            color = JobiestBgSecondary,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                tag,
                                fontSize = 10.sp,
                                color = JobiestMuted,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = onApply,
                    colors = ButtonDefaults.buttonColors(containerColor = JobiestBrand, contentColor = JobiestInk),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply with AI", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}
