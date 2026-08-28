package com.example.brushedmetalcreator.domain.engine

data class TextureParams(
    val width: Int = 800,
    val height: Int = 800,
    val highlightStrength: Float = 0.5f,
    val shadowStrength: Float = 0.5f,
    val falloffExponent: Float = 0.5f,
    val radialRadiusRatio: Float = 0.5f,
    val intensity: Float = 0.5f,
    val baseColorArgb: Int, // Base colour is explicitly provided by the UI state
)