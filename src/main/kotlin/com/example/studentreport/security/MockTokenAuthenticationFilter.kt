package com.example.studentreport.security

import com.example.studentreport.auth.service.AuthService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["app.security.mock-auth"], havingValue = "true", matchIfMissing = true)
class MockTokenAuthenticationFilter(
    private val authService: AuthService,
) : TokenAuthenticationFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request, response)
            return
        }

        var token = request.getHeader("Authorization")?.removePrefix("Bearer ")

        if (token == null) {
            token = request.cookies?.firstOrNull { it.name == "session_token" }?.value
        }

        if (token != null) {
            try {
                val user = authService.validateSession(token)
                if (user != null) {
                    val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.uppercase()}"))
                    val authentication = UsernamePasswordAuthenticationToken(user.id, null, authorities)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            } catch (e: Exception) {
            }
        }
        filterChain.doFilter(request, response)
    }
}