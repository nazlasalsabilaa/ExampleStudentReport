package com.example.studentreport.user.dto

import java.time.Instant
import java.util.UUID

data class UserStatsResponse(
    val id: UUID,
    val userId: UUID,
    val reportCount: Int,
    val pendingCount: Int,
    val completedCount: Int,
    val rejectedCount: Int,
    val updatedAt: Instant
)