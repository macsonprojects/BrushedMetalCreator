package com.example.brushedmetalcreator.ui.feature_editor

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.brushedmetalcreator.domain.engine.GradientType
import com.example.brushedmetalcreator.domain.engine.TextureParams

data class MetalColor(
    val name: String,
    val color: Color
)

// 1. Set the default starting colour in EditorUiState.kt
private const val STARTING_COLOR_INDEX = 0

val DEFAULT_METAL_COLORS = listOf(
    MetalColor("Stainless\nSteel",  Color(0xFFD0D0D0)),
    MetalColor("Soft\nStainless",   Color(0xFFC9C4B6)),
    MetalColor("Soft Steel",        Color(0xFFBABAC3)),
    MetalColor("Cool Alloy",        Color(0xFF9994A0)),
    MetalColor("Steel",             Color(0xFF888888)),
    MetalColor("Mid Steel",         Color(0xFF85858C)),
    MetalColor("Neutral\nAlloy",    Color(0xFFA89A8D)),
    MetalColor("Titanium",          Color(0xFF8A9BAE)),
    MetalColor("Titanium\nSlate",   Color(0xFF78889C)),
    MetalColor("Steel Blue",        Color(0xFF66758A)),
    MetalColor("Shadow\nMetal",     Color(0xFF545F73)),
    MetalColor("Graphite",          Color(0xFF4F4F55)),
    MetalColor("Gunmetal",          Color(0xFF4A5058)),
    MetalColor("Dark Steel",        Color(0xFF444444)),
    MetalColor("Dark\nGunmetal",    Color(0xFF3F454C)),
    MetalColor("Charcoal",          Color(0xFF343A40)),
    MetalColor("Obsidian",          Color(0xFF292E32)),
    MetalColor("Black\nStainless",  Color(0xFF1A1A1C)),
    MetalColor("Brass",             Color(0xFFC6A667)),
    MetalColor("Bronze",            Color(0xFFCD7F32)),
    MetalColor("Copper",            Color(0xFFB87333)),
)

data class EditorUiState(
    val params: TextureParams = TextureParams(
        baseColorArgb = (DEFAULT_METAL_COLORS[STARTING_COLOR_INDEX].color.toArgb())
    ),
    val generatedBitmap: Bitmap? = null,
    val isGenerating: Boolean = false,
    val isExporting: Boolean = false,
    val exportMessage: String? = null,
    val items: List<SwipeMenuItem> = listOf(
        SwipeMenuItem(ITEM_RESET,               "RESET"),
        SwipeMenuItem(ITEM_HIGHLIGHT,           "HIGHLIGHT", isActive = false),
        SwipeMenuItem(ITEM_SHADOW,              "SHADOW", isActive = false),
        SwipeMenuItem(ITEM_FALLOFF,             "FALLOFF\nCURVE", isActive = false),
        SwipeMenuItem(ITEM_LINEAR_GRADIENT,     "LINEAR\nGRADIENT", isActive = false),
        SwipeMenuItem(ITEM_RADIAL_GRADIENT,     "RADIAL\nGRADIENT", isActive = false),
        SwipeMenuItem(ITEM_GRAIN_INTENSITY,     "GRAIN\nINTENSITY", isActive = false),
        SwipeMenuItem(ITEM_BASE_METAL,          "BASE\nCOLOUR", isActive = false),
    ),
    val colorList: List<MetalColor> = DEFAULT_METAL_COLORS,
    val colorIndex: Int = STARTING_COLOR_INDEX,
    val gradientType: GradientType = GradientType.Linear
) {
    /**
     * Synchronizes menu item percentage values (0-100) with current texture parameters.
     * 
     * This ensures the UI remains consistent with the "source of truth" (TextureParams).
     * For example, if intensity is 0.5, the label in the menu is updated to "50".
     */
    fun syncWithParams(): EditorUiState {
        val updatedItems = items.map { item ->
            val newValue = when (item.id) {
                ITEM_HIGHLIGHT -> (params.highlightStrength * 100).toInt()
                ITEM_SHADOW -> (params.shadowStrength * 100).toInt()
                ITEM_FALLOFF -> (params.falloffExponent * 100).toInt() - 50
                ITEM_RADIAL_GRADIENT -> (params.radialRadiusRatio * 100).toInt()
                ITEM_GRAIN_INTENSITY -> (params.intensity * 100).toInt()
                else -> item.value
            }
            item.copy(value = newValue)
        }
        return copy(items = updatedItems)
    }
}