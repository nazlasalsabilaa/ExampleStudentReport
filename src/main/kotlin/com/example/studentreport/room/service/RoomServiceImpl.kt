package com.example.studentreport.room.service

import com.example.studentreport.building.dto.BuildingResponse
import com.example.studentreport.entity.Room
import com.example.studentreport.repository.BuildingRepository
import com.example.studentreport.repository.RoomRepository
import com.example.studentreport.room.dto.CreateRoomRequest
import com.example.studentreport.room.dto.RoomResponse
import com.example.studentreport.room.dto.UpdateRoomRequest
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
class RoomServiceImpl(
    private val roomRepository: RoomRepository,
    private val buildingRepository: BuildingRepository
) : RoomService {

    @Transactional(readOnly = true)
    override fun getAllRooms(search: String?, buildingId: UUID?, floor: Int?, pageable: Pageable): Page<RoomResponse> {
        val spec = Specification<Room> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            if (!search.isNullOrBlank()) {
                val searchPattern = "%${search.lowercase()}%"
                val namePredicate = cb.like(cb.lower(root.get("name")), searchPattern)
                val codePredicate = cb.like(cb.lower(root.get("code")), searchPattern)
                predicates.add(cb.or(namePredicate, codePredicate))
            }

            buildingId?.let { predicates.add(cb.equal(root.get<UUID>("buildingId"), it)) }
            floor?.let { predicates.add(cb.equal(root.get<Int>("floor"), it)) }

            cb.and(*predicates.toTypedArray())
        }
        return roomRepository.findAll(spec, pageable).map { it.toResponse() }
    }

    @Transactional
    override fun createRoom(request: CreateRoomRequest): RoomResponse {
        if (roomRepository.existsByCode(request.code)) {
            throw IllegalArgumentException("Room code already exists")
        }

        val building = buildingRepository.findById(request.buildingId)
            .orElseThrow { IllegalArgumentException("Building not found") }

        val now = Instant.now()
        val room = Room(
            buildingId = request.buildingId,
            name = request.name,
            floor = request.floor,
            code = request.code,
            createdAt = now,
            updatedAt = now,
            building = building
        )
        return roomRepository.save(room).toResponse()
    }

    @Transactional(readOnly = true)
    override fun getRoomById(id: UUID): RoomResponse {
        val room = roomRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Room not found") }
        return room.toResponse()
    }

    @Transactional
    override fun updateRoom(id: UUID, request: UpdateRoomRequest): RoomResponse {
        val room = roomRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Room not found") }

        request.code?.let { newCode ->
            if (newCode != room.code && roomRepository.existsByCode(newCode)) {
                throw IllegalArgumentException("Room code already exists")
            }
            room.code = newCode
        }
        request.name?.let { room.name = it }
        request.floor?.let { room.floor = it }

        request.buildingId?.let { newBuildingId ->
            val building = buildingRepository.findById(newBuildingId)
                .orElseThrow { IllegalArgumentException("Building not found") }
            room.building = building
        }

        room.updatedAt = Instant.now()
        return roomRepository.save(room).toResponse()
    }

    @Transactional
    override fun deleteRoom(id: UUID) {
        val room = roomRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Room not found") }
        roomRepository.delete(room)
    }

    @Transactional(readOnly = true)
    override fun getRoomsByBuildingId(buildingId: UUID, pageable: Pageable): Page<RoomResponse> {
        if (!buildingRepository.existsById(buildingId)) {
            throw IllegalArgumentException("Building not found")
        }
        return roomRepository.findByBuildingId(buildingId, pageable).map { it.toResponse() }
    }

    private fun Room.toResponse(): RoomResponse {
        val buildingResponse = this.building?.let {
            BuildingResponse(
                id = it.id!!,
                name = it.name,
                code = it.code,
                createdAt = it.createdAt.atOffset(ZoneOffset.UTC),
                updatedAt = it.updatedAt.atOffset(ZoneOffset.UTC)
            )
        }

        return RoomResponse(
            id = this.id!!,
            buildingId = this.buildingId,
            building = buildingResponse,
            name = this.name,
            floor = this.floor,
            code = this.code,
            createdAt = this.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = this.updatedAt.atOffset(ZoneOffset.UTC)
        )
    }
}