package com.example.studentreport.category.service

import com.example.studentreport.category.dto.CategoryResponse
import com.example.studentreport.category.dto.CreateCategoryRequest
import com.example.studentreport.category.dto.UpdateCategoryRequest
import com.example.studentreport.entity.Category
import com.example.studentreport.repository.CategoryRepository
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository
) : CategoryService {

    @Transactional(readOnly = true)
    override fun getAllCategories(search: String?, pageable: Pageable): Page<CategoryResponse> {
        val spec = Specification<Category> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            if (!search.isNullOrBlank()) {
                val searchPattern = "%${search.lowercase()}%"
                val namePredicate = cb.like(cb.lower(root.get("name")), searchPattern)
                val descPredicate = cb.like(cb.lower(root.get("description")), searchPattern)
                predicates.add(cb.or(namePredicate, descPredicate))
            }
            cb.and(*predicates.toTypedArray())
        }
        return categoryRepository.findAll(spec, pageable).map { it.toResponse() }
    }

    @Transactional
    override fun createCategory(request: CreateCategoryRequest): CategoryResponse {
        val now = Instant.now()
        val category = Category(
            name = request.name,
            description = request.description,
            createdAt = now,
            updatedAt = now
        )
        return categoryRepository.save(category).toResponse()
    }

    @Transactional(readOnly = true)
    override fun getCategoryById(id: UUID): CategoryResponse {
        val category = categoryRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Category not found") }
        return category.toResponse()
    }

    @Transactional
    override fun updateCategory(id: UUID, request: UpdateCategoryRequest): CategoryResponse {
        val category = categoryRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Category not found") }

        request.name?.let { category.name = it }
        request.description?.let { category.description = it }
        category.updatedAt = Instant.now()

        return categoryRepository.save(category).toResponse()
    }

    @Transactional
    override fun deleteCategory(id: UUID) {
        val category = categoryRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Category not found") }
        categoryRepository.delete(category)
    }

    private fun Category.toResponse(): CategoryResponse {
        return CategoryResponse(
            id = this.id!!,
            name = this.name,
            description = this.description,
            createdAt = this.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = this.updatedAt.atOffset(ZoneOffset.UTC)
        )
    }
}