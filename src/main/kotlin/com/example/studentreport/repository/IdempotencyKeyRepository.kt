package com.example.studentreport.repository

import com.example.studentreport.entity.IdempotencyKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface IdempotencyKeyRepository : JpaRepository<IdempotencyKey, Int> {
    fun findByUserIdAndKey(userId: UUID, key: String): IdempotencyKey?
}