package com.example.studentreport.auth.dto

import java.time.OffsetDateTime
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val role: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)
