package com.example.studentreport.repository

import com.example.studentreport.entity.Report
import com.example.studentreport.entity.ReportStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ReportRepository : JpaRepository<Report, UUID>, JpaSpecificationExecutor<Report> {

    @EntityGraph(attributePaths = ["user", "room", "room.building", "category"])
    override fun findAll(spec: Specification<Report>, pageable: Pageable): Page<Report>

    fun countByStatus(status: ReportStatus): Int
    fun findByStatus(status: ReportStatus): List<Report>
    fun findTop3ByReporterIdOrderByCreatedAtDesc(reporterId: UUID): List<Report>
}