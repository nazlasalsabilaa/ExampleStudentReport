package com.example.studentreport.web.controller

import com.example.studentreport.auth.dto.UserResponse
import com.example.studentreport.report.service.ReportService
import com.example.studentreport.user.service.UserService
import com.example.studentreport.web.service.WebAuthHelper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Pageable
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.util.UUID

@Controller
class WebDashboardController(
    private val userService: UserService,
    private val reportService: ReportService,
    private val webAuthHelper: WebAuthHelper
) {

    private fun Authentication?.getUserIdOrThrow(): UUID {
        if (this == null || !this.isAuthenticated) {
            throw IllegalStateException("User must be authenticated")
        }
        return when (val p = this.principal) {
            is UserResponse -> p.id
            is UUID -> p
            else -> throw IllegalStateException("Unexpected principal type")
        }
    }

    @GetMapping("/dashboard")
    fun dashboard(auth: Authentication?, model: Model): String {
        if (webAuthHelper.isAdmin(auth)) {
            return "redirect:/dashboard/admin"
        }

        val userId = auth.getUserIdOrThrow()
        val stats = userService.getUserStats(userId)

        val dashboardStats = mapOf(
            "reportCount" to stats.reportCount,
            "pendingReport" to (stats.pendingReport + stats.inProgressReport),
            "completedReport" to stats.completedReport
        )

        val recentReportsRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"))
        val recentReports = reportService.listReports(
            search = null, categoryId = null, roomId = null, buildingId = null,
            status = null, includeDeleted = false, currentUserId = userId,
            isAdmin = false, pageable = recentReportsRequest
        ).content

        model.addAttribute("isAdmin", false)
        model.addAttribute("userStats", dashboardStats)
        model.addAttribute("recentReports", recentReports)

        return "dashboard/dashboard_mahasiswa"
    }

    @GetMapping("/dashboard/admin")
    fun dashboardAdmin(auth: Authentication?, model: Model): String {
        val stats = reportService.getAdminDashboardStats()
        val pendingReports = reportService.getPendingReports(Pageable.unpaged()).content

        model.addAttribute("isAdmin", webAuthHelper.isAdmin(auth))
        model.addAttribute("pendingCount", stats["pendingCount"])
        model.addAttribute("inProgressCount", stats["inProgressCount"])
        model.addAttribute("completedCount", stats["completedCount"])
        model.addAttribute("pendingReports", pendingReports)

        return "dashboard/dashboard_admin"
    }
}