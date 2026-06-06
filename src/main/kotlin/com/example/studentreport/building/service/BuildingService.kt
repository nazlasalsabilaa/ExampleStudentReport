package com.example.studentreport.building.service

import com.example.studentreport.building.dto.BuildingResponse
import com.example.studentreport.building.dto.CreateBuildingRequest
import com.example.studentreport.building.dto.UpdateBuildingRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface BuildingService {
    fun getAllBuildings(search: String?, pageable: Pageable): Page<BuildingResponse>
    fun createBuilding(request: CreateBuildingRequest): BuildingResponse
    fun getBuildingById(id: UUID): BuildingResponse
    fun updateBuilding(id: UUID, request: UpdateBuildingRequest): BuildingResponse
    fun deleteBuilding(id: UUID)
}