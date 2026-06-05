package com.example.studentreport.seeder

import com.example.studentreport.entity.StudentData
import com.example.studentreport.entity.User
import com.example.studentreport.entity.UserRole
import com.example.studentreport.entity.UserStats
import com.example.studentreport.repository.StudentDataRepository
import com.example.studentreport.repository.UserRepository
import com.example.studentreport.repository.UserStatsRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class UserSeeder(
    private val userRepository: UserRepository,
    private val studentDataRepository: StudentDataRepository,
    private val userStatsRepository: UserStatsRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun seed() {
        seedAdmin()
        seedStudent()
    }

    private fun seedAdmin() {
        val adminEmail = "admin@ulm.ac.id"
        if (!userRepository.existsByEmail(adminEmail)) {
            val now = Instant.now()
            val admin = User(
                email = adminEmail,
                name = "System Administrator",
                passwordHash = passwordEncoder.encode("admin123")!!,
                role = UserRole.ADMIN,
                createdAt = now,
                updatedAt = now
            )
            userRepository.save(admin)
        }
    }

    private fun seedStudent() {
        val userEmail = "student@mhs.ulm.ac.id"
        if (!userRepository.existsByEmail(userEmail)) {
            val now = Instant.now()
            val user = User(
                email = userEmail,
                name = "Test Student",
                passwordHash = passwordEncoder.encode("student123")!!,
                role = UserRole.USER,
                createdAt = now,
                updatedAt = now
            )
            val savedUser = userRepository.save(user)

            val studentData = StudentData(
                userId = savedUser.id!!,
                nim = "2410173100000",
                faculty = "Fakultas Teknik",
                major = "Teknologi Informasi",
                year = 2024,
                user = savedUser
            )
            studentDataRepository.save(studentData)

            val userStats = UserStats(
                userId = savedUser.id!!,
                reportCount = 0,
                pendingReport = 0,
                completedReport = 0,
                inProgressReport = 0,
                rejectedReport = 0,
                updatedAt = now,
                user = savedUser
            )
            userStatsRepository.save(userStats)
        }
    }
}