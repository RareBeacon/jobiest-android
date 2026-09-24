package com.jobiest.android.data.repository

import com.jobiest.android.network.JobiestApiService
import com.jobiest.android.network.models.AtsScanRequest
import com.jobiest.android.network.models.AtsScanResponse
import com.jobiest.android.network.models.ExecuteFreeToolRequest
import com.jobiest.android.network.models.FreeToolItem

class CareerToolsRepository(
    private val apiService: JobiestApiService
) {

    val availableTools: List<FreeToolItem> = listOf(
        FreeToolItem("free-ats-resume-scanner", "ATS Resume Scanner", "Scan your resume against ATS algorithms", "Resume", "search"),
        FreeToolItem("free-cover-letter-writer", "Cover Letter Writer", "AI tailored cover letters for any role", "Application", "description"),
        FreeToolItem("free-career-path-explorer", "Career Path Explorer", "Discover career trajectories & salary benchmarks", "Strategy", "trending_up"),
        FreeToolItem("free-interview-question-generator", "Interview Prep Generator", "Tailored behavioral & technical questions", "Interview", "psychology"),
        FreeToolItem("free-linkedin-headline-builder", "LinkedIn Headline Builder", "High-conversion headlines and summaries", "Branding", "badge"),
        FreeToolItem("free-follow-up-email-writer", "Follow-up Email Writer", "Professional interview follow-ups", "Email", "mail"),
        FreeToolItem("free-job-description-analyzer", "Job Description Analyzer", "Extract key skills & hidden requirements", "Analysis", "analytics"),
        FreeToolItem("free-skills-matcher", "Skills Gap Matcher", "Compare your skills against target roles", "Skills", "check_circle")
    )

    suspend fun scanAts(resumeText: String, jobDescription: String): Result<AtsScanResponse> {
        return try {
            val response = apiService.scanAts(AtsScanRequest(resumeText, jobDescription))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("ATS scan failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun executeTool(toolId: String, answers: Map<String, String>): Result<String> {
        return try {
            val response = apiService.executeFreeTool(ExecuteFreeToolRequest(toolId, answers))
            if (response.isSuccessful && response.body()?.resultText != null) {
                Result.success(response.body()!!.resultText!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Tool generation failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
