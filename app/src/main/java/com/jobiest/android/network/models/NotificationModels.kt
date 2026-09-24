package com.jobiest.android.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterPushTokenRequest(
    val token: String,
    val platform: String = "android",
    @SerialName("deviceModel") val deviceModel: String? = null
)

@Serializable
data class RegisterPushTokenResponse(
    val ok: Boolean = true,
    val message: String? = null,
    val registeredAt: String? = null
)

@Serializable
data class UnregisterPushTokenRequest(
    val token: String
)

@Serializable
data class NotificationPreferencesDto(
    val applications: Boolean = true,
    val jobs: Boolean = true,
    val resume: Boolean = true,
    @SerialName("autoApply") val autoApply: Boolean = true,
    val security: Boolean = true
)

@Serializable
data class NotificationPreferencesResponse(
    val preferences: NotificationPreferencesDto = NotificationPreferencesDto()
)

@Serializable
data class TestNotificationRequest(
    val type: String = "job_match",
    val customMessage: String? = null
)

@Serializable
data class TestNotificationResponse(
    val ok: Boolean = true,
    val deliveredTo: Int = 0,
    val notification: TestNotificationDetailDto? = null
)

@Serializable
data class TestNotificationDetailDto(
    val title: String,
    val body: String,
    val deepLink: String? = null,
    val type: String? = null,
    val sentAt: String? = null
)

@Serializable
data class NotificationItemDto(
    val id: String,
    val title: String,
    val body: String,
    @SerialName("deep_link") val deepLink: String? = null,
    val type: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val read: Boolean = false
)

@Serializable
data class NotificationsResponse(
    val notifications: List<NotificationItemDto> = emptyList()
)
