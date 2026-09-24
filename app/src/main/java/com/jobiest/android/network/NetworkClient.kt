package com.jobiest.android.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.jobiest.android.BuildConfig
import com.jobiest.android.security.SecureSessionManager
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Centralized network client manager for configuring Retrofit, OkHttp, and serialization.
 */
class NetworkClient(sessionManager: SecureSessionManager) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val contentType = "application/json".toMediaType()

    // Base OkHttpClient with timeouts
    private val baseOkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BASIC // Never log headers or bodies containing tokens
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
        )
        .build()

    // Supabase Auth HTTP client (uses public anon key header)
    private val supabaseOkHttpClient = baseOkHttpClient.newBuilder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    // Jobiest API HTTP client (injects user's Bearer JWT)
    private val jobiestOkHttpClient = baseOkHttpClient.newBuilder()
        .addInterceptor(AuthInterceptor(sessionManager))
        .build()

    // Retrofit instances
    val supabaseAuthService: SupabaseAuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SUPABASE_AUTH_URL)
            .client(supabaseOkHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(SupabaseAuthService::class.java)
    }

    val jobiestApiService: JobiestApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.JOBIEST_API_BASE_URL)
            .client(jobiestOkHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(JobiestApiService::class.java)
    }
}
