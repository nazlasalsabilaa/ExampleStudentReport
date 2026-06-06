package com.example.studentreport.report.service

import com.example.studentreport.auth.dto.UserResponse
import com.example.studentreport.entity.ReportLog
import com.example.studentreport.entity.ReportStatus
import com.example.studentreport.report.dto.ReportLogResponse
import com.example.studentreport.repository.ReportLogRepository
import com.example.studentreport.repository.ReportRepository
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneOffset
import java.util.UUID

@Service
class ReportLogServiceImpl(
    private val reportLogRepository: ReportLogRepository,
    private val reportRepository: ReportRepository
) : ReportLogService {

    @Transactional(readOnly = true)
    override fun getLogsByReportId(
        reportId: UUID,
        currentUserId: UUID,
        isAdmin: Boolean,
        pageable: Pageable
    ): Page<ReportLogResponse> {
        val report = reportRepository.findById(reportId)
            .orElseThrow { IllegalArgumentException("Report not found") }

        if (!isAdmin && report.reporterId != currentUserId) {
            throw AccessDeniedException("Access denied: You can only view logs for your own reports")
        }

        return reportLogRepository.findByReportIdOrderByCreatedAtDesc(reportId, pageable).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    override fun getAllReportLogs(
        adminId: UUID?,
        reportId: UUID?,
        newStatus: ReportStatus?,
        pageable: Pageable
    ): Page<ReportLogResponse> {
        val spec = Specification<ReportLog> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            adminId?.let { predicates.add(cb.equal(root.get<UUID>("adminId"), it)) }
            reportId?.let { predicates.add(cb.equal(root.get<UUID>("reportId"), it)) }
            newStatus?.let { predicates.add(cb.equal(root.get<ReportStatus>("newStatus"), it)) }

            cb.and(*predicates.toTypedArray())
        }
        return reportLogRepository.findAll(spec, pageable).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    override fun getReportLogById(id: UUID): ReportLogResponse {
        val log = reportLogRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Report log not found") }

        return log.toResponse()
    }

    private fun ReportLog.toResponse(): ReportLogResponse {
        val adminResponse = UserResponse(
            id = this.admin!!.id!!,
            name = this.admin!!.name,
            email = this.admin!!.email,
            role = this.admin!!.role.name,
            createdAt = this.admin!!.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = this.admin!!.updatedAt.atOffset(ZoneOffset.UTC)
        )

        return ReportLogResponse(
            id = this.id!!,
            reportId = this.reportId,
            admin = adminResponse,
            oldStatus = this.oldStatus,
            newStatus = this.newStatus,
            notes = this.notes,
            createdAt = this.createdAt.atOffset(ZoneOffset.UTC)
        )
    }
}