package com.example.studentreport.user.dto

data class UpdateStudentDataRequest(
    val nim: String? = null,
    val faculty: String? = null,
    val major: String? = null,
    val year: Int? = null
)