package com.example.studentreport.web.service.impl

import com.example.studentreport.entity.Report
import com.example.studentreport.entity.ReportStatus
import com.example.studentreport.repository.ReportRepository
import com.example.studentreport.repository.UserStatsRepository
import com.example.studentreport.web.service.DashboardService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class DashboardServiceImpl(
    private val reportRepository: ReportRepository,
    private val userStatsRepository: UserStatsRepository
) : DashboardService {

    override fun getUserDashboardStats(userId: UUID): Map<String, Int> {
        val stats = userStatsRepository.findByUserId(userId)
            ?: throw IllegalArgumentException("User stats not found")

        return mapOf(
            "reportCount" to stats.reportCount,
            "pendingReport" to (stats.pendingReport + stats.inProgressReport),
            "completedReport" to stats.completedReport
        )
    }

    override fun getRecentUserReports(userId: UUID, limit: Int): List<Report> {
        return reportRepository.findTop3ByReporterIdOrderByCreatedAtDesc(userId)
    }

    override fun getAdminDashboardStats(): Map<String, Int> {
        return mapOf(
            "pendingCount" to reportRepository.countByStatus(ReportStatus.PENDING),
            "inProgressCount" to reportRepository.countByStatus(ReportStatus.IN_PROGRESS),
            "completedCount" to reportRepository.countByStatus(ReportStatus.COMPLETED)
        )
    }

    override fun getPendingReports(): List<Report> {
        return reportRepository.findByStatus(ReportStatus.PENDING)
    }
}