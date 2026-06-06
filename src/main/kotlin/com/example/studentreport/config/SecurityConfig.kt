package com.example.studentreport.config

import com.example.studentreport.security.IdempotencyFilter
import com.example.studentreport.security.TokenAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    val tokenAuthFilter: TokenAuthenticationFilter,
    val idempotencyFilter: IdempotencyFilter
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf{it.disable()}
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/api/v1/auth/**").permitAll()
                auth.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                auth.requestMatchers("/", "/login", "/register", "/error").permitAll()
                auth.anyRequest().authenticated()
            }
            .addFilterBefore(tokenAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterAfter(idempotencyFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}