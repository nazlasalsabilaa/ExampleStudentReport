package com.example.studentreport.security

import com.example.studentreport.auth.dto.UserResponse
import com.example.studentreport.entity.IdempotencyKey
import com.example.studentreport.repository.IdempotencyKeyRepository
import com.example.studentreport.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Component
class IdempotencyFilter(
    private val idempotencyKeyRepository: IdempotencyKeyRepository,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val method = request.method
        val idempotencyKeyHeader = request.getHeader("Idempotency-Key")

        if (method !in listOf("POST", "PUT", "PATCH") || idempotencyKeyHeader.isNullOrBlank()) {
            filterChain.doFilter(request, response)
            return
        }

        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || !auth.isAuthenticated) {
            filterChain.doFilter(request, response)
            return
        }

        val userId = when (val principal = auth.principal) {
            is UserResponse -> principal.id
            is UUID -> principal
            else -> {
                filterChain.doFilter(request, response)
                return
            }
        }

        val requestPath = request.requestURI
        val existingKey = idempotencyKeyRepository.findByUserIdAndKey(userId, idempotencyKeyHeader)

        if (existingKey != null) {
            if (!existingKey.isExpired() && existingKey.requestPath == requestPath) {
                response.status = existingKey.responseStatus.toInt()
                response.contentType = "application/json"
                response.writer.write(existingKey.responseBody)
                return
            }
        }

        val responseWrapper = ContentCachingResponseWrapper(response)

        filterChain.doFilter(request, responseWrapper)

        if (responseWrapper.status < 500) {
            val responseBodyString = String(responseWrapper.contentAsByteArray)

            val newKey = IdempotencyKey(
                key = idempotencyKeyHeader,
                userId = userId,
                requestPath = requestPath,
                responseStatus = responseWrapper.status.toShort(),
                responseBody = responseBodyString,
                createdAt = Instant.now(),
                expiresAt = Instant.now().plus(24, ChronoUnit.HOURS),
                user = userRepository.getReferenceById(userId)
            )
            idempotencyKeyRepository.save(newKey)
        }

        responseWrapper.copyBodyToResponse()
    }
}