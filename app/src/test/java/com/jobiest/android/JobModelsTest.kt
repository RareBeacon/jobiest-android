package com.jobiest.android

import com.jobiest.android.network.models.ApplicationDto
import com.jobiest.android.network.models.ApplicationJobSummaryDto
import com.jobiest.android.network.models.JobDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class JobModelsTest {

    @Test
    fun testJobDtoCreation() {
        val job = JobDto(
            id = "test-job-uuid",
            source = "greenhouse",
            company = "TestCorp",
            title = "Android Engineer",
            url = "https://boards.greenhouse.io/test/jobs/123",
            location = "Remote",
            isSaved = true
        )

        assertEquals("test-job-uuid", job.id)
        assertEquals("Android Engineer", job.title)
        assertTrue(job.isSaved)
    }

    @Test
    fun testApplicationDisplayJobFallback() {
        val summary = ApplicationJobSummaryDto(
            id = "j1",
            company = "Jobiest Inc",
            title = "Kotlin Architect"
        )
        val app = ApplicationDto(
            id = "app-uuid",
            status = "DRAFT",
            job = summary
        )

        assertEquals("Jobiest Inc", app.displayJob?.company)
        assertEquals("Kotlin Architect", app.displayJob?.title)
    }
}
