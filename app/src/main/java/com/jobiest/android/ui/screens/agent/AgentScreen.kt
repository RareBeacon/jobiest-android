package com.jobiest.android.ui.screens.agent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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

data class AgentMessage(
    val id: String,
    val sender: String, // "agent" or "user"
    val text: String,
    val timestamp: String,
    val actionLabel: String? = null,
    val actionRoute: String? = null
)

@Composable
fun AgentScreen(
    onNavigateBack: () -> Unit = {},
    onActionClick: (String) -> Unit = {}
) {
    var messages by remember {
        mutableStateOf(
            listOf(
                AgentMessage(
                    id = "1",
                    sender = "agent",
                    text = "Good morning! I found 3 roles worth your attention and noticed your resume could tell a stronger story about the AI workflow you led.",
                    timestamp = "9:42 AM"
                ),
                AgentMessage(
                    id = "2",
                    sender = "user",
                    text = "Which one should I prioritize this week?",
                    timestamp = "9:44 AM"
                ),
                AgentMessage(
                    id = "3",
                    sender = "agent",
                    text = "Start with Airbnb. It’s a 94% fit, the team is actively interviewing, and your systems craft gives you a differentiated angle. I’ve prepared a tailored resume outline.",
                    timestamp = "9:44 AM",
                    actionLabel = "Airbnb resume outline",
                    actionRoute = "resume_studio"
                )
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    var selectedTone by remember { mutableStateOf("Focused") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    fun sendMessage() {
        if (inputText.isBlank()) return
        val userMsg = AgentMessage(
            id = System.currentTimeMillis().toString(),
            sender = "user",
            text = inputText.trim(),
            timestamp = "Just now"
        )
        messages = messages + userMsg
        val prompt = inputText
        inputText = ""

        scope.launch {
            delay(400)
            listState.animateScrollToItem(messages.size - 1)
            delay(800)
            val reply = when {
                prompt.contains("resume", ignoreCase = true) ->
                    "I analyzed your resume against the target role. Adding measurable outcomes to 2 project bullets will increase recruiter match confidence."
                prompt.contains("interview", ignoreCase = true) ->
                    "For system design questions, emphasize how you handled scale and user feedback loops. Let me know when you'd like a mock session!"
                else ->
                    "Understood. Grounding this in your target compensation ($180k+) and remote-first preference, I will prioritize high-leverage roles."
            }
            messages = messages + AgentMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                sender = "agent",
                text = reply,
                timestamp = "Just now",
                actionLabel = "Review matching roles",
                actionRoute = "jobs"
            )
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            JobiestBrandHeader(
                title = "AI Career Agent",
                subtitle = "Always-on career intelligence & context-aware guidance",
                onBack = onNavigateBack
            )
        },
        bottomBar = {
            Surface(
                color = JobiestBg,
                border = BorderStroke(1.dp, JobiestBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Tone Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tone:", fontSize = 11.sp, color = JobiestMuted, fontWeight = FontWeight.Bold)
                        listOf("Focused", "Encouraging", "Direct").forEach { tone ->
                            FilterChip(
                                selected = selectedTone == tone,
                                onClick = { selectedTone = tone },
                                label = { Text(tone, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = JobiestBrandSoft,
                                    selectedLabelColor = JobiestInk
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Ask your career agent anything…", fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = JobiestInk)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { sendMessage() },
                            modifier = Modifier
                                .size(44.dp)
                                .background(JobiestBrand, CircleShape)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Send", tint = JobiestInk)
                        }
                    }
                }
            }
        },
        containerColor = JobiestBgSecondary
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Status header pill
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = JobiestSuccessBg,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, JobiestSuccessBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(7.dp).background(JobiestSuccess, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Agent Active · Ready to assist", fontSize = 12.sp, color = JobiestSuccess, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                val isAgent = msg.sender == "agent"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isAgent) Alignment.Start else Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = if (isAgent) Arrangement.Start else Arrangement.End
                    ) {
                        if (isAgent) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(JobiestLogoNavy, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = JobiestBrand, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Card(
                            modifier = Modifier.widthIn(max = 280.dp),
                            shape = RoundedCornerShape(
                                topStart = 14.dp,
                                topEnd = 14.dp,
                                bottomStart = if (isAgent) 2.dp else 14.dp,
                                bottomEnd = if (isAgent) 14.dp else 2.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAgent) JobiestCard else JobiestInk
                            ),
                            border = if (isAgent) BorderStroke(1.dp, JobiestBorder) else null
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = msg.text,
                                    fontSize = 13.sp,
                                    color = if (isAgent) JobiestInk else Color.White
                                )

                                if (msg.actionLabel != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        color = JobiestBrandSoft,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.clickable {
                                            msg.actionRoute?.let { onActionClick(it) }
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Description, contentDescription = null, tint = JobiestInk, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(msg.actionLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = JobiestInk)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.ArrowOutward, contentDescription = null, tint = JobiestInk, modifier = Modifier.size(11.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.timestamp,
                                    fontSize = 10.sp,
                                    color = if (isAgent) JobiestMuted2 else Color(0xFFBAC4D5),
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}
