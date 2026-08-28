package com.example.brushedmetalcreator.utilities

import androidx.compose.ui.graphics.Color

/**
 * Parses hex strings like "FF5733", "0xFF5733", or "#FF5733" to a Compose Color.
 * Returns null if the format is invalid.
 */
fun parseHexColor(hexString: String): Color? {
    val cleanHex = hexString.trim()
        .removePrefix("#")
        .removePrefix("0x")
        .removePrefix("0X")

    return runCatching {
        when (cleanHex.length) {
            6 -> {
                val colorInt = cleanHex.toLong(16).toInt() or 0xFF000000.toInt()
                Color(colorInt)
            }
            8 -> {
                val colorInt = cleanHex.toLong(16).toInt()
                Color(colorInt)
            }
            else -> null
        }
    }.getOrNull()
}

