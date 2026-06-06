package com.example.studentreport.user.service

import com.example.studentreport.auth.dto.UserResponse
import com.example.studentreport.entity.StudentData
import com.example.studentreport.entity.User
import com.example.studentreport.entity.UserRole
import com.example.studentreport.repository.StudentDataRepository
import com.example.studentreport.repository.UserRepository
import com.example.studentreport.repository.UserStatsRepository
import com.example.studentreport.user.dto.ChangePasswordRequest
import com.example.studentreport.user.dto.StudentDataResponse
import com.example.studentreport.user.dto.UpdateStudentDataRequest
import com.example.studentreport.user.dto.UpdateUserRequest
import com.example.studentreport.user.dto.UserStatsResponse
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userStatsRepository: UserStatsRepository,
    private val passwordEncoder: PasswordEncoder,
    private val studentDataRepository: StudentDataRepository
): UserService {

    @Transactional(readOnly = true)
    override fun getUserProfile(userId: UUID): UserResponse {
        val user = findUserOrThrow(userId)
        return user.toResponse()
    }

    @Transactional(readOnly = true)
    override fun getAllUsers(
        search: String?,
        role: UserRole?,
        pageable: Pageable
    ): Page<UserResponse> {
        val spec = Specification<User> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            if (!search.isNullOrBlank()) {
                val searchPattern = "%${search.lowercase()}%"
                val namePredicate = cb.like(cb.lower(root.get("name")), searchPattern)
                val emailPredicate = cb.like(cb.lower(root.get("email")), searchPattern)
                predicates.add(cb.or(namePredicate, emailPredicate))
            }

            if (role != null) {
                predicates.add(cb.equal(root.get<UserRole>("role"), role))
            }

            cb.and(*predicates.toTypedArray())
        }
        return userRepository.findAll(spec, pageable).map { it.toResponse() }
    }

    @Transactional
    override fun updateUser(
        userId: UUID,
        request: UpdateUserRequest
    ): UserResponse {
        val user = findUserOrThrow(userId)

        request.email?.let { newEmail ->
            if (newEmail != user.email && userRepository.existsByEmail(newEmail)) {
                throw IllegalArgumentException("Email is already in use")
            }
            user.email = newEmail
        }

        request.name?.let { user.name = it }
        user.updatedAt = Instant.now()

        return userRepository.save(user).toResponse()
    }

    @Transactional
    override fun deleteUser(userId: UUID) {
        val user = findUserOrThrow(userId)
        userRepository.delete(user)
    }

    @Transactional(readOnly = true)
    override fun getUserStats(userId: UUID): UserStatsResponse {
        val stats = userStatsRepository.findAll().firstOrNull { it.userId == userId }
            ?: throw IllegalArgumentException("User stats not found")

        return UserStatsResponse(
            id = stats.id!!,
            userId = stats.userId,
            reportCount = stats.reportCount,
            pendingReport = stats.pendingReport,
            completedReport = stats.completedReport,
            rejectedReport = stats.rejectedReport,
            updatedAt = stats.updatedAt
        )
    }

    @Transactional
    override fun changePassword(
        userId: UUID,
        request: ChangePasswordRequest
    ) {
        val user = findUserOrThrow(userId)
        if (!passwordEncoder.matches(request.oldPassword, user.passwordHash)) {
            throw IllegalArgumentException("Invalid old password")
        }

        user.passwordHash = passwordEncoder.encode(request.newPassword)!!
        user.updatedAt = Instant.now()

        userRepository.save(user)
    }

    private fun findUserOrThrow(userId: UUID): User {
        return userRepository.findById(userId).orElseThrow{
            IllegalArgumentException("User Not Found")
        }
    }

    @Transactional(readOnly = true)
    override fun getStudentData(userId: UUID): StudentDataResponse {
        val studentData = studentDataRepository.findByUserId(userId)
            ?: throw IllegalArgumentException("Student data not found")

        return studentData.toResponse()
    }

    @Transactional
    override fun updateStudentData(userId: UUID, request: UpdateStudentDataRequest): StudentDataResponse {
        val studentData = studentDataRepository.findByUserId(userId)
            ?: throw IllegalArgumentException("Student data not found")

        request.nim?.let { studentData.nim = it }
        request.faculty?.let { studentData.faculty = it }
        request.major?.let { studentData.major = it }
        request.year?.let { studentData.year = it }

        return studentDataRepository.save(studentData).toResponse()
    }


    private fun User.toResponse(): UserResponse {
        return UserResponse(
            id = this.id!!,
            name = this.name,
            email = this.email,
            role = this.role.name,
            createdAt = this.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = this.updatedAt.atOffset(ZoneOffset.UTC)
        )
    }

    private fun StudentData.toResponse(): StudentDataResponse {
        return StudentDataResponse(
            id = this.id!!,
            userId = this.userId,
            nim = this.nim,
            faculty = this.faculty,
            major = this.major,
            year = this.year
        )
    }
}