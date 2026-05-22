package com.example.studentreport.auth.service

import com.example.studentreport.auth.dto.AuthResponse
import com.example.studentreport.auth.dto.LoginRequest
import com.example.studentreport.auth.dto.RegisterRequest
import com.example.studentreport.auth.dto.UserResponse

interface AuthService {
    fun login(request: LoginRequest): AuthResponse
    fun register(request: RegisterRequest): UserResponse
}