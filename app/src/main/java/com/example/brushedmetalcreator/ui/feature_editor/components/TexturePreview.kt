package com.example.brushedmetalcreator.ui.feature_editor.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import com.example.brushedmetalcreator.domain.engine.GradientType
import com.example.brushedmetalcreator.domain.engine.Noise
import com.example.brushedmetalcreator.domain.engine.TextureParams
import com.example.brushedmetalcreator.ui.feature_editor.GRAIN_ALPHA_MAX
import com.example.brushedmetalcreator.ui.feature_editor.SPECULAR_HIGHLIGHT_ALPHA

@Composable
fun TexturePreview(
    params: TextureParams,
    generatedBitmap: Bitmap?,
    isGenerating: Boolean,
    graphicsLayer: GraphicsLayer,
    modifier: Modifier = Modifier,
    gradientType: GradientType = GradientType.Linear,
) {
    val falloffExponent = remember(params.falloffExponent) {
        0.5f + (params.falloffExponent * 3.0f)
    }

    val baseColor = params.baseColorArgb

    val colors = remember(baseColor, params.highlightStrength, params.shadowStrength, falloffExponent, gradientType) {
        val intColors = when (gradientType) {
            GradientType.Linear -> Noise.metallicGradientLinear(
                params.highlightStrength,
                params.shadowStrength,
                falloffExponent,
                baseColor
            )
            GradientType.Radial -> Noise.metallicGradientRadial(
                params.highlightStrength,
                params.shadowStrength,
                falloffExponent,
                baseColor
            )
        }
        intColors.map { Color(it) }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawWithContent {
                        graphicsLayer.record {
                            this@onDrawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    }
                }
        ) {
            // 1. Draw base metallic gradient
            val gradientBrush = when (gradientType) {
                GradientType.Linear -> Brush.linearGradient(
                    colors = colors,
                    start = Offset(size.width / 2f, 0f),
                    end = Offset(size.width / 2f, size.height)
                )
                GradientType.Radial -> {
                    val minRadius = minOf(size.width, size.height) / 2f
                    val maxRadius = maxOf(size.width, size.height) / 2f
                    val radius = minRadius + (maxRadius - minRadius) * params.radialRadiusRatio
                    Brush.radialGradient(
                        colors = colors,
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = radius
                    )
                }
            }
            drawRect(brush = gradientBrush)

            // 2. Overlay asynchronously generated noise bitmap
            generatedBitmap?.let { bitmap ->
                drawImage(
                    image = bitmap.asImageBitmap(),
                    dstSize = androidx.compose.ui.unit.IntSize(
                        width = size.width.toInt(),
                        height = size.height.toInt()
                    ),
                    alpha = GRAIN_ALPHA_MAX * params.intensity,
                    blendMode = BlendMode.Multiply
                )
            }

            // 3. Draw specular highlight overlay
            val highlightBrush = Brush.linearGradient(
                0f to Color.Transparent,
                0.35f to Color.Transparent,
                0.5f to Color.White.copy(alpha = SPECULAR_HIGHLIGHT_ALPHA * params.highlightStrength),
                0.65f to Color.Transparent,
                1f to Color.Transparent,
                start = Offset(size.width / 2f, 0f),
                end = Offset(size.width / 2f, size.height)
            )
            drawRect(brush = highlightBrush, blendMode = BlendMode.Screen)
        }

        // Show non-intrusive indicator when engine is calculating
        if (isGenerating && generatedBitmap == null) {
            CircularProgressIndicator(color = Color.White.copy(alpha = 0.7f))
        }
    }
}