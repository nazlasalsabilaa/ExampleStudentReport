package com.example.studentreport.repository

import com.example.studentreport.entity.Building
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BuildingRepository : JpaRepository<Building, UUID>, JpaSpecificationExecutor<Building> {
    fun existsByCode(code: String): Boolean
}