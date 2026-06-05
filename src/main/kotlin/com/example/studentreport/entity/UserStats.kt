package com.example.studentreport.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "user_stats")
data class UserStats(
    @Id @GeneratedValue val id: UUID? = null,
    @Column(name = "user_id") val userId: UUID,
    var reportCount: Int,
    var pendingReport: Int,
    var inProgressReport: Int,
    var completedReport: Int,
    var rejectedReport: Int,
    @Column(name = "updated_at") var updatedAt: Instant,

    @OneToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    var user: User? = null
) {
    fun incrementReportCount() {
        reportCount++
        updatedAt = Instant.now()
    }

    fun updateOnStatusChange(oldStatus: ReportStatus, newStatus: ReportStatus) {
        when (oldStatus) {
            ReportStatus.PENDING -> pendingReport--
            ReportStatus.COMPLETED -> completedReport--
            ReportStatus.REJECTED -> rejectedReport--
            ReportStatus.IN_PROGRESS -> inProgressReport--
        }
        when (newStatus) {
            ReportStatus.PENDING -> pendingReport++
            ReportStatus.COMPLETED -> completedReport++
            ReportStatus.REJECTED -> rejectedReport++
            ReportStatus.IN_PROGRESS -> inProgressReport++
        }
        updatedAt = Instant.now()
    }
}