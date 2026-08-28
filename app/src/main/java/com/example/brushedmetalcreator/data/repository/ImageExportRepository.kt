package com.example.brushedmetalcreator.data.repository

import android.graphics.Bitmap

enum class ExportFormat {
    PNG,
    JPEG
}

interface ImageExportRepository {
    suspend fun exportImage(
        bitmap: Bitmap,
        format: ExportFormat,
        fileName: String
    ): Result<Unit>
}