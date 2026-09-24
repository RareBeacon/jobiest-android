package com.jobiest.android.ui.screens.resume

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.jobiest.android.ui.components.JobiestBrandHeader
import com.jobiest.android.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ResumeStudioScreen(
    onNavigateBack: () -> Unit = {},
    onStartTailoring: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var jobDescription by remember {
        mutableStateOf(
            "We are looking for a Senior Product Designer to lead end-to-end product strategy, design systems, and AI-powered experiences. You will partner with engineering and research to ship intuitive SaaS products, run experiments, and create measurable customer impact."
        )
    }
    var isAnalyzing by remember { mutableStateOf(false) }
    var extractedKeywords by remember { mutableStateOf<List<String>>(emptyList()) }
    var score by remember { mutableIntStateOf(87) }
    var showCompareDialog by remember { mutableStateOf(false) }
    var showPreviewDialog by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf("Paper") }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val defaultSuggestions = remember(extractedKeywords) {
        if (extractedKeywords.isNotEmpty()) {
            listOf(
                "Bring \"${extractedKeywords.first()}\" into your summary with a concrete outcome",
                "Add a ${extractedKeywords.getOrElse(1) { "design systems" }} bullet to your Northstar experience",
                "Move ${extractedKeywords.getOrElse(2) { "AI workflows" }} into your top skills to improve recruiter matching"
            )
        } else {
            listOf(
                "Add measurable outcomes to 2 project bullets",
                "Tighten your summary around AI product work",
                "Surface your design systems leadership"
            )
        }
    }

    fun analyzeJob() {
        if (jobDescription.trim().length < 40) {
            snackbarMessage = "Add a longer job description so the AI can extract signals."
            return
        }
        scope.launch {
            isAnalyzing = true
            delay(600)
            val keywordsPool = listOf(
                "product strategy", "design systems", "AI-powered experiences",
                "SaaS", "user research", "experimentation", "customer impact",
                "cross-functional leadership"
            )
            val found = keywordsPool.filter { jobDescription.contains(it, ignoreCase = true) }
            extractedKeywords = if (found.isNotEmpty()) found else keywordsPool.take(4)
            score = 92
            isAnalyzing = false
            snackbarMessage = "AI keyword analysis complete: ${extractedKeywords.size} terms surfaced."
        }
    }

    fun printResume() {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            val webView = WebView(context)
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    val printAdapter = webView.createPrintDocumentAdapter("Jobiest_Resume_David_Morgan")
                    printManager?.print("Jobiest Resume", printAdapter, PrintAttributes.Builder().build())
                }
            }
            val html = """
                <html>
                <body style="font-family: sans-serif; padding: 24px; color: #17223b;">
                    <h1 style="margin: 0; color: #062b68;">David Morgan</h1>
                    <p style="color: #616d81; margin: 4px 0 16px 0;">Product designer crafting calm, high-leverage software.</p>
                    <hr style="border: 0; border-top: 1px solid #e2e6ed;" />
                    <h3>Experience</h3>
                    <p><strong>Senior Product Designer</strong> &middot; Northstar (2021&mdash;Present)</p>
                    <p>Led end-to-end design for AI workflows, increasing activation by 31% across 400k users.</p>
                    <h3>Capabilities</h3>
                    <p>Product strategy &middot; Design systems &middot; AI experiences &middot; Figma</p>
                </body>
                </html>
            """.trimIndent()
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        } catch (_: Exception) {
            snackbarMessage = "Printing service not available on this device."
        }
    }

    Scaffold(
        topBar = {
            JobiestBrandHeader(
                title = "Resume Studio",
                subtitle = "Target your resume to the role with AI keyword intelligence",
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

            // Action toolbar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { printResume() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, JobiestBorder)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export PDF", fontSize = 13.sp, color = JobiestInk)
                    }

                    Button(
                        onClick = { showCompareDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = JobiestBrand, contentColor = JobiestInk),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Compare", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Keyword Intelligence Lab
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = JobiestCard),
                    border = BorderStroke(1.dp, JobiestBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(JobiestBrandSoft, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = JobiestInk, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("AI Keyword Intelligence", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = JobiestInk)
                                Text("Compare job brief against your evidence", fontSize = 12.sp, color = JobiestMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = jobDescription,
                            onValueChange = { jobDescription = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            placeholder = { Text("Paste job description here…", fontSize = 13.sp) },
                            shape = RoundedCornerShape(8.dp),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = JobiestInk)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${jobDescription.length} chars", fontSize = 11.sp, color = JobiestMuted2)
                            Button(
                                onClick = { analyzeJob() },
                                enabled = !isAnalyzing,
                                colors = ButtonDefaults.buttonColors(containerColor = JobiestInk, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isAnalyzing) {
                                    CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Analyzing…", fontSize = 12.sp)
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Extract Keywords", fontSize = 12.sp)
                                }
                            }
                        }

                        if (extractedKeywords.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Matched signals in role:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = JobiestInk)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                extractedKeywords.take(3).forEach { kw ->
                                    Surface(
                                        color = JobiestSuccessBg,
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, JobiestSuccessBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = JobiestSuccess, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(kw, fontSize = 11.sp, color = JobiestSuccess, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Score and Recommendations Card
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
                                Text("Impact Score", fontSize = 13.sp, color = JobiestMuted)
                                Text("$score / 100", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                            }
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .border(4.dp, if (score >= 90) JobiestSuccess else JobiestBrand, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$score%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { score / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (score >= 90) JobiestSuccess else JobiestBrand,
                            trackColor = JobiestBorderSubtle
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Targeted improvements:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                        Spacer(modifier = Modifier.height(8.dp))

                        defaultSuggestions.forEachIndexed { idx, sugg ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        snackbarMessage = "Added suggestion ${idx + 1} to tailoring queue."
                                    },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = JobiestBgSecondary),
                                border = BorderStroke(1.dp, JobiestBorderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(JobiestBrand, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${idx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(sugg, fontSize = 12.sp, color = JobiestInk, modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = JobiestMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                onStartTailoring("Tailored Role")
                                snackbarMessage = "AI tailoring initiated for your resume."
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = JobiestInk, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Improve with AI", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Live Preview Card
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(JobiestSuccess, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Live preview", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JobiestMuted)
                            }
                            TextButton(onClick = { showPreviewDialog = true }) {
                                Text("Full view", fontSize = 12.sp, color = JobiestCobalt)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Resume paper simulation
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = JobiestBg),
                            border = BorderStroke(1.dp, JobiestBorderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("David Morgan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                                Text("Product designer crafting calm, high-leverage software.", fontSize = 11.sp, color = JobiestMuted)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = JobiestBorder)
                                Text("Experience", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Senior Product Designer · Northstar", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = JobiestInk)
                                Text("Led end-to-end design for an AI workflow, increasing activation by 31% across 400k users.", fontSize = 11.sp, color = JobiestInk)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Capabilities", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Product strategy · Design systems · AI experiences · Figma", fontSize = 11.sp, color = JobiestCobalt)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Version Comparison Dialog
    if (showCompareDialog) {
        Dialog(onDismissRequest = { showCompareDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = JobiestCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Resume Comparison", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = JobiestInk)
                        IconButton(onClick = { showCompareDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = JobiestMuted)
                        }
                    }
                    Text("See what the AI optimized before submission.", fontSize = 12.sp, color = JobiestMuted)
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = JobiestBgSecondary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Original (Baseline)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = JobiestMuted)
                            Text("Worked on design projects and collaborated with teams.", fontSize = 12.sp, color = JobiestInk)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = JobiestSuccessBg),
                        border = BorderStroke(1.dp, JobiestSuccessBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("AI-Optimized (+8% lift)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = JobiestSuccess)
                            Text("Led an AI workflow that increased activation 31% across 400k users and built systems that cut handoff time 40%.", fontSize = 12.sp, color = JobiestInk)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showCompareDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = JobiestInk, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Looks Great")
                    }
                }
            }
        }
    }
}
