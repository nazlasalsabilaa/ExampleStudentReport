package com.example.studentreport.report.dto

import java.util.UUID

data class UpdateReportRequest(
    val categoryId: UUID? = null,
    val roomId: UUID? = null,
    val title: String? = null,
    val description: String? = null
)