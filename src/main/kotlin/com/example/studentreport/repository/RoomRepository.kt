package com.example.studentreport.repository

import com.example.studentreport.entity.Room
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RoomRepository : JpaRepository<Room, UUID> {
    fun findAll(spec: Specification<Room>, pageable: Pageable): Page<Room>
    fun existsByCode(code: String): Boolean
    fun findByBuildingId(buildingId: UUID, pageable: Pageable): Page<Room>
}