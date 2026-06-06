package com.example.studentreport.upvote.service

import com.example.studentreport.entity.Upvote
import com.example.studentreport.repository.ReportRepository
import com.example.studentreport.repository.UpvoteRepository
import com.example.studentreport.repository.UserRepository
import com.example.studentreport.upvote.dto.UpvoteResponse
import com.example.studentreport.upvote.dto.UpvoteSummaryResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

@Service
class UpvoteServiceImpl(
    private val upvoteRepository: UpvoteRepository,
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository
) : UpvoteService {

    @Transactional(readOnly = true)
    override fun getUpvoteSummary(reportId: UUID, currentUserId: UUID): UpvoteSummaryResponse {
        if (!reportRepository.existsById(reportId)) {
            throw IllegalArgumentException("Report not found")
        }

        val totalUpvotes = upvoteRepository.countByReportId(reportId)
        val upvotedByMe = upvoteRepository.existsByUserIdAndReportId(currentUserId, reportId)

        return UpvoteSummaryResponse(
            reportId = reportId,
            totalUpvotes = totalUpvotes,
            upvotedByMe = upvotedByMe
        )
    }

    @Transactional
    override fun upvoteReport(reportId: UUID, currentUserId: UUID): UpvoteResponse {
        val report = reportRepository.findById(reportId)
            .orElseThrow { IllegalArgumentException("Report not found") }

        if (upvoteRepository.existsByUserIdAndReportId(currentUserId, reportId)) {
            throw IllegalStateException("You have already upvoted this report")
        }

        val user = userRepository.getReferenceById(currentUserId)

        val upvote = Upvote(
            userId = currentUserId,
            reportId = reportId,
            createdAt = Instant.now(),
            report = report,
            user = user
        )

        val savedUpvote = upvoteRepository.save(upvote)

        return UpvoteResponse(
            id = savedUpvote.id!!,
            userId = savedUpvote.userId,
            reportId = savedUpvote.reportId,
            createdAt = savedUpvote.createdAt.atOffset(ZoneOffset.UTC)
        )
    }

    @Transactional
    override fun removeUpvote(reportId: UUID, currentUserId: UUID) {
        if (!reportRepository.existsById(reportId)) {
            throw IllegalArgumentException("Report not found")
        }

        val upvote = upvoteRepository.findByUserIdAndReportId(currentUserId, reportId)
            ?: throw IllegalArgumentException("Upvote not found")

        upvoteRepository.delete(upvote)
    }
}