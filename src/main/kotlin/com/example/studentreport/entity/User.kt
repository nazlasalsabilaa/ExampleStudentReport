package com.example.studentreport.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    var email: String,

    @Column(name = "password_hash")
    var passwordHash: String,

    @Column(name = "created_at")
    val createdAt: Instant,

    var name: String,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    var role: UserRole,

    @Column(name = "updated_at")
    var updatedAt: Instant,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL]) 
    var studentData: StudentData? = null,
    
    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL]) 
    var stats: UserStats? = null,
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL]) 
    var sessions: MutableList<Session> = mutableListOf(),
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL]) 
    var reports: MutableList<Report> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    var idempotencyKeys: MutableList<IdempotencyKey> = mutableListOf()
) {
    fun addReport(report: Report) { 
        reports.add(report)
        report.user = this 
    }
    
    fun addSession(session: Session) { 
        sessions.add(session)
        session.user = this 
    }

    fun addIdempotencyKey(key: IdempotencyKey) {
        idempotencyKeys.add(key)
        key.user = this
    }
}