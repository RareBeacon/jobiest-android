package com.jobiest.android.network.models

import kotlinx.serialization.Serializable

@Serializable
data class AtsScanRequest(
    val resumeText: String,
    val jobDescription: String
)

@Serializable
data class AtsScanResponse(
    val score: Int = 0,
    val matchedKeywords: List<String> = emptyList(),
    val missingKeywords: List<String> = emptyList(),
    val feedback: List<String> = emptyList()
)

@Serializable
data class FreeToolItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val icon: String
)

@Serializable
data class ExecuteFreeToolRequest(
    val toolId: String,
    val answers: Map<String, String>
)

@Serializable
data class ExecuteFreeToolResponse(
    val ok: Boolean = true,
    val resultText: String? = null,
    val error: String? = null
)
