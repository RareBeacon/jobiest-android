package com.jobiest.android.data.repository

import com.jobiest.android.network.JobiestApiService
import com.jobiest.android.network.models.CompletenessDto
import com.jobiest.android.network.models.JobPreferencesDto
import com.jobiest.android.network.models.ProfileDto

class ProfileRepository(
    private val apiService: JobiestApiService
) {

    suspend fun getProfile(): Result<ProfileDto> {
        return try {
            val response = apiService.getProfile()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(profile: ProfileDto): Result<Unit> {
        return try {
            val response = apiService.updateProfile(profile)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Failed to update profile: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCompleteness(): Result<CompletenessDto> {
        return try {
            val response = apiService.getCompleteness()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load profile strength: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPreferences(): Result<JobPreferencesDto> {
        return try {
            val response = apiService.getPreferences()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load preferences: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setApplicationMode(mode: String): Result<Unit> {
        return try {
            val response = apiService.setApplicationMode(mapOf("mode" to mode))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Failed to set mode: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
