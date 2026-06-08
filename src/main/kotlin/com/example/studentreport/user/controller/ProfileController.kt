package com.example.studentreport.user.controller

import com.example.studentreport.repository.UserRepository
import com.example.studentreport.auth.dto.UserResponse
import org.springframework.security.core.Authentication
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.stereotype.Controller
import java.util.UUID

@Controller
class ProfileController(private val userRepository: UserRepository) {

    @GetMapping("/profile/admin")
    fun showAdminProfile(auth: Authentication?, model: Model): String {
        val principal = auth?.principal as? UserResponse ?: return "redirect:/login"
        
        val user = userRepository.findById(principal.id).orElse(null)
            ?: return "redirect:/login"

        if (user.role.name != "ADMIN") {
            return "redirect:/dashboard"
        }
        
        model.addAttribute("user", user)
        return "profile_admin"
    }

    @GetMapping("/profile/student")
    fun showStudentProfile(auth: Authentication?, model: Model): String {
        val principal = auth?.principal as? UserResponse ?: return "redirect:/login"
        
        val user = userRepository.findById(principal.id).orElse(null)
            ?: return "redirect:/login"

        if (user.role.name != "STUDENT" && user.role.name != "USER") {
            return "redirect:/dashboard"
        }
        
        model.addAttribute("user", user)
        return "profile_student"
    }
}