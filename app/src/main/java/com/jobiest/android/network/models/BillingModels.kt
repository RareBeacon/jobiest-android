package com.jobiest.android.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EntitlementDto(
    val plan: String = "FREE", // FREE, BASIC, PREMIUM, MAX
    @SerialName("subscription_status") val subscriptionStatus: String? = null,
    @SerialName("automation_enabled") val automationEnabled: Boolean = false,
    @SerialName("ai_credits_remaining") val aiCreditsRemaining: Int = 0,
    @SerialName("applications_remaining") val applicationsRemaining: Int = 0
)

@Serializable
data class BillingProvidersResponse(
    val providers: List<String> = listOf("flutterwave", "paystack")
)

@Serializable
data class CreatePaymentRequest(
    val plan: String, // BASIC, PREMIUM, MAX
    val provider: String = "flutterwave",
    val redirectUrl: String? = null
)

@Serializable
data class CreatePaymentResponse(
    @SerialName("checkoutUrl") val checkoutUrl: String? = null,
    val reference: String? = null,
    val error: String? = null
)
