package com.example.studentreport.upvote.dto

import java.time.OffsetDateTime
import java.util.UUID

data class UpvoteResponse(
    val id: UUID,
    val userId: UUID,
    val reportId: UUID,
    val createdAt: OffsetDateTime
)