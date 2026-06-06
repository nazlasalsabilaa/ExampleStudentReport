package com.example.studentreport.config

import com.example.studentreport.web.service.WebAuthHelper
import org.springframework.security.core.Authentication
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice
class GlobalModelAttributes(private val webAuthHelper: WebAuthHelper) {

    @ModelAttribute
    fun addGlobalAttributes(auth: Authentication?, model: Model) {
        model.addAttribute("isAdmin", webAuthHelper.isAdmin(auth))
    }
}