package com.example.studentreport.web.controller

import com.example.studentreport.web.service.ReportService
import com.example.studentreport.web.service.MasterDataService
import com.example.studentreport.web.service.WebAuthHelper
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Controller
class WebReportController(
    private val reportService: ReportService,
    private val masterDataService: MasterDataService,
    private val webAuthHelper: WebAuthHelper
) {
    @GetMapping("/feed")
    fun feed(
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) categoryId: UUID?,
        @RequestParam(required = false) roomId: UUID?,
        @PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        auth: Authentication?,
        model: Model
    ): String {
        val reportsPage = reportService.getFeedReports(search, categoryId, roomId, pageable)

        model.addAttribute("isAdmin", webAuthHelper.isAdmin(auth))
        model.addAttribute("allReports", reportsPage.content)
        model.addAttribute("categories", masterDataService.getAllCategories())
        model.addAttribute("rooms", masterDataService.getAllRooms())
        model.addAttribute("currentSearch", search)
        model.addAttribute("currentCategory", categoryId)
        model.addAttribute("currentRoom", roomId)

        return "report/feed"
    }

    @GetMapping("/buat-laporan")
    fun buatLaporan(auth: Authentication?, model: Model): String {
        model.addAttribute("isAdmin", webAuthHelper.isAdmin(auth))
        model.addAttribute("categories", masterDataService.getAllCategories())
        model.addAttribute("rooms", masterDataService.getAllRooms())
        return "report/buat_laporan"
    }
}