package com.jobiest.android.security

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val accessToken: String,
    val refreshToken: String? = null,
    val userId: String,
    val email: String? = null,
    val expiresAt: Long = 0L
)

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val session: Session) : AuthState
}
