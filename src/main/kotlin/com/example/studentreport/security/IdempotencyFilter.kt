package com.example.studentreport.security

import com.example.studentreport.auth.dto.UserResponse
import com.example.studentreport.entity.IdempotencyKey
import com.example.studentreport.repository.IdempotencyKeyRepository
import com.example.studentreport.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.dao.DataIntegrityViolationException
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
        val userId = if (auth != null && auth.isAuthenticated && auth.name != "anonymousUser") {
            when (val principal = auth.principal) {
                is UserResponse -> principal.id
                is UUID -> principal
                else -> null
            }
        } else {
            null
        }

        val requestPath = request.requestURI

        var isNewRequest = false
        try {
            val placeholder = IdempotencyKey(
                key = idempotencyKeyHeader,
                requestPath = requestPath,
                responseStatus = 0,
                responseBody = "",
                createdAt = Instant.now(),
                expiresAt = Instant.now().plus(24, ChronoUnit.HOURS),
                user = userId?.let { userRepository.getReferenceById(it) }
            )
            idempotencyKeyRepository.saveAndFlush(placeholder)
            isNewRequest = true
        } catch (e: DataIntegrityViolationException) {

        }

        if (!isNewRequest) {
            val existingKey = idempotencyKeyRepository.findByKey(idempotencyKeyHeader)

            if (existingKey != null) {
                if (existingKey.responseStatus == 0.toShort()) {
                    response.status = 409
                    response.contentType = "application/json"
                    response.writer.write("""{"success":false,"message":"Concurrent request processing"}""")
                    return
                }

                if (!existingKey.isExpired() && existingKey.requestPath == requestPath) {
                    response.status = existingKey.responseStatus.toInt()
                    response.contentType = "application/json"
                    response.characterEncoding = "UTF-8"
                    response.writer.write(existingKey.responseBody)
                    return
                }
            }
        }

        val responseWrapper = ContentCachingResponseWrapper(response)
        filterChain.doFilter(request, responseWrapper)

        if (responseWrapper.status < 500) {
            val responseBodyString = String(responseWrapper.contentAsByteArray, Charsets.UTF_8)
            val existingKey = idempotencyKeyRepository.findByKey(idempotencyKeyHeader)

            if (existingKey != null) {
                existingKey.responseStatus = responseWrapper.status.toShort()
                existingKey.responseBody = responseBodyString
                idempotencyKeyRepository.save(existingKey)
            }
        }

        responseWrapper.copyBodyToResponse()
    }
}