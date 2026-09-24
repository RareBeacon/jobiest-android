package com.jobiest.android.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationJobSummaryDto(
    val id: String? = null,
    val company: String? = null,
    val title: String? = null,
    val url: String? = null
)

@Serializable
data class ApplicationDto(
    val id: String,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("job_id") val jobId: String? = null,
    val email: String? = null,
    val status: String = "DRAFT", // DRAFT, PREPARING, AWAITING_APPROVAL, APPROVED, QUEUED, SUBMITTED, INTERVIEW, REJECTED, WITHDRAWN
    @SerialName("idempotency_key") val idempotencyKey: String? = null,
    @SerialName("submitted_at") val submittedAt: String? = null,
    val error: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val fitScore: Int? = null,
    val job: ApplicationJobSummaryDto? = null,
    val jobs: ApplicationJobSummaryDto? = null
) {
    val displayJob: ApplicationJobSummaryDto?
        get() = job ?: jobs
}

@Serializable
data class ApplicationsResponse(
    val applications: List<ApplicationDto> = emptyList(),
    val automationEnabled: Boolean = false
)

@Serializable
data class CreateManualApplicationRequest(
    val mode: String = "manual",
    val company: String,
    val title: String,
    val url: String,
    val status: String = "DRAFT"
)

@Serializable
data class TargetJobRequest(
    val url: String
)

@Serializable
data class PrepareApplicationRequest(
    val jobId: String,
    val roleTitle: String? = null,
    val company: String? = null
)

@Serializable
data class GenericActionResponse(
    val ok: Boolean = true,
    val error: String? = null,
    val message: String? = null
)
