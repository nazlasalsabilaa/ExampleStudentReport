package com.example.studentreport.user.service

import com.example.studentreport.auth.dto.UserResponse
import com.example.studentreport.entity.UserRole
import com.example.studentreport.user.dto.ChangePasswordRequest
import com.example.studentreport.user.dto.StudentDataResponse
import com.example.studentreport.user.dto.UpdateStudentDataRequest
import com.example.studentreport.user.dto.UpdateUserRequest
import com.example.studentreport.user.dto.UserStatsResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface UserService {
    fun getUserProfile(userId: UUID): UserResponse
    fun getAllUsers(search: String?, role: UserRole?, pageable: Pageable): Page<UserResponse>
    fun updateUser(userId: UUID, request: UpdateUserRequest): UserResponse
    fun deleteUser(userId: UUID)
    fun getUserStats(userId: UUID): UserStatsResponse
    fun changePassword(userId: UUID, request: ChangePasswordRequest)
    fun getStudentData(userId: UUID): StudentDataResponse
    fun updateStudentData(userId: UUID, request: UpdateStudentDataRequest): StudentDataResponse
}