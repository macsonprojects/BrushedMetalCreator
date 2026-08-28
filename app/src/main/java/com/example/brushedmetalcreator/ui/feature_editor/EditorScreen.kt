package com.example.brushedmetalcreator.ui.feature_editor

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.brushedmetalcreator.data.repository.ExportFormat
import com.example.brushedmetalcreator.ui.theme.BrushedMetalCreatorTheme

@Composable
fun EditorScreen(
    onNavigateToAbout: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditorScreenContent(
        uiState = uiState,
        onNavigateToAbout = onNavigateToAbout,
        onNavigateToSettings = onNavigateToSettings,
        onToggleItem = viewModel::toggleItem,
        onResetItem = viewModel::resetItem,
        onDrag = viewModel::onDrag,
        onExport = viewModel::exportTexture,
        onClearExportMessage = viewModel::clearExportMessage,
        onUpdateTextureDimensions = viewModel::updateTextureDimensions
    )
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun EditorScreenContent(
    uiState: EditorUiState,
    onNavigateToAbout: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onToggleItem: (Int) -> Unit,
    onResetItem: (Boolean) -> Unit,
    onDrag: (Float) -> Unit,
    onExport: (ExportFormat, android.graphics.Bitmap?) -> Unit,
    onClearExportMessage: () -> Unit,
    onUpdateTextureDimensions: (Int, Int) -> Unit
) {
    val activity = LocalActivity.current
    val windowInfo = androidx.compose.ui.platform.LocalWindowInfo.current
    val density = LocalDensity.current

    // Extract exact pixel dimensions from WindowInfo
    val screenWidthPx = windowInfo.containerSize.width
    val screenHeightPx = windowInfo.containerSize.height

    // Calculate Dp for WindowSizeClass fallback
    val screenWidthDp = density.run { screenWidthPx.toDp() }
    val screenHeightDp = density.run { screenHeightPx.toDp() }

    val windowSizeClass = if (activity != null) {
        calculateWindowSizeClass(activity)
    } else {
        WindowSizeClass.calculateFromSize(
            DpSize(screenWidthDp, screenHeightDp)
        )
    }

    // Notify ViewModel whenever screen container size changes
    LaunchedEffect(screenWidthPx, screenHeightPx) {
        onUpdateTextureDimensions(screenWidthPx, screenHeightPx)
    }

    EditorContent(
        uiState = uiState,
        windowSizeClass = windowSizeClass,
        onToggleItem = onToggleItem,
        onResetItem = onResetItem,
        onDrag = onDrag,
        onExport = onExport,
        onClearExportMessage = onClearExportMessage,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToAbout = onNavigateToAbout
    )
}

@Preview(name = "Portrait Phone", showBackground = true, widthDp = 540, heightDp = 1200)
@Composable
fun EditorScreenPreview() {
    BrushedMetalCreatorTheme {
        EditorScreenContent(
            uiState = EditorUiState(),
            onNavigateToAbout = {},
            onNavigateToSettings = {},
            onToggleItem = {},
            onResetItem = {},
            onDrag = {},
            onExport = { _, _ -> },
            onClearExportMessage = {},
            onUpdateTextureDimensions = { _, _ -> }
        )
    }
}
