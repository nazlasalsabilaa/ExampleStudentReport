package com.example.studentreport.room.service

import com.example.studentreport.room.dto.CreateRoomRequest
import com.example.studentreport.room.dto.RoomResponse
import com.example.studentreport.room.dto.UpdateRoomRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface RoomService {
    fun getAllRooms(search: String?, buildingId: UUID?, floor: Int?, pageable: Pageable): Page<RoomResponse>
    fun createRoom(request: CreateRoomRequest): RoomResponse
    fun getRoomById(id: UUID): RoomResponse
    fun updateRoom(id: UUID, request: UpdateRoomRequest): RoomResponse
    fun deleteRoom(id: UUID)
    fun getRoomsByBuildingId(buildingId: UUID, pageable: Pageable): Page<RoomResponse>
}