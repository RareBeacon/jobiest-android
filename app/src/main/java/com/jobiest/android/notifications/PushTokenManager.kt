package com.jobiest.android.notifications

import android.content.Context
import android.os.Build
import com.jobiest.android.network.JobiestApiService
import com.jobiest.android.network.models.RegisterPushTokenRequest
import com.jobiest.android.network.models.UnregisterPushTokenRequest
import com.jobiest.android.security.SecureSessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class PushTokenManager(
    private val context: Context,
    private val apiService: JobiestApiService,
    private val sessionManager: SecureSessionManager
) {
    private val prefs = context.getSharedPreferences("jobiest_push_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PUSH_TOKEN = "push_token"
        private const val KEY_TOKEN_REGISTERED = "token_registered"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    /**
     * Get or generate a persistent device push token.
     */
    fun getOrCreateDeviceToken(): String {
        var token = prefs.getString(KEY_PUSH_TOKEN, null)
        if (token.isNullOrBlank()) {
            token = "fcm_${UUID.randomUUID()}_${Build.MODEL.replace(" ", "_")}"
            prefs.edit().putString(KEY_PUSH_TOKEN, token).apply()
        }
        return token
    }

    fun isRegisteredWithBackend(): Boolean {
        return prefs.getBoolean(KEY_TOKEN_REGISTERED, false)
    }

    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    /**
     * Register device token with backend authenticated session.
     */
    suspend fun registerDeviceToken(): Boolean = withContext(Dispatchers.IO) {
        if (sessionManager.getAccessToken().isNullOrBlank()) return@withContext false
        val token = getOrCreateDeviceToken()
        val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})"
        try {
            val response = apiService.registerPushToken(
                RegisterPushTokenRequest(
                    token = token,
                    platform = "android",
                    deviceModel = deviceModel
                )
            )
            val success = response.isSuccessful && response.body()?.ok == true
            if (success) {
                prefs.edit().putBoolean(KEY_TOKEN_REGISTERED, true).apply()
            }
            success
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Unregister device token with backend on logout.
     */
    suspend fun unregisterDeviceToken(): Boolean = withContext(Dispatchers.IO) {
        val token = prefs.getString(KEY_PUSH_TOKEN, null) ?: return@withContext true
        try {
            val response = apiService.unregisterPushToken(UnregisterPushTokenRequest(token = token))
            prefs.edit().putBoolean(KEY_TOKEN_REGISTERED, false).apply()
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }
}
