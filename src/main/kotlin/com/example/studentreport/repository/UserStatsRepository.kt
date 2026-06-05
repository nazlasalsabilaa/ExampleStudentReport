package com.example.studentreport.repository

import com.example.studentreport.entity.UserStats
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserStatsRepository: JpaRepository<UserStats, UUID> {
    fun findByUserId(userId: UUID): UserStats?
}