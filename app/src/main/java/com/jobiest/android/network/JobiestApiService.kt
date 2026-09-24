package com.jobiest.android.network

import com.jobiest.android.network.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit interface for the Jobiest HTTPS API.
 * All protected endpoints require a valid Bearer JWT provided by AuthInterceptor.
 */
interface JobiestApiService {

    // === AUTHENTICATION (/api/auth/*) ===
    @POST("auth/signup")
    suspend fun signup(@Body body: JobiestSignupRequest): Response<JobiestSignupResponse>

    @POST("auth/verify")
    suspend fun verifyEmail(@Body body: JobiestVerifyRequest): Response<JobiestVerifyResponse>

    // === PROFILE & PREFERENCES ===
    @GET("profile")
    suspend fun getProfile(): Response<ProfileDto>

    @POST("profile")
    suspend fun updateProfile(@Body profile: ProfileDto): Response<GenericActionResponse>

    @GET("profile/completeness")
    suspend fun getCompleteness(): Response<CompletenessDto>

    @GET("preferences")
    suspend fun getPreferences(): Response<JobPreferencesDto>

    @POST("preferences")
    suspend fun updatePreferences(@Body prefs: JobPreferencesDto): Response<GenericActionResponse>

    @POST("preferences/mode")
    suspend fun setApplicationMode(@Body body: Map<String, String>): Response<GenericActionResponse>

    // === JOBS & SEARCH ===
    @GET("jobs")
    suspend fun searchJobs(
        @Query("q") query: String? = null,
        @Query("location") location: String? = null,
        @Query("source") source: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<JobsResponse>

    @GET("saved-jobs")
    suspend fun getSavedJobs(): Response<SavedJobsResponse>

    @POST("saved-jobs")
    suspend fun saveJob(@Body body: SaveJobRequest): Response<GenericActionResponse>

    @DELETE("saved-jobs")
    suspend fun deleteSavedJob(@Query("jobId") jobId: String): Response<GenericActionResponse>

    // === APPLICATIONS & PIPELINE ===
    @GET("applications")
    suspend fun getApplications(): Response<ApplicationsResponse>

    @POST("applications")
    suspend fun createManualApplication(@Body body: CreateManualApplicationRequest): Response<GenericActionResponse>

    @POST("applications/target")
    suspend fun targetJob(@Body body: TargetJobRequest): Response<GenericActionResponse>

    @POST("applications/prepare")
    suspend fun prepareApplication(@Body body: PrepareApplicationRequest): Response<GenericActionResponse>

    @POST("applications/{id}/approve")
    suspend fun approveApplication(@Path("id") applicationId: String): Response<GenericActionResponse>

    @POST("applications/{id}/auto-submit")
    suspend fun autoSubmitApplication(@Path("id") applicationId: String): Response<GenericActionResponse>

    @POST("applications/{id}/submit")
    suspend fun submitApplication(@Path("id") applicationId: String): Response<GenericActionResponse>

    @POST("applications/{id}/reject")
    suspend fun rejectApplication(@Path("id") applicationId: String): Response<GenericActionResponse>

    @POST("applications/{id}/withdraw")
    suspend fun withdrawApplication(@Path("id") applicationId: String): Response<GenericActionResponse>

    // === CAREER TOOLS & ATS ===
    @POST("ats/scan")
    suspend fun scanAts(@Body body: AtsScanRequest): Response<AtsScanResponse>

    @POST("free-tools/generate")
    suspend fun executeFreeTool(@Body body: ExecuteFreeToolRequest): Response<ExecuteFreeToolResponse>

    // === BILLING & ENTITLEMENTS ===
    @GET("entitlements")
    suspend fun getEntitlements(): Response<EntitlementDto>

    @GET("billing/providers")
    suspend fun getBillingProviders(): Response<BillingProvidersResponse>

    @POST("billing/flutterwave/create")
    suspend fun createFlutterwavePayment(@Body body: CreatePaymentRequest): Response<CreatePaymentResponse>

    @POST("billing/paystack/create")
    suspend fun createPaystackPayment(@Body body: CreatePaymentRequest): Response<CreatePaymentResponse>

    // === NOTIFICATIONS (/api/notifications/*) ===
    @POST("notifications/register-token")
    suspend fun registerPushToken(@Body body: RegisterPushTokenRequest): Response<RegisterPushTokenResponse>

    @POST("notifications/unregister-token")
    suspend fun unregisterPushToken(@Body body: UnregisterPushTokenRequest): Response<GenericActionResponse>

    @GET("notifications/preferences")
    suspend fun getNotificationPreferences(): Response<NotificationPreferencesResponse>

    @POST("notifications/preferences")
    suspend fun updateNotificationPreferences(@Body body: NotificationPreferencesDto): Response<GenericActionResponse>

    @POST("notifications/test")
    suspend fun sendTestNotification(@Body body: TestNotificationRequest): Response<TestNotificationResponse>

    @GET("notifications")
    suspend fun getNotifications(): Response<NotificationsResponse>
}

