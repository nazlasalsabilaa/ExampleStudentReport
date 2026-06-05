package com.example.studentreport.report.dto

import java.time.OffsetDateTime
import java.util.UUID

data class ReportImageResponse(
    val id: UUID,
    val reportId: UUID,
    val imageUrl: String,
    val uploadedAt: OffsetDateTime
)