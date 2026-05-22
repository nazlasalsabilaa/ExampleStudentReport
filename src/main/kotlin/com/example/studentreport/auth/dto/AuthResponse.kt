package com.example.studentreport.auth.dto

import java.time.OffsetDateTime

data class AuthResponse(
    val token: String,
    val expiresAt: OffsetDateTime,
    val user: UserResponse
)