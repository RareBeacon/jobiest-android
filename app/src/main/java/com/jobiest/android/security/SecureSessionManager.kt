package com.jobiest.android.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Secure token and session storage backed by the Android Keystore (AES-256-GCM).
 * Never leaks access tokens to logs, cleartext storage, or backup archives.
 */
class SecureSessionManager(context: Context) {

    private val prefs: SharedPreferences

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        loadInitialSession()
    }

    private fun loadInitialSession() {
        val token = prefs.getString(KEY_ACCESS_TOKEN, null)
        val userId = prefs.getString(KEY_USER_ID, null)

        if (!token.isNullOrBlank() && !userId.isNullOrBlank()) {
            val refresh = prefs.getString(KEY_REFRESH_TOKEN, null)
            val email = prefs.getString(KEY_EMAIL, null)
            val expires = prefs.getLong(KEY_EXPIRES_AT, 0L)

            val session = Session(
                accessToken = token,
                refreshToken = refresh,
                userId = userId,
                email = email,
                expiresAt = expires
            )
            _authState.value = AuthState.Authenticated(session)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun saveSession(session: Session) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, session.accessToken)
            .putString(KEY_REFRESH_TOKEN, session.refreshToken)
            .putString(KEY_USER_ID, session.userId)
            .putString(KEY_EMAIL, session.email)
            .putLong(KEY_EXPIRES_AT, session.expiresAt)
            .apply()

        _authState.value = AuthState.Authenticated(session)
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
        _authState.value = AuthState.Unauthenticated
    }

    companion object {
        private const val PREFS_FILE_NAME = "jobiest_secure_vault"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_EXPIRES_AT = "expires_at"
    }
}
