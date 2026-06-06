package com.example.studentreport.report.controller

import com.example.studentreport.auth.dto.ApiResponse
import com.example.studentreport.report.dto.ReportImageResponse
import com.example.studentreport.report.service.ReportImageService
import com.example.studentreport.web.service.WebAuthHelper
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/v1/reports/{reportId}/images")
class RestReportImageController(
    private val reportImageService: ReportImageService,
    private val webAuthHelper: WebAuthHelper
) {

    private fun Authentication.getUserId(): UUID = this.principal as UUID

    @GetMapping
    fun listImages(
        @PathVariable reportId: UUID,
        authentication: Authentication
    ): ApiResponse<List<ReportImageResponse>> {
        val images = reportImageService.getImagesByReportId(
            reportId,
            authentication.getUserId(),
            webAuthHelper.isAdmin(authentication)
        )
        return ApiResponse(success = true, message = "Images retrieved successfully", data = images)
    }

    @PostMapping
    fun uploadImages(
        @PathVariable reportId: UUID,
        @RequestParam("images") files: List<MultipartFile>,
        authentication: Authentication
    ): ApiResponse<List<ReportImageResponse>> {
        val uploadedImages = reportImageService.uploadImages(
            reportId,
            authentication.getUserId(),
            webAuthHelper.isAdmin(authentication),
            files
        )
        return ApiResponse(success = true, message = "Images uploaded successfully", data = uploadedImages)
    }

    @DeleteMapping("/{imageId}")
    fun deleteImage(
        @PathVariable reportId: UUID,
        @PathVariable imageId: UUID,
        authentication: Authentication
    ): ApiResponse<Unit> {
        reportImageService.deleteImage(
            reportId,
            imageId,
            authentication.getUserId(),
            webAuthHelper.isAdmin(authentication)
        )
        return ApiResponse(success = true, message = "Image deleted successfully")
    }

}