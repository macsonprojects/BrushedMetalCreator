package com.example.brushedmetalcreator.ui.feature_settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brushedmetalcreator.data.repository.UserSettingsRepository
import com.example.brushedmetalcreator.ui.feature_editor.MetalColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: UserSettingsRepository
) : ViewModel() {

    val customColors: StateFlow<List<MetalColor>> = repository.customColorsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveCustomColor(slotIndex: Int, color: Color) {
        viewModelScope.launch {
            repository.updateCustomColor(slotIndex, color.toArgb())
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            repository.clearAllCustomColors()
        }
    }
}
