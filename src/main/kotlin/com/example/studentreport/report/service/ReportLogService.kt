package com.example.studentreport.report.service

import com.example.studentreport.entity.ReportStatus
import com.example.studentreport.report.dto.ReportLogResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ReportLogService {
    fun getLogsByReportId(reportId: UUID, pageable: Pageable): Page<ReportLogResponse>
    fun getAllReportLogs(adminId: UUID?, reportId: UUID?, newStatus: ReportStatus?, pageable: Pageable): Page<ReportLogResponse>
    fun getReportLogById(id: UUID): ReportLogResponse
}