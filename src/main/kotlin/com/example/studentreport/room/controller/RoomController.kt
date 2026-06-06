package com.example.studentreport.room.controller

import com.example.studentreport.auth.dto.ApiResponse
import com.example.studentreport.room.dto.CreateRoomRequest
import com.example.studentreport.room.dto.RoomResponse
import com.example.studentreport.room.dto.UpdateRoomRequest
import com.example.studentreport.room.service.RoomService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/rooms")
class RoomController(
    private val roomService: RoomService
) {

    @GetMapping
    fun getAllRooms(
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) buildingId: UUID?,
        @RequestParam(required = false) floor: Int?,
        @PageableDefault(size = 20) pageable: Pageable
    ): ApiResponse<Page<RoomResponse>> {
        val rooms = roomService.getAllRooms(search, buildingId, floor, pageable)
        return ApiResponse(success = true, message = "Rooms retrieved successfully", data = rooms)
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createRoom(
        @RequestHeader("Idempotency-Key", required = false) idempotencyKey: String?,
        @RequestBody request: CreateRoomRequest
    ): ApiResponse<RoomResponse> {
        val room = roomService.createRoom(request)
        return ApiResponse(success = true, message = "Room created successfully", data = room)
    }

    @GetMapping("/{id}")
    fun getRoomById(@PathVariable id: UUID): ApiResponse<RoomResponse> {
        val room = roomService.getRoomById(id)
        return ApiResponse(success = true, message = "Room retrieved successfully", data = room)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateRoom(
        @PathVariable id: UUID,
        @RequestHeader("Idempotency-Key", required = false) idempotencyKey: String?,
        @RequestBody request: UpdateRoomRequest
    ): ApiResponse<RoomResponse> {
        val room = roomService.updateRoom(id, request)
        return ApiResponse(success = true, message = "Room updated successfully", data = room)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteRoom(@PathVariable id: UUID): ApiResponse<Unit> {
        roomService.deleteRoom(id)
        return ApiResponse(success = true, message = "Room deleted successfully")
    }
}