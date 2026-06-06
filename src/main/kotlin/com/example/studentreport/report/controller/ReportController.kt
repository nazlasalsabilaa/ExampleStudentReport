package com.example.studentreport.report.controller

import com.example.studentreport.auth.dto.ApiResponse
import com.example.studentreport.entity.ReportStatus
import com.example.studentreport.report.dto.CreateReportRequest
import com.example.studentreport.report.dto.ReportResponse
import com.example.studentreport.report.dto.UpdateReportRequest
import com.example.studentreport.report.dto.UpdateReportStatusRequest
import com.example.studentreport.report.service.ReportService
import com.example.studentreport.security.getUserId
import com.example.studentreport.web.service.WebAuthHelper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/reports")
class ReportController(
    private val reportService: ReportService,
    private val webAuthHelper: WebAuthHelper
) {
    @GetMapping
    fun listReports(
        authentication: Authentication,
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) categoryId: UUID?,
        @RequestParam(required = false) roomId: UUID?,
        @RequestParam(required = false) buildingId: UUID?,
        @RequestParam(required = false) status: ReportStatus?,
        @RequestParam(required = false, defaultValue = "false") includeDeleted: Boolean,
        @PageableDefault(size = 20) pageable: Pageable
    ): ApiResponse<Page<ReportResponse>> {
        val reports = reportService.listReports(
            search, categoryId, roomId, buildingId, status, includeDeleted,
            authentication.getUserId(), webAuthHelper.isAdmin(authentication), pageable
        )
        return ApiResponse(success = true, message = "Reports retrieved successfully", data = reports)
    }

    @PostMapping
    fun createReport(
        authentication: Authentication,
        @RequestBody request: CreateReportRequest
    ): ApiResponse<ReportResponse> {
        val report = reportService.createReport(authentication.getUserId(), request)
        return ApiResponse(success = true, message = "Report created successfully", data = report)
    }

    @GetMapping("/{id}")
    fun getReportById(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ApiResponse<ReportResponse> {
        val report = reportService.getReportById(id, authentication.getUserId(), webAuthHelper.isAdmin(authentication))
        return ApiResponse(success = true, message = "Report retrieved successfully", data = report)
    }

    @PutMapping("/{id}")
    fun updateReport(
        @PathVariable id: UUID,
        authentication: Authentication,
        @RequestBody request: UpdateReportRequest
    ): ApiResponse<ReportResponse> {
        val report = reportService.updateReport(id, authentication.getUserId(), webAuthHelper.isAdmin(authentication), request)
        return ApiResponse(success = true, message = "Report updated successfully", data = report)
    }

    @DeleteMapping("/{id}")
    fun deleteReport(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ApiResponse<Unit> {
        reportService.deleteReport(id, authentication.getUserId(), webAuthHelper.isAdmin(authentication))
        return ApiResponse(success = true, message = "Report deleted successfully")
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateReportStatus(
        @PathVariable id: UUID,
        authentication: Authentication,
        @RequestBody request: UpdateReportStatusRequest
    ): ApiResponse<ReportResponse> {
        val report = reportService.updateReportStatus(id, authentication.getUserId(), request)
        return ApiResponse(success = true, message = "Report status updated successfully", data = report)
    }
}