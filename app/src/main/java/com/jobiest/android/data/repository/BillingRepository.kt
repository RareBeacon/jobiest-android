package com.jobiest.android.data.repository

import com.jobiest.android.network.JobiestApiService
import com.jobiest.android.network.models.CreatePaymentRequest
import com.jobiest.android.network.models.CreatePaymentResponse
import com.jobiest.android.network.models.EntitlementDto

class BillingRepository(
    private val apiService: JobiestApiService
) {

    suspend fun getEntitlements(): Result<EntitlementDto> {
        return try {
            val response = apiService.getEntitlements()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load entitlements: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSubscriptionPayment(
        plan: String,
        provider: String = "flutterwave"
    ): Result<CreatePaymentResponse> {
        return try {
            val req = CreatePaymentRequest(
                plan = plan,
                provider = provider,
                redirectUrl = "https://jobiest.com/billing/success"
            )
            val response = if (provider == "paystack") {
                apiService.createPaystackPayment(req)
            } else {
                apiService.createFlutterwavePayment(req)
            }

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to initiate payment: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
