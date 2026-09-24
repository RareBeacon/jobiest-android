package com.jobiest.android

import com.jobiest.android.network.models.JobiestSignupRequest
import com.jobiest.android.network.models.JobiestVerifyRequest
import com.jobiest.android.network.models.SupabaseTokenResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test

class AuthModelsTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun testSupabaseTokenResponseWithMissingAccessTokenDoesNotCrash() {
        // Unconfirmed user payload where GoTrue returns user attributes at the root
        val unconfirmedUserJson = """
            {
                "id": "e394017b-6009-444a-91ff-2fbe5c52c930",
                "aud": "authenticated",
                "role": "",
                "email": "candidate@example.com"
            }
        """.trimIndent()

        val parsed = json.decodeFromString<SupabaseTokenResponse>(unconfirmedUserJson)
        assertNull("access_token should default to null when unconfirmed", parsed.accessToken)
        assertNull("refresh_token should default to null", parsed.refreshToken)
        assertEquals("candidate@example.com", parsed.resolvedEmail)
        assertEquals("e394017b-6009-444a-91ff-2fbe5c52c930", parsed.resolvedId)
    }

    @Test
    fun testSupabaseTokenResponseWithSession() {
        // Confirmed user payload with session tokens and nested user
        val confirmedSessionJson = """
            {
                "access_token": "mock-jwt-token",
                "token_type": "bearer",
                "expires_in": 3600,
                "refresh_token": "mock-refresh-token",
                "user": {
                    "id": "user-uuid-123",
                    "email": "active@jobiest.com"
                }
            }
        """.trimIndent()

        val parsed = json.decodeFromString<SupabaseTokenResponse>(confirmedSessionJson)
        assertEquals("mock-jwt-token", parsed.accessToken)
        assertEquals("mock-refresh-token", parsed.refreshToken)
        assertEquals("active@jobiest.com", parsed.resolvedEmail)
        assertEquals("user-uuid-123", parsed.resolvedId)
    }

    @Test
    fun testJobiestSignupAndVerifyPayloads() {
        val signupReq = JobiestSignupRequest(
            email = "tester@jobiest.com",
            password = "SecretPassword123!",
            fullName = "Chidi Okafor",
            phone = "+2348012345678"
        )
        assertEquals("tester@jobiest.com", signupReq.email)
        assertEquals("+2348012345678", signupReq.phone)

        val verifyReq = JobiestVerifyRequest(
            action = "confirm",
            email = "tester@jobiest.com",
            code = "123456"
        )
        assertEquals("confirm", verifyReq.action)
        assertEquals("123456", verifyReq.code)
    }
}
