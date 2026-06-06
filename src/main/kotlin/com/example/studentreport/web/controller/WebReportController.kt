package com.example.studentreport.web.controller

import com.example.studentreport.auth.dto.UserResponse
import com.example.studentreport.entity.ReportStatus
import com.example.studentreport.report.service.ReportService
import com.example.studentreport.category.service.CategoryService
import com.example.studentreport.room.service.RoomService
import com.example.studentreport.web.service.WebAuthHelper
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Controller
class WebReportController(
    private val reportService: ReportService,
    private val categoryService: CategoryService,
    private val roomService: RoomService,
    private val webAuthHelper: WebAuthHelper
) {

    private fun Authentication?.getUserIdOrNull(): UUID? {
        if (this == null || !this.isAuthenticated) return null
        return when (val p = this.principal) {
            is UserResponse -> p.id
            is UUID -> p
            else -> null
        }
    }

    @GetMapping("/feed")
    fun feed(
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) categoryId: UUID?,
        @RequestParam(required = false) roomId: UUID?,
        @RequestParam(required = false) buildingId: UUID?,
        @RequestParam(required = false) status: ReportStatus?,
        @PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        auth: Authentication?,
        model: Model
    ): String {
        val isAdmin = webAuthHelper.isAdmin(auth)
        val currentUserId = auth.getUserIdOrNull()
            ?: throw IllegalStateException("User must be authenticated to view feed")

        val reportsPage = reportService.listReports(
            search = search,
            categoryId = categoryId,
            roomId = roomId,
            buildingId = buildingId,
            status = status,
            includeDeleted = false,
            currentUserId = currentUserId,
            isAdmin = isAdmin,
            pageable = pageable
        )

        model.addAttribute("isAdmin", isAdmin)
        model.addAttribute("allReports", reportsPage.content)
        model.addAttribute("categories", categoryService.getAllCategories(null, Pageable.unpaged()).content)
        model.addAttribute("rooms", roomService.getAllRooms(null, null, null, Pageable.unpaged()).content)

        model.addAttribute("currentSearch", search)
        model.addAttribute("currentCategory", categoryId)
        model.addAttribute("currentRoom", roomId)

        return "report/feed"
    }

    @GetMapping("/buat-laporan")
    fun buatLaporan(auth: Authentication?, model: Model): String {
        model.addAttribute("isAdmin", webAuthHelper.isAdmin(auth))
        model.addAttribute("categories", categoryService.getAllCategories(null, Pageable.unpaged()).content)
        model.addAttribute("rooms", roomService.getAllRooms(null, null, null, Pageable.unpaged()).content)
        return "report/buat_laporan"
    }

    @GetMapping("/report/{id}")
    fun detail(@PathVariable id: UUID, auth: Authentication?, model: Model): String {
        val userId = auth.getUserIdOrNull() ?: throw IllegalStateException("Must be authenticated")
        val isAdmin = webAuthHelper.isAdmin(auth)

        val report = reportService.getReportById(id, userId, isAdmin)
        model.addAttribute("report", report)

        return "report/detail_laporan"
    }
}