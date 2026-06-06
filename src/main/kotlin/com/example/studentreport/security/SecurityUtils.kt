package com.example.studentreport.security

import com.example.studentreport.auth.dto.UserResponse
import org.springframework.security.core.Authentication
import java.util.UUID

fun Authentication.getUserId(): UUID {
    return when (val principal = this.principal) {
        is UserResponse -> principal.id
        is UUID -> principal
        else -> throw IllegalStateException("Unexpected security principal type: ${principal?.javaClass?.name}")
    }
}