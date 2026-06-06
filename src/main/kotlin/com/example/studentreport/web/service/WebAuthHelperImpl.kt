package com.example.studentreport.web.service

import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class WebAuthHelperImpl : WebAuthHelper {

    override fun isAdmin(auth: Authentication?): Boolean {
        return auth?.authorities?.any {
            it.authority == "ROLE_ADMIN" || it.authority == "ADMIN"
        } == true
    }

    override fun isAuthenticated(auth: Authentication?): Boolean {
        return auth?.isAuthenticated == true && auth.name != "anonymousUser"
    }

    override fun clearSessionCookie(): String {
        return ResponseCookie.from("session_token", "")
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .build()
            .toString()
    }
}