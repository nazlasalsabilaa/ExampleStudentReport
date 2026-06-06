package com.example.studentreport.upvote.dto

import java.util.UUID

data class UpvoteSummaryResponse(
    val reportId: UUID,
    val totalUpvotes: Int,
    val upvotedByMe: Boolean
)