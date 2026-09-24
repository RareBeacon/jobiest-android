package com.jobiest.android.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobiest.android.ui.components.JobiestBrandAppBadge
import com.jobiest.android.ui.theme.*

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var step by remember { mutableIntStateOf(0) }
    val totalSteps = 4

    val titles = listOf(
        "Your AI Career Agent",
        "Visible, Not Mysterious",
        "Set Your Target Path",
        "Personalization Complete"
    )

    val descriptions = listOf(
        "Jobiest finds opportunities, prepares tailored applications, and helps you land your next role with zero search fatigue.",
        "Your agent scans 140+ roles daily, ranks them against your skills, and explains why each role fits before you apply.",
        "Tell Jobiest your target role, compensation goals, and preferred work mode so recommendations compound in value.",
        "Your AI career agent is ready. Start discovering your highest-leverage career opportunities."
    )

    Scaffold(
        containerColor = JobiestBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                JobiestBrandAppBadge(size = 36.dp)

                if (step < totalSteps - 1) {
                    TextButton(onClick = { onFinishOnboarding() }) {
                        Text("Skip", color = JobiestMuted, fontSize = 13.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
            }

            // Visual Core
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(JobiestBgSecondary, CircleShape)
                        .border(2.dp, JobiestBrandSoft, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (step) {
                            0 -> Icons.Default.AutoAwesome
                            1 -> Icons.Default.Visibility
                            2 -> Icons.Default.TrackChanges
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        tint = when (step) {
                            3 -> JobiestSuccess
                            else -> JobiestLogoNavy
                        },
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = titles[step],
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = JobiestInk,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = descriptions[step],
                    fontSize = 14.sp,
                    color = JobiestMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Bottom controls: Step Dots & CTA
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Step Dots
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 0 until totalSteps) {
                        Box(
                            modifier = Modifier
                                .size(if (i == step) 22.dp else 8.dp, 8.dp)
                                .background(
                                    if (i == step) JobiestBrand else JobiestBorder,
                                    RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (step < totalSteps - 1) {
                            step += 1
                        } else {
                            onFinishOnboarding()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (step == totalSteps - 1) JobiestLogoNavy else JobiestBrand,
                        contentColor = if (step == totalSteps - 1) Color.White else JobiestInk
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (step == totalSteps - 1) "Start Finding Opportunities" else "Continue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (step == totalSteps - 1) Icons.Default.Check else Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account?", fontSize = 13.sp, color = JobiestMuted)
                    TextButton(onClick = onLoginClick) {
                        Text("Log In", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = JobiestCobalt)
                    }
                }
            }
        }
    }
}
