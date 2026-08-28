package com.example.brushedmetalcreator.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.brushedmetalcreator.ui.feature_editor.MetalColor
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

interface UserSettingsRepository {
    val customColorsFlow: Flow<List<MetalColor>>
    suspend fun updateCustomColor(slotIndex: Int, colorInt: Int)
    suspend fun clearAllCustomColors()
}

class UserSettingsRepositoryImpl(
    private val context: Context
) : UserSettingsRepository {

    private val customColor1Key = intPreferencesKey("custom_color_1")
    private val customColor2Key = intPreferencesKey("custom_color_2")
    private val customColor3Key = intPreferencesKey("custom_color_3")

    // Default fallbacks if user hasn't set custom colours yet
    private val defaultCustom1 = 0xFFA66A4C.toInt() // Antique Copper
    private val defaultCustom2 = 0xFFB08D57.toInt() // Burnished Gold
    private val defaultCustom3 = 0xFF665546.toInt() // Oil-Rubbed Bronze

    override val customColorsFlow: Flow<List<MetalColor>> = context.dataStore.data
        .map { prefs ->
            val c1 = prefs[customColor1Key] ?: defaultCustom1
            val c2 = prefs[customColor2Key] ?: defaultCustom2
            val c3 = prefs[customColor3Key] ?: defaultCustom3

            listOf(
                MetalColor("CUSTOM 1", Color(c1)),
                MetalColor("CUSTOM 2", Color(c2)),
                MetalColor("CUSTOM 3", Color(c3))
            )
        }

    override suspend fun updateCustomColor(slotIndex: Int, colorInt: Int) {
        context.dataStore.edit { prefs ->
            when (slotIndex) {
                0 -> prefs[customColor1Key] = colorInt
                1 -> prefs[customColor2Key] = colorInt
                2 -> prefs[customColor3Key] = colorInt
            }
        }
    }

    override suspend fun clearAllCustomColors() {
        context.dataStore.edit { prefs ->
            prefs.remove(customColor1Key)
            prefs.remove(customColor2Key)
            prefs.remove(customColor3Key)
        }
    }
}
