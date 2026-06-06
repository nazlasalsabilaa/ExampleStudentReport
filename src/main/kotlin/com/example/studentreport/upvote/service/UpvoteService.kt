package com.example.studentreport.upvote.service

import com.example.studentreport.upvote.dto.UpvoteResponse
import com.example.studentreport.upvote.dto.UpvoteSummaryResponse
import java.util.UUID

interface UpvoteService {
    fun getUpvoteSummary(reportId: UUID, currentUserId: UUID): UpvoteSummaryResponse
    fun upvoteReport(reportId: UUID, currentUserId: UUID): UpvoteResponse
    fun removeUpvote(reportId: UUID, currentUserId: UUID)
}