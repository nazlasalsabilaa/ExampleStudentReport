package com.example.studentreport.repository

import com.example.studentreport.entity.StudentData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface StudentDataRepository: JpaRepository<StudentData, UUID> {
    fun findByUserId(userId: UUID): StudentData?
}