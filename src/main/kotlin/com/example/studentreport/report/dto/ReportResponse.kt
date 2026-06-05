package com.example.studentreport.report.dto

import com.example.studentreport.auth.dto.UserResponse
import com.example.studentreport.entity.ReportStatus
import java.time.OffsetDateTime
import java.util.UUID

data class ReportResponse(
    val id: UUID,
    val version: Long,
    val reporter: UserResponse,
    val categoryId: UUID,
    val roomId: UUID,
    val title: String,
    val description: String?,
    val status: ReportStatus,
    val upvoteCount: Int,
    val images: List<ReportImageResponse>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val deletedAt: OffsetDateTime? = null
)