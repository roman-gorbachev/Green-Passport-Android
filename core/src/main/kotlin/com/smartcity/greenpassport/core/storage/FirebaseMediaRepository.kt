package com.smartcity.greenpassport.core.storage

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val STORAGE_ROOT = "greenpassport"

class FirebaseMediaRepository @Inject constructor(
    private val storage: FirebaseStorage,
) : MediaRepository {

    override suspend fun upload(path: String, bytes: ByteArray): String {
        val reference = storage.reference.child(namespacedPath(path))
        reference.putBytes(bytes).await()
        return reference.downloadUrl.await().toString()
    }

    override suspend fun getDownloadUrl(path: String): String =
        storage.reference.child(namespacedPath(path)).downloadUrl.await().toString()

    override suspend fun delete(path: String) {
        storage.reference.child(namespacedPath(path)).delete().await()
    }
}

private fun namespacedPath(path: String): String = "$STORAGE_ROOT/$path"
