package com.example.studentreport.repository

import com.example.studentreport.entity.ReportLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ReportLogRepository : JpaRepository<ReportLog, UUID>, JpaSpecificationExecutor<ReportLog> {
    fun findByReportIdOrderByCreatedAtDesc(reportId: UUID, pageable: Pageable): Page<ReportLog>
}