package com.example.studentreport.web.service

import com.example.studentreport.entity.Report
import java.util.UUID

interface DashboardService {
    fun getUserDashboardStats(userId: UUID): Map<String, Int>
    fun getRecentUserReports(userId: UUID, limit: Int): List<Report>
    fun getAdminDashboardStats(): Map<String, Int>
    fun getPendingReports(): List<Report>
}