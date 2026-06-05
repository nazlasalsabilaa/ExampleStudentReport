package com.example.studentreport.report.service

import com.example.studentreport.entity.ReportStatus
import com.example.studentreport.report.dto.CreateReportRequest
import com.example.studentreport.report.dto.ReportResponse
import com.example.studentreport.report.dto.UpdateReportRequest
import com.example.studentreport.report.dto.UpdateReportStatusRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ReportService {
    fun listReports(
        search: String?, categoryId: UUID?, roomId: UUID?, buildingId: UUID?,
        status: ReportStatus?, includeDeleted: Boolean,
        currentUserId: UUID, isAdmin: Boolean, pageable: Pageable
    ): Page<ReportResponse>
    fun createReport(userId: UUID, request: CreateReportRequest): ReportResponse
    fun getReportById(id: UUID, currentUserId: UUID, isAdmin: Boolean): ReportResponse
    fun updateReport(id: UUID, currentUserId: UUID, isAdmin: Boolean, request: UpdateReportRequest): ReportResponse
    fun deleteReport(id: UUID, currentUserId: UUID, isAdmin: Boolean)
    fun updateReportStatus(id: UUID, adminId: UUID, request: UpdateReportStatusRequest): ReportResponse
}