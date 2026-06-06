package com.example.studentreport.report.service

import com.example.studentreport.auth.dto.UserResponse
import com.example.studentreport.entity.Report
import com.example.studentreport.entity.ReportLog
import com.example.studentreport.entity.ReportStatus
import com.example.studentreport.report.dto.CreateReportRequest
import com.example.studentreport.report.dto.ReportImageResponse
import com.example.studentreport.report.dto.ReportResponse
import com.example.studentreport.report.dto.UpdateReportRequest
import com.example.studentreport.report.dto.UpdateReportStatusRequest
import com.example.studentreport.repository.ReportRepository
import com.example.studentreport.repository.UserStatsRepository
import com.example.studentreport.repository.specification.ReportSpecification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

@Service
class ReportServiceImpl(
    private val reportRepository: ReportRepository,
    private val userStatsRepository: UserStatsRepository
) : ReportService {

    @Transactional(readOnly = true)
    override fun listReports(
        search: String?, categoryId: UUID?, roomId: UUID?, buildingId: UUID?,
        status: ReportStatus?, includeDeleted: Boolean,
        currentUserId: UUID, isAdmin: Boolean, pageable: Pageable
    ): Page<ReportResponse> {
        val targetUserId = if (isAdmin) null else currentUserId
        val allowDeleted = isAdmin && includeDeleted

        val spec = ReportSpecification.withFilters(
            search = search,
            categoryId = categoryId,
            roomId = roomId,
            buildingId = buildingId,
            status = status,
            includeDeleted = allowDeleted,
            userId = targetUserId
        )
        return reportRepository.findAll(spec, pageable).map { it.toResponse() }
    }

    @Transactional
    override fun createReport(userId: UUID, request: CreateReportRequest): ReportResponse {
        val now = Instant.now()
        val report = Report(
            reporterId = userId,
            categoryId = request.categoryId,
            roomId = request.roomId,
            title = request.title,
            description = request.description ?: "",
            status = ReportStatus.PENDING,
            createdAt = now,
            updatedAt = now
        )
        val savedReport = reportRepository.save(report)

        val stats = userStatsRepository.findByUserId(userId)
        if (stats != null) {
            stats.incrementReportCount()
            stats.pendingReport++
            userStatsRepository.save(stats)
        }

        return savedReport.toResponse()
    }

    @Transactional(readOnly = true)
    override fun getReportById(id: UUID, currentUserId: UUID, isAdmin: Boolean): ReportResponse {
        val report = reportRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Report not found") }

        if (!isAdmin && report.reporterId != currentUserId) {
            throw AccessDeniedException("Access denied")
        }

        return report.toResponse()
    }

    @Transactional
    override fun updateReport(id: UUID, currentUserId: UUID, isAdmin: Boolean, request: UpdateReportRequest): ReportResponse {
        val report = reportRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Report not found") }

        if (!isAdmin && report.reporterId != currentUserId) {
            throw AccessDeniedException("Not authorized to update this report")
        }

        if (!isAdmin && report.status != ReportStatus.PENDING) {
            throw IllegalStateException("Only pending reports can be edited by the reporter")
        }

        request.title?.let { report.title = it }
        request.description?.let { report.description = it }
        request.categoryId?.let { report.categoryId = it }
        request.roomId?.let { report.roomId = it }
        report.updatedAt = Instant.now()

        return reportRepository.save(report).toResponse()
    }

    @Transactional
    override fun deleteReport(id: UUID, currentUserId: UUID, isAdmin: Boolean) {
        val report = reportRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Report not found") }

        if (!isAdmin && report.reporterId != currentUserId) {
            throw AccessDeniedException("Not authorized to delete this report")
        }

        report.softDelete()
        report.updatedAt = Instant.now()
        reportRepository.save(report)
    }

    @Transactional
    override fun updateReportStatus(id: UUID, adminId: UUID, request: UpdateReportStatusRequest): ReportResponse {
        val report = reportRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Report not found") }

        val oldStatus = report.status
        val newStatus = request.status

        val validTransitions = mapOf(
            ReportStatus.PENDING to listOf(ReportStatus.IN_PROGRESS, ReportStatus.REJECTED),
            ReportStatus.IN_PROGRESS to listOf(ReportStatus.COMPLETED, ReportStatus.REJECTED)
        )

        if (newStatus !in (validTransitions[oldStatus] ?: emptyList())) {
            throw IllegalArgumentException("Invalid status transition from $oldStatus to $newStatus")
        }

        report.status = newStatus
        report.updatedAt = Instant.now()

        val log = ReportLog(
            adminId = adminId,
            reportId = id,
            oldStatus = oldStatus,
            newStatus = newStatus,
            createdAt = Instant.now(),
            notes = request.notes
        )
        report.addLog(log)

        val stats = userStatsRepository.findByUserId(report.reporterId)
        if (stats != null) {
            stats.updateOnStatusChange(oldStatus, newStatus)
            userStatsRepository.save(stats)
        }

        return reportRepository.save(report).toResponse()
    }

    @Transactional(readOnly = true)
    override fun getAdminDashboardStats(): Map<String, Int> {
        return mapOf(
            "pendingCount" to reportRepository.countByStatus(ReportStatus.PENDING),
            "inProgressCount" to reportRepository.countByStatus(ReportStatus.IN_PROGRESS),
            "completedCount" to reportRepository.countByStatus(ReportStatus.COMPLETED)
        )
    }

    @Transactional(readOnly = true)
    override fun getPendingReports(pageable: Pageable): Page<ReportResponse> {
        val spec = ReportSpecification.withFilters(
            search = null, categoryId = null, roomId = null, buildingId = null,
            status = ReportStatus.PENDING, includeDeleted = false, userId = null
        )
        return reportRepository.findAll(spec, pageable).map { it.toResponse() }
    }

    private fun Report.toResponse(): ReportResponse {
        return ReportResponse(
            id = this.id!!,
            version = this.version,
            reporter = UserResponse(
                id = this.user!!.id!!,
                name = this.user!!.name,
                email = this.user!!.email,
                role = this.user!!.role.name,
                createdAt = this.user!!.createdAt.atOffset(ZoneOffset.UTC),
                updatedAt = this.user!!.updatedAt.atOffset(ZoneOffset.UTC)
            ),
            category = this.category!!,
            room = this.room!!,
            categoryId = this.categoryId,
            roomId = this.roomId,
            title = this.title,
            description = this.description,
            status = this.status,
            upvoteCount = this.upvotes.size,
            images = this.images.map { img ->
                ReportImageResponse(img.id!!, img.reportId, img.imageUrl, img.uploadedAt.atOffset(ZoneOffset.UTC))
            },
            createdAt = this.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = this.updatedAt.atOffset(ZoneOffset.UTC),
            deletedAt = this.deletedAt?.atOffset(ZoneOffset.UTC)
        )
    }
}