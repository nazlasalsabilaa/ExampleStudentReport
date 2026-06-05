package com.example.studentreport.web.controller

import com.example.studentreport.web.service.DashboardService
import com.example.studentreport.web.service.WebAuthHelper
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class WebDashboardController(
    private val dashboardService: DashboardService,
    private val webAuthHelper: WebAuthHelper
) {
    @GetMapping("/dashboard")
    fun dashboard(auth: Authentication?, model: Model): String {
        if (webAuthHelper.isAdmin(auth)) {
            return "redirect:/dashboard/admin"
        }

        model.addAttribute("isAdmin", false)
        model.addAttribute("userStats", dashboardService.getUserDashboardStats())
        model.addAttribute("recentReports", dashboardService.getRecentUserReports(3))

        return "dashboard/dashboard_mahasiswa"
    }

    @GetMapping("/dashboard/admin")
    fun dashboardAdmin(auth: Authentication?, model: Model): String {
        val stats = dashboardService.getAdminDashboardStats()

        model.addAttribute("isAdmin", webAuthHelper.isAdmin(auth))
        model.addAttribute("pendingCount", stats["pendingCount"])
        model.addAttribute("inProgressCount", stats["inProgressCount"])
        model.addAttribute("completedCount", stats["completedCount"])
        model.addAttribute("pendingReports", dashboardService.getPendingReports())

        return "dashboard/dashboard_admin"
    }
}