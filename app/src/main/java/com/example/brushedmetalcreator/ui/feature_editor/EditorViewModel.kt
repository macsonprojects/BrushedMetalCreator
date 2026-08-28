package com.example.brushedmetalcreator.ui.feature_editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brushedmetalcreator.data.repository.*
import com.example.brushedmetalcreator.domain.engine.GradientType
import com.example.brushedmetalcreator.domain.engine.TextureEngine
import com.example.brushedmetalcreator.domain.engine.TextureParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * Central coordinator for the Editor feature.
 * Manages the UI state, handles user interactions (drags, taps, exports),
 * and maintains the reactive pipeline that triggers texture regeneration
 * whenever parameters change.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class EditorViewModel @Inject constructor(
    private val exportRepository: ImageExportRepository,
    userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState().syncWithParams())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    // Pipeline flow for parameter updates
    private val _paramsFlow = MutableStateFlow(_uiState.value.params)

    private var dragAccumulator = 0f

    init {
        // Observe DataStore custom colours and append to DEFAULT_METAL_COLORS
        userSettingsRepository.customColorsFlow
            .onEach { customMetals ->
                val combinedList = DEFAULT_METAL_COLORS + customMetals
                _uiState.update { currentState ->
                    val safeIndex = currentState.colorIndex.coerceIn(0, combinedList.lastIndex)
                    currentState.copy(
                        colorList = combinedList,
                        colorIndex = safeIndex
                    )
                }
            }
            .launchIn(viewModelScope)

        observeParamChanges()
    }

    // Sets up a reactive pipeline for parameter changes.
    private fun observeParamChanges() {
        _paramsFlow
            // Prevent spamming the CPU during continuous slider movement
            .debounce(DEBOUNCE_TIME_MS.milliseconds)
            // Only regenerate the noise bitmap if dimensions change
            .distinctUntilChanged { old, new ->
                old.width == new.width && old.height == new.height
            }
            .onEach { params ->
                generateTexture(params)
            }
            .launchIn(viewModelScope)
    }

    fun exportTexture(format: ExportFormat, capturedBitmap: android.graphics.Bitmap? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isExporting = true, exportMessage = null) }

            if (capturedBitmap == null) {
                _uiState.update { it.copy(isExporting = false, exportMessage = "Export failed: Capture error") }
                return@launch
            }

            val fileName = "brushed_metal_${System.currentTimeMillis()}"
            val result = exportRepository.exportImage(capturedBitmap, format, fileName)

            result.onSuccess {
                _uiState.update { currentState ->
                    currentState.copy(
                        isExporting = false,
                        exportMessage = "Saved to Pictures/BrushedMetal!"
                    )
                }
            }.onFailure { error ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isExporting = false,
                        exportMessage = "Export failed: ${error.localizedMessage}"
                    )
                }
            }
        }
    }

    fun clearExportMessage() {
        _uiState.update { it.copy(exportMessage = null) }
    }

    fun toggleItem(itemId: Int) {
        // Reset counter when switching items
        dragAccumulator = 0f
        _uiState.update { currentState ->
            // 1. Determine the next gradient type explicitly
            val nextGradient = when (itemId) {
                ITEM_LINEAR_GRADIENT -> GradientType.Linear
                ITEM_RADIAL_GRADIENT -> GradientType.Radial
                else -> currentState.gradientType
            }
            // 2. Update the active states for all items
            val updatedItems = currentState.items.map { item ->
                if (item.id == itemId) {
                    // Linear is tap-only; Radial is an active swipe
                    val isActionOnly = itemId == ITEM_LINEAR_GRADIENT
                    item.copy(isActive = if (isActionOnly) false else !item.isActive)
                } else {
                    // Deactivate all other items
                    item.copy(isActive = false)
                }
            }
            // Return the combined new state
            currentState.copy(
                items = updatedItems,
                gradientType = nextGradient
            )
        }
    }

    fun resetItem(resetAll: Boolean) {
        if (resetAll) {
            val currentWidth = _uiState.value.params.width
            val currentHeight = _uiState.value.params.height
            val currentColorList = _uiState.value.colorList

            _uiState.update {
                val newState = EditorUiState(colorList = currentColorList)
                newState.copy(
                    params = newState.params.copy(
                        width = currentWidth,
                        height = currentHeight
                    )
                ).syncWithParams()
            }
            _paramsFlow.value = _uiState.value.params
            generateTexture(_uiState.value.params)
        } else {
            _uiState.update { currentState ->
                // Deactivate active item without resetting all parameters
                val updatedItems = currentState.items.map { it.copy(isActive = false) }
                currentState.copy(items = updatedItems)
            }
        }
    }

    fun updateIntensity(newIntensity: Float) {
        _uiState.update { currentState ->
            val updatedParams = currentState.params.copy(intensity = newIntensity.coerceIn(0f, 1f))
            currentState.copy(params = updatedParams).syncWithParams()
        }
        _paramsFlow.value = _uiState.value.params
    }

    fun updateHighlight(newHighlight: Float) {
        _uiState.update { currentState ->
            val updatedParams = currentState.params.copy(highlightStrength = newHighlight.coerceIn(0f, 1f))
            currentState.copy(params = updatedParams).syncWithParams()
        }
        _paramsFlow.value = _uiState.value.params
    }

    fun updateShadow(newShadow: Float) {
        _uiState.update { currentState ->
            val updatedParams = currentState.params.copy(shadowStrength = newShadow.coerceIn(0f, 1f))
            currentState.copy(params = updatedParams).syncWithParams()
        }
        _paramsFlow.value = _uiState.value.params
    }

    fun updateFalloff(newFalloff: Float) {
        _uiState.update { currentState ->
            val updatedParams = currentState.params.copy(falloffExponent = newFalloff.coerceIn(0f, 1f))
            currentState.copy(params = updatedParams).syncWithParams()
        }
        _paramsFlow.value = _uiState.value.params
    }

    fun updateRadialRadius(newRatio: Float) {
        _uiState.update { currentState ->
            val updatedParams = currentState.params.copy(radialRadiusRatio = newRatio.coerceIn(0f, 1f))
            currentState.copy(params = updatedParams).syncWithParams()
        }
        _paramsFlow.value = _uiState.value.params
    }

    fun cycleColor(forward: Boolean = true) {
        _uiState.update { currentState ->
            val total = currentState.colorList.size
            val nextIndex = if (forward) {
                (currentState.colorIndex + 1) % total
            } else {
                (currentState.colorIndex - 1 + total) % total
            }
            val selectedColor = currentState.colorList[nextIndex].color

            // Convert Compose Colour to ARGB Int for TextureParams
            val argb = (selectedColor.alpha * 255).toInt() shl 24 or
                    ((selectedColor.red * 255).toInt() shl 16) or
                    ((selectedColor.green * 255).toInt() shl 8) or
                    (selectedColor.blue * 255).toInt()

            currentState.copy(
                colorIndex = nextIndex,
                params = currentState.params.copy(baseColorArgb = argb)
            ).syncWithParams()
        }
        _paramsFlow.value = _uiState.value.params
    }

    // Handles vertical drag gestures from the [SwipeZone].
    fun onDrag(dragAmount: Float) {
        val activeItem = _uiState.value.items.firstOrNull { it.isActive } ?: return
        val delta = -dragAmount * DRAG_SENSITIVITY_SLIDER

        when (activeItem.id) {
            ITEM_HIGHLIGHT -> {
                val current = _uiState.value.params.highlightStrength
                updateHighlight(current + delta)
            }
            ITEM_SHADOW -> {
                val current = _uiState.value.params.shadowStrength
                updateShadow(current + delta)
            }
            ITEM_FALLOFF -> {
                val current = _uiState.value.params.falloffExponent
                updateFalloff(current + delta)
            }
            ITEM_GRAIN_INTENSITY -> {
                val current = _uiState.value.params.intensity
                updateIntensity(current + delta)
            }
            ITEM_RADIAL_GRADIENT -> {
                val current = _uiState.value.params.radialRadiusRatio
                updateRadialRadius(current + delta)
            }
            ITEM_BASE_METAL -> {
                // Uses an accumulator to provide "clicky" feedback based on drag distance.
                dragAccumulator += dragAmount
                // Total pixels moved to trigger one colour change
                val threshold = DRAG_THRESHOLD_COLOR

                if (kotlin.math.abs(dragAccumulator) >= threshold) {
                    cycleColor(forward = dragAccumulator < 0)
                    // Reset for the next "click"
                    dragAccumulator = 0f
                }
            }
        }
    }

    private fun generateTexture(params: TextureParams) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }

            val bitmap = TextureEngine.generateDirectionalNoise(params)

            _uiState.update {
                it.copy(
                    generatedBitmap = bitmap,
                    isGenerating = false
                )
            }
        }
    }

    fun updateTextureDimensions(widthPx: Int, heightPx: Int) {
        if (widthPx <= 0 || heightPx <= 0) return
        if (_uiState.value.params.width == widthPx && _uiState.value.params.height == heightPx) return

        _uiState.update { currentState ->
            currentState.copy(
                params = currentState.params.copy(
                    width = widthPx,
                    height = heightPx
                )
            )
        }
        _paramsFlow.value = _uiState.value.params
    }
}