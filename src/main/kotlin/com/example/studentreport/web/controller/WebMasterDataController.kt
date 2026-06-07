package com.example.studentreport.web.controller

import com.example.studentreport.building.service.BuildingService
import com.example.studentreport.category.dto.CreateCategoryRequest
import com.example.studentreport.category.dto.UpdateCategoryRequest
import com.example.studentreport.category.service.CategoryService
import com.example.studentreport.room.dto.CreateRoomRequest
import com.example.studentreport.room.dto.UpdateRoomRequest
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
import java.util.UUID

@Controller
@RequestMapping("/master-data")
class WebMasterDataController(
    private val categoryService: CategoryService,
    private val roomService: RoomService,
    private val buildingService: BuildingService,
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
        model.addAttribute("buildings", buildingService.getAllBuildings(null, Pageable.unpaged()).content)

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

    @PostMapping("/categories/update")
    fun updateCategory(
        @RequestParam id: UUID,
        @RequestParam name: String,
        @RequestParam description: String
    ): String {
        categoryService.updateCategory(id, UpdateCategoryRequest(name, description))
        return "redirect:/master-data"
    }

    @PostMapping("/categories/delete")
    fun deleteCategory(@RequestParam id: UUID): String {
        categoryService.deleteCategory(id)
        return "redirect:/master-data"
    }

    @PostMapping("/rooms")
    fun addRoom(
        @RequestParam buildingId: UUID,
        @RequestParam name: String,
        @RequestParam code: String,
        @RequestParam floor: Int,
    ): String {
        roomService.createRoom(CreateRoomRequest(buildingId, name, floor, code))
        return "redirect:/master-data"
    }

    @PostMapping("rooms/update")
    fun updateRoom(
        @RequestParam roomId: UUID,
        @RequestParam buildingId: UUID,
        @RequestParam name: String,
        @RequestParam code: String,
        @RequestParam floor: Int
    ): String {
        roomService.updateRoom(roomId, UpdateRoomRequest(buildingId, name, floor))
        return "redirect:/master-data"
    }

    @PostMapping("/rooms/delete")
    fun deleteRoom(@RequestParam id: UUID): String {
        roomService.deleteRoom(id)
        return "redirect:/master-data"
    }
}