package com.jobiest.android.network

import com.jobiest.android.network.models.SupabaseLoginRequest
import com.jobiest.android.network.models.SupabaseRecoverPasswordRequest
import com.jobiest.android.network.models.SupabaseRefreshTokenRequest
import com.jobiest.android.network.models.SupabaseSignupRequest
import com.jobiest.android.network.models.SupabaseTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Direct HTTPS calls to Supabase Auth API using the safe public anon key.
 */
interface SupabaseAuthService {

    @POST("token")
    suspend fun login(
        @Query("grant_type") grantType: String = "password",
        @Body body: SupabaseLoginRequest
    ): Response<SupabaseTokenResponse>

    @POST("signup")
    suspend fun signup(
        @Body body: SupabaseSignupRequest
    ): Response<SupabaseTokenResponse>

    @POST("recover")
    suspend fun recoverPassword(
        @Body body: SupabaseRecoverPasswordRequest
    ): Response<Unit>

    @POST("token")
    suspend fun refreshToken(
        @Query("grant_type") grantType: String = "refresh_token",
        @Body body: SupabaseRefreshTokenRequest
    ): Response<SupabaseTokenResponse>
}
