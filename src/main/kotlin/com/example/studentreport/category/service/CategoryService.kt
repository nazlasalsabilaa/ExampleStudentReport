package com.example.studentreport.category.service

import com.example.studentreport.category.dto.CategoryResponse
import com.example.studentreport.category.dto.CreateCategoryRequest
import com.example.studentreport.category.dto.UpdateCategoryRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface CategoryService {
    fun getAllCategories(search: String?, pageable: Pageable): Page<CategoryResponse>
    fun createCategory(request: CreateCategoryRequest): CategoryResponse
    fun getCategoryById(id: UUID): CategoryResponse
    fun updateCategory(id: UUID, request: UpdateCategoryRequest): CategoryResponse
    fun deleteCategory(id: UUID)
}