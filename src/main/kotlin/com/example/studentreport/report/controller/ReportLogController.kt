package com.example.studentreport.report.controller

import com.example.studentreport.auth.dto.ApiResponse
import com.example.studentreport.auth.dto.UserResponse
import com.example.studentreport.entity.ReportStatus
import com.example.studentreport.report.dto.ReportLogResponse
import com.example.studentreport.report.service.ReportLogService
import com.example.studentreport.web.service.WebAuthHelper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class ReportLogController(
    private val reportLogService: ReportLogService,
    private val webAuthHelper: WebAuthHelper
) {

    private fun Authentication.getUserId(): UUID {
        return when (val principal = this.principal) {
            is UserResponse -> principal.id
            is UUID -> principal
            else -> throw IllegalStateException("Unexpected principal type")
        }
    }

    @GetMapping("/reports/{reportId}/logs")
    fun getLogsByReportId(
        @PathVariable reportId: UUID,
        @PageableDefault(size = 20) pageable: Pageable,
        authentication: Authentication
    ): ApiResponse<Page<ReportLogResponse>> {
        val isAdmin = webAuthHelper.isAdmin(authentication)
        val logs = reportLogService.getLogsByReportId(reportId, authentication.getUserId(), isAdmin, pageable)
        return ApiResponse(success = true, message = "Report logs retrieved successfully", data = logs)
    }

    @GetMapping("/report-logs")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllReportLogs(
        @RequestParam(required = false) adminId: UUID?,
        @RequestParam(required = false) reportId: UUID?,
        @RequestParam(required = false) newStatus: ReportStatus?,
        @PageableDefault(size = 20) pageable: Pageable
    ): ApiResponse<Page<ReportLogResponse>> {
        val logs = reportLogService.getAllReportLogs(adminId, reportId, newStatus, pageable)
        return ApiResponse(success = true, message = "All report logs retrieved successfully", data = logs)
    }

    @GetMapping("/report-logs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getReportLogById(
        @PathVariable id: UUID
    ): ApiResponse<ReportLogResponse> {
        val log = reportLogService.getReportLogById(id)
        return ApiResponse(success = true, message = "Report log retrieved successfully", data = log)
    }
}