package com.example.studentreport.repository

import com.example.studentreport.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CategoryRepository : JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {
}