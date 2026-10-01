package com.smartcity.greenpassport.feature.tasks.presentation.ui

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

private const val PHOTOS_DIRECTORY = "task_photos"
private const val AUTHORITY_SUFFIX = ".taskphotos"

fun createTaskPhotoUri(context: Context): Uri {
    val directory = File(context.cacheDir, PHOTOS_DIRECTORY).apply { mkdirs() }
    val file = File.createTempFile("task_", ".jpg", directory)
    return FileProvider.getUriForFile(context, context.packageName + AUTHORITY_SUFFIX, file)
}
