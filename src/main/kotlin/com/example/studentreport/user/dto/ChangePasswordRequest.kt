package com.example.studentreport.user.dto

data class ChangePasswordRequest(
    val newPassword: String,
    val oldPassword: String
)
