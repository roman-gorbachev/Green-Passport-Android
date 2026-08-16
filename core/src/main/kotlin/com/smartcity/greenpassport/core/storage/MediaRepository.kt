package com.smartcity.greenpassport.core.storage

interface MediaRepository {
    suspend fun upload(path: String, bytes: ByteArray): String
    suspend fun getDownloadUrl(path: String): String
    suspend fun delete(path: String)
}
