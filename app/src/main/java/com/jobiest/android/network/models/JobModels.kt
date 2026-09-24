package com.jobiest.android.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JobDto(
    val id: String,
    val source: String? = null,
    @SerialName("external_id") val externalId: String? = null,
    val company: String? = null,
    val title: String? = null,
    val url: String? = null,
    val description: String? = null,
    val location: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val isSaved: Boolean = false,
    val matchScore: Int? = null,
    val fitReason: String? = null,
    val salary: String? = null
) {
    val displayCompany: String get() = company ?: "Opportunity"
    val displayTitle: String get() = title ?: "Position"
}

@Serializable
data class PaginationDto(
    val page: Int = 1,
    val limit: Int = 20,
    val total: Int = 0,
    val totalPages: Int = 0
)

@Serializable
data class JobsResponse(
    val jobs: List<JobDto> = emptyList(),
    val pagination: PaginationDto = PaginationDto()
)

@Serializable
data class SaveJobRequest(
    val jobId: String
)

@Serializable
data class SavedJobItemDto(
    @SerialName("savedAt") val savedAt: String? = null,
    val job: JobDto? = null
)

@Serializable
data class SavedJobsResponse(
    val savedJobs: List<SavedJobItemDto> = emptyList()
)
