package com.example.studentreport.auth.service

import com.example.studentreport.auth.dto.AuthResponse
import com.example.studentreport.auth.dto.LoginRequest
import com.example.studentreport.auth.dto.RegisterRequest
import com.example.studentreport.auth.dto.UserResponse
import com.example.studentreport.entity.Session
import com.example.studentreport.entity.StudentData
import com.example.studentreport.entity.User
import com.example.studentreport.entity.UserRole
import com.example.studentreport.entity.UserStats
import com.example.studentreport.repository.SessionRepository
import com.example.studentreport.repository.StudentDataRepository
import com.example.studentreport.repository.UserRepository
import com.example.studentreport.repository.UserStatsRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
@ConditionalOnProperty(name = ["app.security.mock-auth"], havingValue = "false")
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val studentDataRepository: StudentDataRepository,
    private val userStatsRepository: UserStatsRepository,
    private val passwordEncoder: PasswordEncoder
) : AuthService {

    @Transactional
    override fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("Invalid email or password")

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw IllegalArgumentException("Invalid email or password")
        }

        val token = UUID.randomUUID().toString()
        val now = Instant.now()
        val expiresAt = now.plus(1, ChronoUnit.HOURS)

        val session = Session(
            userId = user.id!!,
            token = token,
            expiresAt = expiresAt,
            user = user
        )

        user.addSession(session)
        userRepository.save(user)

        return AuthResponse(
            token = token,
            expiresAt = expiresAt.atOffset(ZoneOffset.UTC),
            user = user.toResponse()
        )
    }

    @Transactional
    override fun register(request: RegisterRequest): UserResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email is already registered")
        }

        val now = Instant.now()
        val user = User(
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password)!!,
            createdAt = now,
            updatedAt = now,
            name = request.name,
            role = UserRole.USER
        )

        val savedUser = userRepository.save(user)

        val studentData = StudentData(
            userId = savedUser.id!!,
            nim = request.nim,
            faculty = request.faculty,
            major = request.major,
            year = request.year,
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

        return savedUser.toResponse()
    }

    @Transactional
    override fun logout(token: String) {
        sessionRepository.findByToken(token)?.let { session ->
            session.user?.sessions?.remove(session)
            session.user = null
            sessionRepository.delete(session)
        }
    }

    @Transactional(readOnly = true)
    override fun validateSession(token: String): UserResponse? {
        val session = sessionRepository.findByToken(token) ?: return null

        if (session.isExpired()) {
            sessionRepository.delete(session)
            return null
        }

        return session.user?.toResponse()
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
}