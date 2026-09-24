package com.jobiest.android.ui.screens.billing

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobiest.android.data.repository.BillingRepository
import com.jobiest.android.network.models.EntitlementDto
import com.jobiest.android.ui.components.JobiestButton
import com.jobiest.android.ui.components.LoadingView
import com.jobiest.android.ui.theme.*
import kotlinx.coroutines.launch

private data class PlanInfo(
    val code: String,
    val name: String,
    val price: String,
    val period: String,
    val features: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    billingRepository: BillingRepository,
    onNavigateBack: () -> Unit
) {
    var entitlement by remember { mutableStateOf<EntitlementDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var upgradingPlan by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val plans = listOf(
        PlanInfo(
            code = "BASIC",
            name = "Basic Plan",
            price = "NGN 5,000",
            period = "/month",
            features = listOf(
                "3 AI tailored documents/day",
                "2 lifetime auto-apply trials",
                "50 career tool uses/day",
                "ATS Keyword scanner"
            )
        ),
        PlanInfo(
            code = "PREMIUM",
            name = "Premium Plan",
            price = "NGN 10,000",
            period = "/month",
            features = listOf(
                "10 AI documents/day",
                "10 auto-apply agent slots/day",
                "Unlimited career AI tools",
                "Priority browser worker queue"
            )
        ),
        PlanInfo(
            code = "MAX",
            name = "Max Plan",
            price = "NGN 20,000",
            period = "/month",
            features = listOf(
                "20 AI documents/day",
                "20 auto-apply agent slots/day",
                "Unlimited everything",
                "Dedicated career agent worker"
            )
        )
    )

    LaunchedEffect(Unit) {
        val res = billingRepository.getEntitlements()
        if (res.isSuccess) entitlement = res.getOrNull()
        isLoading = false
    }

    if (isLoading) {
        LoadingView()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plans & Subscription", color = TextPrimary) },
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
            // Current Plan Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = JobiestNavy),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(JobiestGold)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Current Subscription",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = JobiestGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${entitlement?.plan ?: "FREE"} PLAN",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("AI Documents Remaining Today:", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text("${entitlement?.aiCreditsRemaining ?: 0}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Auto-Apply Slots Remaining:", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text("${entitlement?.applicationsRemaining ?: 0}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Upgrade Your Career Agent",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Automate your search with verified Flutterwave billing. Cancel anytime.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            plans.forEach { plan ->
                val isCurrentPlan = entitlement?.plan == plan.code

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = JobiestCardBg),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (plan.code == "PREMIUM") JobiestGold else JobiestBorder
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = plan.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            if (plan.code == "PREMIUM") {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = JobiestGold
                                ) {
                                    Text(
                                        text = "POPULAR",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = JobiestNavyDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = plan.price,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = JobiestGold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = plan.period,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        plan.features.forEach { feat ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = JobiestGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = feat, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isCurrentPlan) {
                            OutlinedButton(
                                onClick = {},
                                enabled = false,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Current Active Plan")
                            }
                        } else {
                            JobiestButton(
                                text = "Upgrade with Flutterwave",
                                isLoading = upgradingPlan == plan.code,
                                onClick = {
                                    scope.launch {
                                        upgradingPlan = plan.code
                                        errorMessage = null
                                        val res = billingRepository.createSubscriptionPayment(plan.code, "flutterwave")
                                        upgradingPlan = null
                                        if (res.isSuccess && !res.getOrNull()?.checkoutUrl.isNullOrBlank()) {
                                            val checkoutUrl = res.getOrNull()!!.checkoutUrl!!
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(checkoutUrl))
                                            context.startActivity(intent)
                                        } else {
                                            errorMessage = res.exceptionOrNull()?.message ?: "Failed to initiate payment."
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
