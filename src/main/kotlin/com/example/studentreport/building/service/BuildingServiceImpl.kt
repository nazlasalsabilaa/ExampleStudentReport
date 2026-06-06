package com.example.studentreport.building.service

import com.example.studentreport.building.dto.BuildingResponse
import com.example.studentreport.building.dto.CreateBuildingRequest
import com.example.studentreport.building.dto.UpdateBuildingRequest
import com.example.studentreport.entity.Building
import com.example.studentreport.repository.BuildingRepository
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

@Service
class BuildingServiceImpl(
    private val buildingRepository: BuildingRepository
) : BuildingService {

    @Transactional(readOnly = true)
    override fun getAllBuildings(search: String?, pageable: Pageable): Page<BuildingResponse> {
        val spec = Specification<Building> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            if (!search.isNullOrBlank()) {
                val searchPattern = "%${search.lowercase()}%"
                val namePredicate = cb.like(cb.lower(root.get("name")), searchPattern)
                val codePredicate = cb.like(cb.lower(root.get("code")), searchPattern)
                predicates.add(cb.or(namePredicate, codePredicate))
            }
            cb.and(*predicates.toTypedArray())
        }
        return buildingRepository.findAll(spec, pageable).map { it.toResponse() }
    }

    @Transactional
    override fun createBuilding(request: CreateBuildingRequest): BuildingResponse {
        if (buildingRepository.existsByCode(request.code)) {
            throw IllegalArgumentException("Building code already exists")
        }
        val now = Instant.now()
        val building = Building(
            name = request.name,
            code = request.code,
            createdAt = now,
            updatedAt = now
        )
        return buildingRepository.save(building).toResponse()
    }

    @Transactional(readOnly = true)
    override fun getBuildingById(id: UUID): BuildingResponse {
        val building = buildingRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Building not found") }
        return building.toResponse()
    }

    @Transactional
    override fun updateBuilding(id: UUID, request: UpdateBuildingRequest): BuildingResponse {
        val building = buildingRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Building not found") }

        request.code?.let { newCode ->
            if (newCode != building.code && buildingRepository.existsByCode(newCode)) {
                throw IllegalArgumentException("Building code already exists")
            }
            building.code = newCode
        }
        request.name?.let { building.name = it }
        building.updatedAt = Instant.now()

        return buildingRepository.save(building).toResponse()
    }

    @Transactional
    override fun deleteBuilding(id: UUID) {
        val building = buildingRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Building not found") }
        buildingRepository.delete(building)
    }

    private fun Building.toResponse(): BuildingResponse {
        return BuildingResponse(
            id = this.id!!,
            name = this.name,
            code = this.code,
            createdAt = this.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = this.updatedAt.atOffset(ZoneOffset.UTC)
        )
    }
}