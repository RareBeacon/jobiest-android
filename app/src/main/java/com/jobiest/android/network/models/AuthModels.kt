package com.jobiest.android.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseLoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class SupabaseSignupRequest(
    val email: String,
    val password: String,
    val data: Map<String, String>? = null
)

@Serializable
data class SupabaseRecoverPasswordRequest(
    val email: String
)

@Serializable
data class SupabaseRefreshTokenRequest(
    @SerialName("refresh_token") val refreshToken: String
)

/**
 * Resilient Supabase Token Response.
 * Nullable fields with default values guarantee no MissingFieldException crashes
 * when GoTrue returns unconfirmed user objects or errors.
 */
@Serializable
data class SupabaseTokenResponse(
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("expires_in") val expiresIn: Long = 3600L,
    @SerialName("token_type") val tokenType: String = "bearer",
    val user: SupabaseUserDto? = null,
    val id: String? = null,
    val email: String? = null
) {
    val resolvedEmail: String? get() = user?.email ?: email
    val resolvedId: String? get() = user?.id ?: id
}


@Serializable
data class SupabaseUserDto(
    val id: String,
    val email: String? = null,
    @SerialName("email_confirmed_at") val emailConfirmedAt: String? = null,
    @SerialName("app_metadata") val appMetadata: Map<String, String>? = null,
    @SerialName("user_metadata") val userMetadata: Map<String, String>? = null
)

@Serializable
data class SupabaseErrorResponse(
    val error: String? = null,
    @SerialName("error_description") val errorDescription: String? = null,
    val msg: String? = null,
    val message: String? = null
)

// === JOBIEST PRODUCTION SERVER AUTH DTOs (/api/auth/*) ===

@Serializable
data class JobiestSignupRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String
)

@Serializable
data class JobiestSignupResponse(
    val ok: Boolean = true,
    val email: String? = null,
    val error: String? = null,
    val message: String? = null
)

@Serializable
data class JobiestVerifyRequest(
    val action: String, // "send" or "confirm"
    val email: String,
    val code: String? = null
)

@Serializable
data class JobiestVerifyResponse(
    val ok: Boolean = false,
    val message: String? = null,
    val error: String? = null
)
