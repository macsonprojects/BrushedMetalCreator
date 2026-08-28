package com.example.brushedmetalcreator.domain.engine

import android.graphics.Bitmap
import com.example.brushedmetalcreator.ui.feature_editor.FIXED_RANDOM_SEED
import com.example.brushedmetalcreator.ui.feature_editor.NOISE_STEP_RANGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

object TextureEngine {

    /**
     * Generates a noise bitmap on a CPU background thread.
     * 
     * The algorithm uses a "random walk" approach: for each vertical column, it starts 
     * with a random intensity and then makes small incremental changes as it moves down. 
     * This creates the characteristic vertical streaks seen in brushed metal.
     * 
     * @param params Contains the dimensions and the base colour for the texture.
     * @return A [Bitmap] containing the grayscale directional noise.
     */
    suspend fun generateDirectionalNoise(params: TextureParams): Bitmap = withContext(Dispatchers.Default) {
        val width = params.width
        val height = params.height
        val pixels = IntArray(width * height)
        
        // Using a fixed seed ensures that the underlying grain pattern remains stable
        // when the user only adjusts intensities or colors.
        val random = Random(FIXED_RANDOM_SEED) 

        for (x in 0 until width) {
            var last = (random.nextFloat() * 255).toInt()

            for (y in 0 until height) {
                // The random step creates the "brushed" effect. 
                // Larger steps result in rougher grain.
                val current = (last + random.nextInt(-NOISE_STEP_RANGE, NOISE_STEP_RANGE)).coerceIn(0, 255)
                last = current

                // Noise is generated at full intensity; the UI layer (Compose) 
                // handles the final opacity and blending with the base colour.
                val v = current
                val argb = (0xFF shl 24) or (v shl 16) or (v shl 8) or v
                pixels[y * width + x] = argb
            }
        }

        Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }
}