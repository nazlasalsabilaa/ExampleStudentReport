package com.example.studentreport.upvote.controller

import com.example.studentreport.auth.dto.ApiResponse
import com.example.studentreport.auth.dto.UserResponse
import com.example.studentreport.security.getUserId
import com.example.studentreport.upvote.dto.UpvoteResponse
import com.example.studentreport.upvote.dto.UpvoteSummaryResponse
import com.example.studentreport.upvote.service.UpvoteService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/reports/{reportId}/upvotes")
class UpvoteController(
    private val upvoteService: UpvoteService
) {

    @GetMapping
    fun getUpvoteSummary(
        @PathVariable reportId: UUID,
        authentication: Authentication
    ): ApiResponse<UpvoteSummaryResponse> {
        val summary = upvoteService.getUpvoteSummary(reportId, authentication.getUserId())
        return ApiResponse(success = true, message = "Upvote summary retrieved successfully", data = summary)
    }

    @PostMapping
    fun upvoteReport(
        @PathVariable reportId: UUID,
        @RequestHeader("Idempotency-Key", required = false) idempotencyKey: String?,
        authentication: Authentication
    ): ApiResponse<UpvoteResponse> {
        val upvote = upvoteService.upvoteReport(reportId, authentication.getUserId())
        return ApiResponse(success = true, message = "Report upvoted successfully", data = upvote)
    }

    @DeleteMapping
    fun removeUpvote(
        @PathVariable reportId: UUID,
        authentication: Authentication
    ): ApiResponse<Unit> {
        upvoteService.removeUpvote(reportId, authentication.getUserId())
        return ApiResponse(success = true, message = "Upvote removed successfully")
    }
}