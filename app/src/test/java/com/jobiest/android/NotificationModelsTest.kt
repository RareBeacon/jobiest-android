package com.jobiest.android

import com.jobiest.android.network.models.NotificationItemDto
import com.jobiest.android.network.models.NotificationPreferencesDto
import com.jobiest.android.network.models.RegisterPushTokenRequest
import com.jobiest.android.network.models.TestNotificationRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationModelsTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun testRegisterPushTokenRequestSerialization() {
        val req = RegisterPushTokenRequest(
            token = "fcm_test_token_12345",
            platform = "android",
            deviceModel = "Pixel 8 Pro"
        )
        val encoded = json.encodeToString(req)
        assertTrue(encoded.contains("fcm_test_token_12345"))
        assertTrue(encoded.contains("Pixel 8 Pro"))
    }

    @Test
    fun testNotificationPreferencesSerialization() {
        val prefs = NotificationPreferencesDto(
            applications = true,
            jobs = true,
            resume = false,
            autoApply = true,
            security = true
        )
        val encoded = json.encodeToString(prefs)
        val decoded = json.decodeFromString<NotificationPreferencesDto>(encoded)

        assertTrue(decoded.applications)
        assertTrue(decoded.jobs)
        assertEquals(false, decoded.resume)
        assertTrue(decoded.autoApply)
    }

    @Test
    fun testNotificationItemDeserialization() {
        val payload = """
            {
                "id": "notif-uuid-1",
                "title": "Application Update",
                "body": "Your Airbnb application is ready for review.",
                "deep_link": "jobiest://applications",
                "type": "application",
                "created_at": "2026-09-24T10:00:00Z",
                "read": false
            }
        """.trimIndent()

        val parsed = json.decodeFromString<NotificationItemDto>(payload)
        assertEquals("notif-uuid-1", parsed.id)
        assertEquals("jobiest://applications", parsed.deepLink)
        assertEquals(false, parsed.read)
    }
}
