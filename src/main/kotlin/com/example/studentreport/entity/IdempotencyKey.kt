package com.example.studentreport.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "idempotency_keys")
data class IdempotencyKey(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int? = null,
    val key: String,
    @Column(name = "user_id", insertable = false, updatable = false) val userId: UUID? = null,
    @Column(name = "request_path") val requestPath: String,
    @Column(name = "response_status") var responseStatus: Short,
    @Column(name = "response_body", columnDefinition = "TEXT") var responseBody: String,
    @Column(name = "created_at") val createdAt: Instant,
    @Column(name = "expires_at") val expiresAt: Instant,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User? = null
) {
    fun isExpired(): Boolean {
        return Instant.now().isAfter(expiresAt)
    }
}