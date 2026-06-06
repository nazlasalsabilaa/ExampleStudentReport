package com.example.studentreport.building.controller

import com.example.studentreport.auth.dto.ApiResponse
import com.example.studentreport.building.dto.BuildingResponse
import com.example.studentreport.building.dto.CreateBuildingRequest
import com.example.studentreport.building.dto.UpdateBuildingRequest
import com.example.studentreport.building.service.BuildingService
import com.example.studentreport.room.dto.RoomResponse
import com.example.studentreport.room.service.RoomService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/buildings")
class BuildingController(
    private val buildingService: BuildingService,
    private val roomService: RoomService
) {

    @GetMapping
    fun getAllBuildings(
        @RequestParam(required = false) search: String?,
        @PageableDefault(size = 20) pageable: Pageable
    ): ApiResponse<Page<BuildingResponse>> {
        val buildings = buildingService.getAllBuildings(search, pageable)
        return ApiResponse(success = true, message = "Buildings retrieved successfully", data = buildings)
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createBuilding(
        @RequestHeader("Idempotency-Key", required = false) idempotencyKey: String?,
        @RequestBody request: CreateBuildingRequest
    ): ApiResponse<BuildingResponse> {
        val building = buildingService.createBuilding(request)
        return ApiResponse(success = true, message = "Building created successfully", data = building)
    }

    @GetMapping("/{id}")
    fun getBuildingById(@PathVariable id: UUID): ApiResponse<BuildingResponse> {
        val building = buildingService.getBuildingById(id)
        return ApiResponse(success = true, message = "Building retrieved successfully", data = building)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateBuilding(
        @PathVariable id: UUID,
        @RequestHeader("Idempotency-Key", required = false) idempotencyKey: String?,
        @RequestBody request: UpdateBuildingRequest
    ): ApiResponse<BuildingResponse> {
        val building = buildingService.updateBuilding(id, request)
        return ApiResponse(success = true, message = "Building updated successfully", data = building)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteBuilding(@PathVariable id: UUID): ApiResponse<Unit> {
        buildingService.deleteBuilding(id)
        return ApiResponse(success = true, message = "Building deleted successfully")
    }

    @GetMapping("/{id}/rooms")
    fun getRoomsByBuildingId(
        @PathVariable id: UUID,
        @PageableDefault(size = 20) pageable: Pageable
    ): ApiResponse<Page<RoomResponse>> {
        val rooms = roomService.getRoomsByBuildingId(id, pageable)
        return ApiResponse(success = true, message = "Rooms retrieved successfully", data = rooms)
    }
}