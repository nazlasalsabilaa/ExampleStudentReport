package com.example.studentreport.room.dto

import java.util.UUID

data class UpdateRoomRequest(
    val buildingId: UUID? = null,
    val name: String? = null,
    val floor: Int? = null,
    val code: String? = null
)