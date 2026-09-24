package com.jobiest.android.data.repository

import com.jobiest.android.network.JobiestApiService
import com.jobiest.android.network.SupabaseAuthService
import com.jobiest.android.network.models.*
import com.jobiest.android.security.AuthState
import com.jobiest.android.security.SecureSessionManager
import com.jobiest.android.security.Session
import kotlinx.coroutines.flow.StateFlow

class AuthRepository(
    private val authService: SupabaseAuthService,
    private val jobiestApiService: JobiestApiService,
    private val sessionManager: SecureSessionManager
) {

    val authState: StateFlow<AuthState> = sessionManager.authState

    suspend fun login(email: String, pass: String): Result<Session> {
        return try {
            val response = authService.login(body = SupabaseLoginRequest(email.trim(), pass))
            if (response.isSuccessful && response.body() != null) {
                val tokenResp = response.body()!!
                val token = tokenResp.accessToken
                if (token.isNullOrBlank()) {
                    return Result.failure(Exception("Please verify your email address to continue."))
                }
                val session = Session(
                    accessToken = token,
                    refreshToken = tokenResp.refreshToken ?: "",
                    userId = tokenResp.user?.id ?: "",
                    email = tokenResp.user?.email ?: email,
                    expiresAt = System.currentTimeMillis() + (tokenResp.expiresIn * 1000)
                )
                sessionManager.saveSession(session)
                Result.success(session)
            } else {
                val errBody = response.errorBody()?.string() ?: ""
                val errorMsg = when {
                    errBody.contains("Email not confirmed", ignoreCase = true) ->
                        "Your email is not verified yet. Please check your inbox for the 6-digit code."
                    errBody.contains("Invalid login credentials", ignoreCase = true) ->
                        "Incorrect email or password. Please try again."
                    else -> "Authentication failed (${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun serverSignup(fullName: String, email: String, phone: String, pass: String): Result<String> {
        return try {
            val response = jobiestApiService.signup(
                JobiestSignupRequest(
                    fullName = fullName.trim(),
                    email = email.trim().lowercase(),
                    phone = phone.trim(),
                    password = pass
                )
            )
            if (response.isSuccessful) {
                Result.success(email.trim().lowercase())
            } else {
                val err = response.errorBody()?.string() ?: "Registration failed"
                val friendly = when {
                    err.contains("already exists", ignoreCase = true) -> "An account with this email already exists. Please sign in."
                    err.contains("password", ignoreCase = true) -> "Password requires at least 8 characters, a number, and a special character."
                    err.contains("phone", ignoreCase = true) -> "Please enter a valid phone number with country code, e.g. +234..."
                    else -> "Could not create account. Please check your details."
                }
                Result.failure(Exception(friendly))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyEmail(email: String, code: String): Result<Unit> {
        return try {
            val response = jobiestApiService.verifyEmail(
                JobiestVerifyRequest(
                    action = "confirm",
                    email = email.trim().lowercase(),
                    code = code.trim()
                )
            )
            if (response.isSuccessful && response.body()?.ok == true) {
                Result.success(Unit)
            } else {
                val msg = response.body()?.message ?: "Invalid or expired code. Please try again."
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resendVerificationCode(email: String): Result<String> {
        return try {
            val response = jobiestApiService.verifyEmail(
                JobiestVerifyRequest(
                    action = "send",
                    email = email.trim().lowercase()
                )
            )
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Verification code sent to your inbox.")
            } else {
                Result.failure(Exception("Could not resend code. Please try again shortly."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun handleOAuthCallback(accessToken: String, refreshToken: String?, expiresIn: Long = 3600L): Session {
        val session = Session(
            accessToken = accessToken,
            refreshToken = refreshToken ?: "",
            userId = "", // Populated via /api/profile
            email = "",
            expiresAt = System.currentTimeMillis() + (expiresIn * 1000)
        )
        sessionManager.saveSession(session)
        return session
    }

    suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val response = authService.recoverPassword(SupabaseRecoverPasswordRequest(email.trim()))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Password recovery failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }
}
