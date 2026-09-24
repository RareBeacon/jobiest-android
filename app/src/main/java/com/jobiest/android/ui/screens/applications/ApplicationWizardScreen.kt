package com.jobiest.android.ui.screens.applications

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobiest.android.ui.components.JobiestBrandHeader
import com.jobiest.android.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ApplicationWizardScreen(
    company: String,
    role: String,
    onNavigateBack: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {}
) {
    var step by remember { mutableIntStateOf(0) }
    val steps = listOf("Resume", "Tailoring", "Cover Letter", "Review", "Done")

    var selectedResume by remember { mutableStateOf("David Morgan — Product Designer v4.2") }
    var coverLetter by remember {
        mutableStateOf(
            "Hi $company team,\n\nI’m excited to apply for the $role position. My work leading AI workflows and scalable design systems has taught me how to make complex products feel calm, clear, and useful.\n\nI’d love to bring that perspective to $company."
        )
    }
    var tone by remember { mutableStateOf("Professional") }
    var isGeneratingLetter by remember { mutableStateOf(false) }
    var hasConsent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun nextStep() {
        errorMessage = null
        when (step) {
            0 -> {
                if (selectedResume.isBlank()) {
                    errorMessage = "Select a resume to continue."
                    return
                }
                step = 1
            }
            1 -> step = 2
            2 -> {
                if (coverLetter.trim().length < 80) {
                    errorMessage = "Add at least 80 characters to your cover letter."
                    return
                }
                step = 3
            }
            3 -> {
                if (!hasConsent) {
                    errorMessage = "Confirm that you reviewed the application package."
                    return
                }
                step = 4
            }
            4 -> onSubmitSuccess()
        }
    }

    Scaffold(
        topBar = {
            JobiestBrandHeader(
                title = "Apply with AI",
                subtitle = "$role · $company",
                onBack = onNavigateBack
            )
        },
        bottomBar = {
            Surface(
                color = JobiestBg,
                border = BorderStroke(1.dp, JobiestBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 0 && step < 4) {
                        TextButton(onClick = { step -= 1 }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back", color = JobiestMuted)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = { nextStep() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (step == 3) JobiestSuccess else JobiestBrand,
                            contentColor = if (step == 3) Color.White else JobiestInk
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when (step) {
                                3 -> "Submit Application"
                                4 -> "Done"
                                else -> "Continue"
                            },
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (step == 4) Icons.Default.Check else Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
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

            // Progress indicator bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.forEachIndexed { idx, label ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        when {
                                            idx < step -> JobiestSuccess
                                            idx == step -> JobiestBrand
                                            else -> JobiestBorder
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (idx < step) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                } else {
                                    Text("${idx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = if (idx == step) JobiestInk else JobiestMuted,
                                fontWeight = if (idx == step) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            if (errorMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = JobiestDangerBg),
                        border = BorderStroke(1.dp, JobiestDangerBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = JobiestDanger, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(errorMessage!!, fontSize = 12.sp, color = JobiestDanger)
                        }
                    }
                }
            }

            // Step Content Panels
            when (step) {
                0 -> item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = JobiestCard),
                        border = BorderStroke(1.dp, JobiestBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Choose Source Resume", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = JobiestInk)
                            Text("Jobiest creates a tailored role-specific version without changing your original.", fontSize = 12.sp, color = JobiestMuted)
                            Spacer(modifier = Modifier.height(14.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedResume = "David Morgan — Product Designer v4.2" },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = JobiestBrandSoft),
                                border = BorderStroke(1.dp, JobiestBrand)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = JobiestInk)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("David Morgan — Product Designer", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = JobiestInk)
                                        Text("v4.2 · Updated 2 days ago · 87 score", fontSize = 11.sp, color = JobiestMuted)
                                    }
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = JobiestInk)
                                }
                            }
                        }
                    }
                }

                1 -> item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = JobiestCard),
                        border = BorderStroke(1.dp, JobiestBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(32.dp).background(JobiestBrandSoft, CircleShape), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = JobiestInk, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Optimizing for $company", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = JobiestInk)
                                    Text("Surfacing 3 highest-leverage evidence signals", fontSize = 12.sp, color = JobiestMuted)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            listOf(
                                "AI-powered product workflow experience",
                                "Design systems leadership & craft",
                                "Cross-functional collaboration with engineering"
                            ).forEach { signal ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = JobiestSuccessBg),
                                    border = BorderStroke(1.dp, JobiestSuccessBorder),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = JobiestSuccess, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(signal, fontSize = 12.sp, color = JobiestInk, modifier = Modifier.weight(1f))
                                        Text("Matched", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = JobiestSuccess)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = JobiestCard),
                        border = BorderStroke(1.dp, JobiestBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Your Cover Letter Draft", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = JobiestInk)
                            Text("Tailor the voice and tone before final submission.", fontSize = 12.sp, color = JobiestMuted)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf("Professional", "Creative", "Enthusiastic").forEach { t ->
                                    FilterChip(
                                        selected = tone == t,
                                        onClick = { tone = t },
                                        label = { Text(t, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = JobiestBrandSoft,
                                            selectedLabelColor = JobiestInk
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        isGeneratingLetter = true
                                        delay(600)
                                        val intro = when (tone) {
                                            "Creative" -> "I love turning complex product challenges into software people remember."
                                            "Enthusiastic" -> "I’m genuinely excited about the opportunity to bring my energy and passion to $company."
                                            else -> "I’m excited to apply for this role."
                                        }
                                        coverLetter = "Hi $company team,\n\n$intro As a designer of AI-native products and scalable design systems, I’ve learned how to make complex products feel clear, useful, and human.\n\nI’d bring a collaborative, outcome-focused approach to your team.\n\nBest,\nDavid Morgan"
                                        isGeneratingLetter = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, JobiestBorder)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = JobiestCobalt)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isGeneratingLetter) "Regenerating draft…" else "Regenerate with AI ($tone)", fontSize = 12.sp, color = JobiestCobalt)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = coverLetter,
                                onValueChange = { coverLetter = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = JobiestInk)
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "${coverLetter.trim().length} / 80 characters minimum",
                                fontSize = 11.sp,
                                color = if (coverLetter.trim().length >= 80) JobiestSuccess else JobiestDanger
                            )
                        }
                    }
                }

                3 -> item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = JobiestCard),
                        border = BorderStroke(1.dp, JobiestBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Surface(
                                color = JobiestSuccessBg,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, JobiestSuccessBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = JobiestSuccess, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Ready for your review: tailored resume & cover letter package are prepared.", fontSize = 12.sp, color = JobiestSuccess)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Application Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JobiestInk)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Company", fontSize = 12.sp, color = JobiestMuted)
                                Text(company, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Role", fontSize = 12.sp, color = JobiestMuted)
                                Text(role, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Match Confidence", fontSize = 12.sp, color = JobiestMuted)
                                Text("94% Fit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JobiestSuccess)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { hasConsent = !hasConsent },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = hasConsent,
                                    onCheckedChange = { hasConsent = it },
                                    colors = CheckboxDefaults.colors(checkedColor = JobiestSuccess)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "I reviewed the resume, cover letter, and destination. I’m ready to submit.",
                                    fontSize = 12.sp,
                                    color = JobiestInk
                                )
                            }
                        }
                    }
                }

                4 -> item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = JobiestCard),
                        border = BorderStroke(1.dp, JobiestBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(JobiestSuccessBg, CircleShape)
                                    .border(2.dp, JobiestSuccess, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = JobiestSuccess, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Application Submitted!", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = JobiestInk)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Your application for $role at $company is now in your tracker. We’ll notify you when follow-up is needed.",
                                fontSize = 12.sp,
                                color = JobiestMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
