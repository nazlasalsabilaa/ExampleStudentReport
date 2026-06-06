package com.example.studentreport.storage.service

import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class MinioStorageServiceImpl(
    @Value("\${minio.url}") private val url: String,
    @Value("\${minio.public-url}") private val publicUrl: String,
    @Value("\${minio.access-key}") private val accessKey: String,
    @Value("\${minio.secret-key}") private val secretKey: String,
    @Value("\${minio.bucket-name}") private val bucketName: String
) : StorageService {
    private val minioClient = MinioClient.builder().endpoint(url).credentials(accessKey, secretKey).build()

    override fun store(file: MultipartFile): String {
        if (file.isEmpty) throw IllegalArgumentException("Failed to store empty file")

        val extensions = file.originalFilename?.substringAfterLast(".", "") ?: ""
        val uniqueFilename = "${UUID.randomUUID()}.$extensions"

        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(uniqueFilename)
                .stream(file.inputStream, file.size, -1)
                .contentType(file.contentType)
                .build()
        )

        return "$publicUrl/$bucketName/$uniqueFilename"
    }

    override fun delete(imageUrl: String) {
        val filename = imageUrl.substringAfterLast("/")
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .`object`(filename)
                .build()
        )
    }

}