package com.example.studentreport.report.dto

import com.example.studentreport.auth.dto.UserResponse
import com.example.studentreport.entity.ReportStatus
import java.time.OffsetDateTime
import java.util.UUID

data class ReportLogResponse(
    val id: UUID,
    val reportId: UUID,
    val admin: UserResponse,
    val oldStatus: ReportStatus,
    val newStatus: ReportStatus,
    val notes: String?,
    val createdAt: OffsetDateTime
)