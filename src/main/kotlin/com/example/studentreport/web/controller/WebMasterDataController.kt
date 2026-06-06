package com.example.studentreport.web.controller

import com.example.studentreport.category.dto.CreateCategoryRequest
import com.example.studentreport.category.service.CategoryService
import com.example.studentreport.room.service.RoomService
import com.example.studentreport.web.service.WebAuthHelper
import org.springframework.data.domain.Pageable
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/master-data")
class WebMasterDataController(
    private val categoryService: CategoryService,
    private val roomService: RoomService,
    private val webAuthHelper: WebAuthHelper
) {
    @GetMapping
    fun masterData(auth: Authentication?, model: Model): String {
        if (!webAuthHelper.isAdmin(auth)) {
            return "redirect:/dashboard"
        }

        model.addAttribute("isAdmin", true)
        model.addAttribute("categories", categoryService.getAllCategories(null, Pageable.unpaged()).content)
        model.addAttribute("rooms", roomService.getAllRooms(null, null, null, Pageable.unpaged()).content)

        return "master_data"
    }

    @PostMapping("/categories")
    fun addCategory(
        @RequestParam name: String,
        @RequestParam description: String
    ): String {
        categoryService.createCategory(CreateCategoryRequest(name, description))
        return "redirect:/master-data"
    }
}