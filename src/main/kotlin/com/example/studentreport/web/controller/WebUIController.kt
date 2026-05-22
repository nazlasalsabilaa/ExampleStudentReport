package com.example.studentreport.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.view.RedirectView

@Controller
class WebUIController {
    @GetMapping("/")
    fun rootRedirect(): RedirectView {
        return RedirectView("/login")
    }

    @GetMapping("/login")
    fun login() = "login"

    @GetMapping("/register")
    fun register() = "register"

    @GetMapping("/dashboard")
    fun dashboard() = "dashboard_mahasiswa"
}