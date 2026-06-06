package com.example.studentreport.room.dto

import com.example.studentreport.building.dto.BuildingResponse
import java.time.OffsetDateTime
import java.util.UUID

data class RoomResponse(
    val id: UUID,
    val buildingId: UUID,
    val building: BuildingResponse?,
    val name: String,
    val floor: Int,
    val code: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)