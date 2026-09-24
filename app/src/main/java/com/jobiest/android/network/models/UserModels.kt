package com.jobiest.android.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    @SerialName("user_id") val userId: String? = null,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("target_roles") val targetRoles: List<String> = emptyList(),
    @SerialName("account_status") val accountStatus: String = "ACTIVE",
    val phone: String? = null,
    val email: String? = null,
    @SerialName("email_verified_at") val emailVerifiedAt: String? = null,
    @SerialName("password_set_at") val passwordSetAt: String? = null
)

@Serializable
data class CompletenessDto(
    val percent: Int = 0,
    val missing: List<String> = emptyList()
)

@Serializable
data class JobPreferencesDto(
    val locations: List<String> = emptyList(),
    @SerialName("remote_types") val remoteTypes: List<String> = emptyList(),
    @SerialName("application_mode") val applicationMode: String = "manual" // "manual" or "auto"
)

@Serializable
data class CareerProfileDto(
    val headline: String? = null,
    val skills: List<String> = emptyList(),
    val bio: String? = null
)

@Serializable
data class DashboardOverviewDto(
    val profile: ProfileDto? = null,
    val careerProfile: CareerProfileDto? = null,
    val preferences: JobPreferencesDto? = null,
    val completeness: CompletenessDto? = null,
    val entitlement: EntitlementDto? = null,
    val applicationCount: Int = 0,
    val inFlightCount: Int = 0,
    val interviewCount: Int = 0,
    val responseRate: Int = 0
)
