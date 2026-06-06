package com.example.studentreport.user.dto

import java.util.UUID

data class StudentDataResponse(
    val id: UUID,
    val userId: UUID,
    val nim: String,
    val faculty: String,
    val major: String,
    val year: Int
)