package com.example.studentreport.repository

import com.example.studentreport.entity.Upvote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UpvoteRepository : JpaRepository<Upvote, UUID> {
    fun existsByUserIdAndReportId(userId: UUID, reportId: UUID): Boolean
    fun findByUserIdAndReportId(userId: UUID, reportId: UUID): Upvote?
    fun countByReportId(reportId: UUID): Int
}