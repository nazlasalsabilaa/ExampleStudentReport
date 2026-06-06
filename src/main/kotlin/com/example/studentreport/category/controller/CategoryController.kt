package com.example.studentreport.category.controller

import com.example.studentreport.auth.dto.ApiResponse
import com.example.studentreport.category.dto.CategoryResponse
import com.example.studentreport.category.dto.CreateCategoryRequest
import com.example.studentreport.category.dto.UpdateCategoryRequest
import com.example.studentreport.category.service.CategoryService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @GetMapping
    fun getAllCategories(
        @RequestParam(required = false) search: String?,
        @PageableDefault(size = 20) pageable: Pageable
    ): ApiResponse<Page<CategoryResponse>> {
        val categories = categoryService.getAllCategories(search, pageable)
        return ApiResponse(success = true, message = "Categories retrieved successfully", data = categories)
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createCategory(
        @RequestHeader("Idempotency-Key", required = false) idempotencyKey: String?,
        @RequestBody request: CreateCategoryRequest
    ): ApiResponse<CategoryResponse> {
        val category = categoryService.createCategory(request)
        return ApiResponse(success = true, message = "Category created successfully", data = category)
    }

    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: UUID): ApiResponse<CategoryResponse> {
        val category = categoryService.getCategoryById(id)
        return ApiResponse(success = true, message = "Category retrieved successfully", data = category)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateCategory(
        @PathVariable id: UUID,
        @RequestHeader("Idempotency-Key", required = false) idempotencyKey: String?,
        @RequestBody request: UpdateCategoryRequest
    ): ApiResponse<CategoryResponse> {
        val category = categoryService.updateCategory(id, request)
        return ApiResponse(success = true, message = "Category updated successfully", data = category)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteCategory(@PathVariable id: UUID): ApiResponse<Unit> {
        categoryService.deleteCategory(id)
        return ApiResponse(success = true, message = "Category deleted successfully")
    }
}