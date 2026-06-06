package com.example.studentreport.repository

import com.example.studentreport.entity.IdempotencyKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IdempotencyKeyRepository : JpaRepository<IdempotencyKey, Int> {
    fun findByKey(key: String): IdempotencyKey?
}