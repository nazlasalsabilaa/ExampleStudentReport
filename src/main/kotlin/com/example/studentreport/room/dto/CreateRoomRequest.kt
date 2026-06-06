package com.example.studentreport.room.dto

import java.util.UUID

data class CreateRoomRequest(
    val buildingId: UUID,
    val name: String,
    val floor: Int,
    val code: String
)