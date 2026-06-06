package com.example.studentreport.user.controller

import com.example.studentreport.auth.dto.ApiResponse
import com.example.studentreport.auth.dto.UserResponse
import com.example.studentreport.entity.UserRole
import com.example.studentreport.user.dto.ChangePasswordRequest
import com.example.studentreport.user.dto.StudentDataResponse
import com.example.studentreport.user.dto.UpdateStudentDataRequest
import com.example.studentreport.user.dto.UpdateUserRequest
import com.example.studentreport.user.dto.UserStatsResponse
import com.example.studentreport.user.service.UserServiceImpl
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class UserController(
    private val userService: UserServiceImpl
) {
    private fun Authentication.getUserId(): UUID {
        return when (val principal = this.principal) {
            is UserResponse -> principal.id
            is UUID -> principal
            else -> throw IllegalStateException("Unexpected principal type")
        }
    }

    @GetMapping("/auth/me", "/users/me")
    fun getOwnProfile(authentication: Authentication): ApiResponse<UserResponse> {
        val userId = authentication.getUserId()
        val userProfile = userService.getUserProfile(userId)
        return ApiResponse(success = true, message = "Profile retrieved", data = userProfile)
    }

    @PutMapping("/users/me")
    fun updateOwnProfile(
        authentication: Authentication,
        @RequestBody request: UpdateUserRequest
    ): ApiResponse<UserResponse> {
        val userId = authentication.getUserId()
        val updatedUser = userService.updateUser(userId, request)
        return ApiResponse(success = true, message = "Profile updated", data = updatedUser)
    }

    @PatchMapping("/users/me/password")
    fun changeOwnPassword(
        authentication: Authentication,
        @RequestBody request: ChangePasswordRequest
    ): ApiResponse<Unit> {
        val userId = authentication.getUserId()
        userService.changePassword(userId, request)
        return ApiResponse(success = true, message = "Password changed successfully")
    }

    @GetMapping("/users/me/stats")
    fun getOwnStats(authentication: Authentication): ApiResponse<UserStatsResponse> {
        val userId = authentication.getUserId()
        val stats = userService.getUserStats(userId)
        return ApiResponse(success = true, message = "Stats retrieved", data = stats)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    fun getAllUsers(
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) role: UserRole?,
        @PageableDefault(size = 20) pageable: Pageable
    ): ApiResponse<Page<UserResponse>> {
        val users = userService.getAllUsers(search, role, pageable)
        return ApiResponse(success = true, message = "Users retrieved", data = users)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}")
    fun getUserById(@PathVariable id: UUID): ApiResponse<UserResponse> {
        val user = userService.getUserProfile(id)
        return ApiResponse(success = true, message = "User retrieved", data = user)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}")
    fun updateUser(
        @PathVariable id: UUID,
        @RequestBody request: UpdateUserRequest
    ): ApiResponse<UserResponse> {
        val updatedUser = userService.updateUser(id, request)
        return ApiResponse(success = true, message = "User updated", data = updatedUser)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id: UUID): ApiResponse<Unit> {
        userService.deleteUser(id)
        return ApiResponse(success = true, message = "User deleted successfully")
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}/stats")
    fun getUserStats(@PathVariable id: UUID): ApiResponse<UserStatsResponse> {
        val stats = userService.getUserStats(id)
        return ApiResponse(success = true, message = "User stats retrieved", data = stats)
    }

    @GetMapping("/users/me/student-data")
    fun getOwnStudentData(authentication: Authentication): ApiResponse<StudentDataResponse> {
        val userId = authentication.getUserId()
        val data = userService.getStudentData(userId)
        return ApiResponse(success = true, message = "Student data retrieved", data = data)
    }

    @PatchMapping("/users/me/student-data")
    fun updateOwnStudentData(
        authentication: Authentication,
        @RequestBody request: UpdateStudentDataRequest
    ): ApiResponse<StudentDataResponse> {
        val userId = authentication.getUserId()
        val data = userService.updateStudentData(userId, request)
        return ApiResponse(success = true, message = "Student data updated", data = data)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}/student-data")
    fun getStudentDataByUserId(@PathVariable id: UUID): ApiResponse<StudentDataResponse> {
        val data = userService.getStudentData(id)
        return ApiResponse(success = true, message = "Student data retrieved", data = data)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{id}/student-data")
    fun updateStudentDataByUserId(
        @PathVariable id: UUID,
        @RequestBody request: UpdateStudentDataRequest
    ): ApiResponse<StudentDataResponse> {
        val data = userService.updateStudentData(id, request)
        return ApiResponse(success = true, message = "Student data updated", data = data)
    }
}