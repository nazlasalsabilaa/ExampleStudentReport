package com.example.studentreport.auth.service

import com.example.studentreport.auth.dto.AuthResponse
import com.example.studentreport.auth.dto.LoginRequest
import com.example.studentreport.auth.dto.RegisterRequest
import com.example.studentreport.auth.dto.UserResponse
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

// This is only for Mocking not real service
@Service
class MockAuthServiceImpl: AuthService {
    private data class MockUserEntity(
        val profile: UserResponse,
        val passwordHash: String
    )

    private val mockDatabase = ConcurrentHashMap<String, MockUserEntity>()

    override fun login(request: LoginRequest): AuthResponse {
        val entity = mockDatabase[request.email] ?: throw RuntimeException("User not found")

        if (entity.passwordHash != request.password) {
            throw RuntimeException("Invalid credentials")
        }

        return AuthResponse(
            token = "mock.jwt.token.${UUID.randomUUID()}",
            expiresAt = OffsetDateTime.now().plusHours(1),
            user = entity.profile
        )
    }

    override fun register(request: RegisterRequest): UserResponse {
        if (mockDatabase.containsKey(request.email)) {
            throw RuntimeException("User already registered")
        }

        val newUser = UserResponse(
            id = UUID.randomUUID(),
            email = request.email,
            name = request.name,
            role = "User",
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now()
        )

        val newEntity = MockUserEntity(
            profile = newUser,
            passwordHash = request.password
        )

        mockDatabase[request.email] = newEntity
        return newUser
    }

}