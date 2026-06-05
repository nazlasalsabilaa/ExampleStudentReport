package com.example.studentreport.repository.specification

import com.example.studentreport.entity.Report
import com.example.studentreport.entity.ReportStatus
import com.example.studentreport.entity.Room
import org.springframework.data.jpa.domain.Specification
import jakarta.persistence.criteria.Predicate
import java.time.Instant
import java.util.UUID

object ReportSpecification {
    fun withFilters(
        search: String?,
        categoryId: UUID?,
        roomId: UUID?,
        buildingId: UUID?,
        status: ReportStatus?,
        includeDeleted: Boolean,
        userId: UUID? = null
    ): Specification<Report> {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            if (!includeDeleted) {
                predicates.add(cb.isNull(root.get<Instant>("deletedAt")))
            }

            if (!search.isNullOrBlank()) {
                val searchPattern = "%${search.lowercase()}%"
                val titlePredicate = cb.like(cb.lower(root.get("title")), searchPattern)
                val descPredicate = cb.like(cb.lower(root.get("description")), searchPattern)
                predicates.add(cb.or(titlePredicate, descPredicate))
            }

            categoryId?.let { predicates.add(cb.equal(root.get<UUID>("categoryId"), it)) }
            roomId?.let { predicates.add(cb.equal(root.get<UUID>("roomId"), it)) }
            status?.let { predicates.add(cb.equal(root.get<ReportStatus>("status"), it)) }
            userId?.let { predicates.add(cb.equal(root.get<UUID>("reporterId"), it)) }

            if (buildingId != null) {
                val roomJoin = root.join<Report, Room>("room")
                predicates.add(cb.equal(roomJoin.get<UUID>("buildingId"), buildingId))
            }

            cb.and(*predicates.toTypedArray())
        }
    }
}