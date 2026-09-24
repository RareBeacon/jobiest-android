package com.jobiest.android.data.repository

import com.jobiest.android.network.JobiestApiService
import com.jobiest.android.network.models.JobDto
import com.jobiest.android.network.models.JobsResponse
import com.jobiest.android.network.models.SaveJobRequest
import com.jobiest.android.network.models.SavedJobItemDto

class JobRepository(
    private val apiService: JobiestApiService
) {

    suspend fun searchJobs(
        query: String? = null,
        location: String? = null,
        source: String? = null,
        page: Int = 1,
        limit: Int = 20
    ): Result<JobsResponse> {
        return try {
            val response = apiService.searchJobs(query, location, source, page, limit)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load jobs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSavedJobs(): Result<List<SavedJobItemDto>> {
        return try {
            val response = apiService.getSavedJobs()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.savedJobs)
            } else {
                Result.failure(Exception("Failed to fetch saved jobs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleSaveJob(jobId: String, currentSaved: Boolean): Result<Boolean> {
        return try {
            val response = if (currentSaved) {
                apiService.deleteSavedJob(jobId)
            } else {
                apiService.saveJob(SaveJobRequest(jobId))
            }
            if (response.isSuccessful) {
                Result.success(!currentSaved)
            } else {
                Result.failure(Exception("Failed to update bookmark: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveJob(jobId: String): Result<Boolean> = toggleSaveJob(jobId, false)
    suspend fun unsaveJob(jobId: String): Result<Boolean> = toggleSaveJob(jobId, true)
}
