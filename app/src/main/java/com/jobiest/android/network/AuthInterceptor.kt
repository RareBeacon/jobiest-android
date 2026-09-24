package com.jobiest.android.network

import com.jobiest.android.security.SecureSessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp Interceptor that injects Bearer JWT authentication into outgoing requests.
 * Handles token attachment securely without logging sensitive credentials.
 */
class AuthInterceptor(
    private val sessionManager: SecureSessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = sessionManager.getAccessToken()

        val newRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/json")
                .build()
        } else {
            originalRequest.newBuilder()
                .header("Accept", "application/json")
                .build()
        }

        val response = chain.proceed(newRequest)

        // If the token expired (401), trigger session clear
        if (response.code == 401 && !token.isNullOrBlank()) {
            // Unauthenticated response
            sessionManager.clearSession()
        }

        return response
    }
}
