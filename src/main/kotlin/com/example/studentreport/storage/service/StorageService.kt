package com.example.studentreport.storage.service

import org.springframework.web.multipart.MultipartFile

interface StorageService {
    fun store(file: MultipartFile): String
    fun delete(imageUrl: String)
}