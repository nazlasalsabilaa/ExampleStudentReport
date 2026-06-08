package com.example.studentreport.report.service

import com.example.studentreport.entity.ReportImage
import com.example.studentreport.report.dto.ReportImageResponse
import com.example.studentreport.repository.ReportImageRepository
import com.example.studentreport.repository.ReportRepository
import com.example.studentreport.storage.service.StorageService
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

@Service
class ReportImageServiceImpl(
    private val reportRepository: ReportRepository,
    private val reportImageRepository: ReportImageRepository,
    private val storageService: StorageService
) : ReportImageService {

    private val allowedContentTypes = listOf("image/jpeg", "image/png", "image/webp")
    private val maxFileSize = 5 * 1024 * 1024
    private val maxImagesPerReport = 3

    @Transactional(readOnly = true)
    override fun getImagesByReportId(
        reportId: UUID,
        currentUserId: UUID,
        isAdmin: Boolean
    ): List<ReportImageResponse> {
        val report = reportRepository.findById(reportId)
            .orElseThrow { IllegalArgumentException("Report not found") }

        if (!isAdmin && report.reporterId != currentUserId) {
            throw AccessDeniedException("Access denied")
        }

        return reportImageRepository.findByReportId(reportId).map { it.toResponse() }
    }

    @Transactional
    override fun uploadImages(
        reportId: UUID,
        currentUserId: UUID,
        isAdmin: Boolean,
        files: List<MultipartFile>
    ): List<ReportImageResponse> {
        val report = reportRepository.findById(reportId)
            .orElseThrow { IllegalArgumentException("Report not found") }

        if (!isAdmin && report.reporterId != currentUserId) {
            throw AccessDeniedException("Not authorized to upload images for this report")
        }

        val currentImageCount = reportImageRepository.findByReportId(reportId).size
        if (currentImageCount + files.size > maxImagesPerReport) {
            throw IllegalArgumentException("Maximum $maxImagesPerReport images per report. Currently there are $currentImageCount images.")
        }

        val savedImages = files.map { file ->
            if (file.size > maxFileSize) throw IllegalArgumentException("File size exceeds 5MB limit")
            if (!allowedContentTypes.contains(file.contentType)) throw IllegalArgumentException("Invalid file type: ${file.contentType}")

            val imageUrl = storageService.store(file)

            val reportImage = ReportImage(
                reportId = reportId,
                imageUrl = imageUrl,
                uploadedAt = Instant.now(),
                report = report
            )
            reportImageRepository.save(reportImage)
        }

        report.updatedAt = Instant.now()
        reportRepository.save(report)

        return savedImages.map { it.toResponse() }
    }

    @Transactional
    override fun deleteImage(
        reportId: UUID,
        imageId: UUID,
        currentUserId: UUID,
        isAdmin: Boolean
    ) {
        val report = reportRepository.findById(reportId)
            .orElseThrow { IllegalArgumentException("Report not found") }

        if (!isAdmin && report.reporterId != currentUserId) {
            throw AccessDeniedException("Not authorized to delete images from this report")
        }

        val image = reportImageRepository.findById(imageId)
            .orElseThrow { IllegalArgumentException("Image not found") }

        if (image.reportId != reportId) {
            throw IllegalArgumentException("Image does not belong to the specified report")
        }

        storageService.delete(image.imageUrl)
        reportImageRepository.delete(image)

        report.updatedAt = Instant.now()
        reportRepository.save(report)
    }

    private fun ReportImage.toResponse(): ReportImageResponse {
        return ReportImageResponse(
            id = this.id!!,
            reportId = this.reportId,
            imageUrl = this.imageUrl,
            uploadedAt = this.uploadedAt.atOffset(ZoneOffset.UTC)
        )
    }
}