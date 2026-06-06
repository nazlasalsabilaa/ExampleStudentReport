package com.example.studentreport.category.dto

import java.time.OffsetDateTime
import java.util.UUID

data class CategoryResponse(
    val id: UUID,
    val name: String,
    val description: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)