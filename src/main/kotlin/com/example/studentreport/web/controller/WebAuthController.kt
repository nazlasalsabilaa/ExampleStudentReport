package com.example.studentreport.web.controller

import com.example.studentreport.auth.service.AuthService
import com.example.studentreport.web.service.WebAuthHelper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.view.RedirectView

@Controller
class WebAuthController(
    private val authService: AuthService,
    private val webAuthHelper: WebAuthHelper
) {
    @GetMapping("/")
    fun rootRedirect(auth: Authentication?): RedirectView {
        if (webAuthHelper.isAuthenticated(auth)) {
            return if (webAuthHelper.isAdmin(auth)) RedirectView("/master-data") else RedirectView("/dashboard")
        }
        return RedirectView("/login")
    }

    @GetMapping("/login")
    fun login(auth: Authentication?): String {
        if (webAuthHelper.isAuthenticated(auth)) {
            return if (webAuthHelper.isAdmin(auth)) "redirect:/master-data" else "redirect:/dashboard"
        }
        return "auth/login"
    }

    @GetMapping("/register")
    fun register(auth: Authentication?): String {
        if (webAuthHelper.isAuthenticated(auth)) {
            return if (webAuthHelper.isAdmin(auth)) "redirect:/master-data" else "redirect:/dashboard"
        }
        return "auth/register"
    }

    @GetMapping("/user/logout")
    fun logout(
        @CookieValue("session_token", required = false) cookieToken: String?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String {
        cookieToken?.let { authService.logout(it) }

        response.addHeader(HttpHeaders.SET_COOKIE, webAuthHelper.clearSessionCookie())
        SecurityContextHolder.clearContext()
        request.session.invalidate()

        return "redirect:/login"
    }
}