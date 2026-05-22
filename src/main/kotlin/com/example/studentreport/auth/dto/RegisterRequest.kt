package com.example.studentreport.auth.dto

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val nim: String,
    val faculty: String,
    val major: String,
    val year: Int
)
