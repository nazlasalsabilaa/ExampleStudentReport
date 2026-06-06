package com.example.studentreport.building.dto

import java.time.OffsetDateTime
import java.util.UUID

data class BuildingResponse(
    val id: UUID,
    val name: String,
    val code: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)