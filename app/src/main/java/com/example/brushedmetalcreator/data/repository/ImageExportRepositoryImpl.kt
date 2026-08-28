package com.example.brushedmetalcreator.data.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageExportRepositoryImpl(
    private val context: Context
) : ImageExportRepository {

    override suspend fun exportImage(
        bitmap: Bitmap,
        format: ExportFormat,
        fileName: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val compressFormat = when (format) {
                ExportFormat.PNG -> Bitmap.CompressFormat.PNG
                ExportFormat.JPEG -> Bitmap.CompressFormat.JPEG
            }
            val mimeType = when (format) {
                ExportFormat.PNG -> "image/png"
                ExportFormat.JPEG -> "image/jpeg"
            }
            val extension = when (format) {
                ExportFormat.PNG -> ".png"
                ExportFormat.JPEG -> ".jpg"
            }

            val fullFileName = if (fileName.endsWith(extension, ignoreCase = true)) fileName else "$fileName$extension"

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fullFileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/BrushedMetal")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw IllegalStateException("Failed to create MediaStore entry")

            resolver.openOutputStream(imageUri)?.use { outputStream ->
                val success = bitmap.compress(compressFormat, 100, outputStream)
                if (!success) throw IllegalStateException("Failed to compress bitmap")
            } ?: throw IllegalStateException("Failed to open output stream")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            }
        }
    }
}