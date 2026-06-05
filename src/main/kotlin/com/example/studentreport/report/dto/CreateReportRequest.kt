package com.example.studentreport.report.dto

import java.util.UUID

data class CreateReportRequest(
    val categoryId: UUID,
    val roomId: UUID,
    val title: String,
    val description: String? = null
)