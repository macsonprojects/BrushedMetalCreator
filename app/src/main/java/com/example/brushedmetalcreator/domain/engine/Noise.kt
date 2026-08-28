package com.example.brushedmetalcreator.domain.engine

import android.graphics.Color
import kotlin.math.pow

object Noise {

    fun curve(t: Float, exponent: Float): Float = t.coerceIn(0f, 1f).pow(exponent)

    private fun lerpColor(c1: Int, c2: Int, t: Float): Int {
        val a = (Color.alpha(c1) + (Color.alpha(c2) - Color.alpha(c1)) * t).toInt()
        val r = (Color.red(c1) + (Color.red(c2) - Color.red(c1)) * t).toInt()
        val g = (Color.green(c1) + (Color.green(c2) - Color.green(c1)) * t).toInt()
        val b = (Color.blue(c1) + (Color.blue(c2) - Color.blue(c1)) * t).toInt()
        return Color.argb(a, r, g, b)
    }

    fun metallicGradientLinear(
        highlightStrength: Float,
        shadowStrength: Float,
        falloffExponent: Float,
        baseColor: Int
    ): List<Int> {
        val black = Color.BLACK
        val white = Color.WHITE

        val shadow = lerpColor(baseColor, black, curve(shadowStrength * 1.2f, falloffExponent))
        val shadowMid = lerpColor(baseColor, black, curve(shadowStrength * 0.8f, falloffExponent))
        val midLow = lerpColor(baseColor, black, curve(shadowStrength * 0.5f, falloffExponent))

        val highlight = lerpColor(baseColor, white, curve(highlightStrength * 1.4f, falloffExponent))
        val midHigh = lerpColor(baseColor, white, curve(highlightStrength * 0.6f, falloffExponent))

        return listOf(
            shadow, shadowMid, midLow, baseColor, midHigh, highlight, midHigh, baseColor, midLow, shadowMid, shadow
        )
    }

    fun metallicGradientRadial(
        highlightStrength: Float,
        shadowStrength: Float,
        falloffExponent: Float,
        baseColor: Int
    ): List<Int> {
        val black = Color.BLACK
        val white = Color.WHITE

        val shadow = lerpColor(baseColor, black, curve(shadowStrength * 1.2f, falloffExponent))
        val shadowMid = lerpColor(baseColor, black, curve(shadowStrength * 0.8f, falloffExponent))
        val midLow = lerpColor(baseColor, black, curve(shadowStrength * 0.5f, falloffExponent))

        val midHigh = lerpColor(baseColor, white, curve(highlightStrength * 0.5f, falloffExponent))
        val highlight = lerpColor(baseColor, white, curve(highlightStrength * 1.4f, falloffExponent))

        return listOf(highlight, midHigh, baseColor, midLow, shadowMid, shadow)
    }
}