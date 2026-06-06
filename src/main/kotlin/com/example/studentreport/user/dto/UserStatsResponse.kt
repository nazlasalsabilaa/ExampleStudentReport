package com.example.studentreport.user.dto

import java.time.Instant
import java.util.UUID

data class UserStatsResponse(
    val id: UUID,
    val userId: UUID,
    val reportCount: Int,
    val inProgressReport: Int,
    val pendingReport: Int,
    val completedReport: Int,
    val rejectedReport: Int,
    val updatedAt: Instant
)