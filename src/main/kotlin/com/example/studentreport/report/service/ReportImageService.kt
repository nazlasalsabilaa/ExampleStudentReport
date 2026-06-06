package com.example.studentreport.report.service

import com.example.studentreport.report.dto.ReportImageResponse
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

interface ReportImageService {
    fun getImagesByReportId(reportId: UUID, currentUserId: UUID, isAdmin: Boolean): List<ReportImageResponse>
    fun uploadImages(reportId: UUID, currentUserId: UUID, isAdmin: Boolean, files: List<MultipartFile>): List<ReportImageResponse>
    fun deleteImage(reportId: UUID, imageId: UUID, currentUserId: UUID, isAdmin: Boolean)
}