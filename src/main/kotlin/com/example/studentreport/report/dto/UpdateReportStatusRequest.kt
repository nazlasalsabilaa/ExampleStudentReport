package com.example.studentreport.report.dto

import com.example.studentreport.entity.ReportStatus

data class UpdateReportStatusRequest(
    val status: ReportStatus,
    val notes: String? = null
)