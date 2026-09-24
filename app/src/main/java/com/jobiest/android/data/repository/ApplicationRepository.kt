package com.jobiest.android.data.repository

import com.jobiest.android.network.JobiestApiService
import com.jobiest.android.network.models.*

class ApplicationRepository(
    private val apiService: JobiestApiService
) {

    suspend fun getApplications(): Result<ApplicationsResponse> {
        return try {
            val response = apiService.getApplications()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch applications: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createManualApplication(
        company: String,
        title: String,
        url: String,
        status: String = "DRAFT"
    ): Result<Unit> {
        return try {
            val req = CreateManualApplicationRequest(
                company = company.trim(),
                title = title.trim(),
                url = url.trim(),
                status = status
            )
            val response = apiService.createManualApplication(req)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create application: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun targetJob(jobUrl: String): Result<Unit> {
        return try {
            val response = apiService.targetJob(TargetJobRequest(jobUrl.trim()))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to target job: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun approveApplication(applicationId: String): Result<Unit> {
        return try {
            val response = apiService.approveApplication(applicationId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Failed to approve application: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun autoSubmitApplication(applicationId: String): Result<Unit> {
        return try {
            val response = apiService.autoSubmitApplication(applicationId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Failed to auto-submit: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectApplication(applicationId: String): Result<Unit> {
        return try {
            val response = apiService.rejectApplication(applicationId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Failed to reject application: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun withdrawApplication(applicationId: String): Result<Unit> {
        return try {
            val response = apiService.withdrawApplication(applicationId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Failed to withdraw application: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
