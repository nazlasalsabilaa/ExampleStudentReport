package com.example.studentreport.repository

import com.example.studentreport.entity.ReportImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ReportImageRepository : JpaRepository<ReportImage, UUID> {
    fun findByReportId(reportId: UUID): List<ReportImage>
}