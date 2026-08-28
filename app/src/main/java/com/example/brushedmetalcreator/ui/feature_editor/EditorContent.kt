package com.example.brushedmetalcreator.ui.feature_editor

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brushedmetalcreator.data.repository.ExportFormat
import com.example.brushedmetalcreator.ui.feature_editor.components.SwipeZone
import com.example.brushedmetalcreator.ui.feature_editor.components.MenuItemBox
import com.example.brushedmetalcreator.ui.feature_editor.components.TexturePreview
import com.example.brushedmetalcreator.ui.theme.BrushedMetalCreatorTheme
import com.example.brushedmetalcreator.ui.theme.BrushedMetalTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val PICKER_RANGE_NUMERIC = 0
private const val PICKER_RANGE_COLOR = 1

// Define order and grouping of MenuItems
private val MENU_GROUPS: List<List<Int>> = listOf(
    listOf(ITEM_HIGHLIGHT, ITEM_SHADOW, ITEM_FALLOFF),
    listOf(ITEM_LINEAR_GRADIENT, ITEM_RADIAL_GRADIENT),
    listOf(ITEM_GRAIN_INTENSITY, ITEM_BASE_METAL),
    listOf(ITEM_RESET),
)

// Main UI layout for the Editor feature.
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun EditorContent(
    uiState: EditorUiState,
    windowSizeClass: WindowSizeClass,
    onToggleItem: (Int) -> Unit,
    onResetItem: (Boolean) -> Unit,
    onDrag: (Float) -> Unit,
    onExport: (ExportFormat, android.graphics.Bitmap?) -> Unit,
    onClearExportMessage: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val activeItem = uiState.items.firstOrNull { it.isActive }

    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)

    var showExportMenu by remember { mutableStateOf(false) }

    val (displayList, currentIndex) = if (activeItem?.id == ITEM_BASE_METAL) {
        val list = (-PICKER_RANGE_COLOR..PICKER_RANGE_COLOR).map { offset ->
            val i = (uiState.colorIndex + offset).mod(uiState.colorList.size)
            uiState.colorList[i].name
        }
        list to PICKER_RANGE_COLOR
    } else {
        val value = activeItem?.value ?: 0
        val range = if (activeItem?.id == ITEM_FALLOFF) -50..50 else 0..100
        
        val list = (-PICKER_RANGE_NUMERIC..PICKER_RANGE_NUMERIC).map { offset ->
            val v = value + (offset * 5)
            if (v in range) v.toString() else "-"
        }
        list to PICKER_RANGE_NUMERIC
    }

    // Adaptive menu size logic.
    val menuSize: Dp = when {
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
        windowSizeClass.heightSizeClass == WindowHeightSizeClass.Expanded -> 104.dp
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded -> 96.dp
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium -> 88.dp
        windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact -> 88.dp
        else -> 88.dp
    }

    @Composable
    fun EditorHeader(modifier: Modifier = Modifier) {
        val iconTint = BrushedMetalTheme.colors.iconActive
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    scope.launch { drawerState.close() }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuOpen,
                        contentDescription = "Close Drawer",
                        tint = iconTint
                    )
                }
                Text(
                    text = "Editor",
                    style = MaterialTheme.typography.titleLarge,
                    color = BrushedMetalTheme.colors.textPrimary,
                    modifier = Modifier.padding(start = 0.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Share & Export Menu
                if (uiState.isExporting) {
                    CircularProgressIndicator(
                        color = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Box {
                        IconButton(onClick = { showExportMenu = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = "Export Texture",
                                tint = iconTint
                            )
                        }

                        DropdownMenu(
                            expanded = showExportMenu,
                            onDismissRequest = { showExportMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Export as PNG") },
                                onClick = {
                                    showExportMenu = false
                                    scope.launch {
                                        val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                        onExport(ExportFormat.PNG, bitmap)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Export as JPEG") },
                                onClick = {
                                    showExportMenu = false
                                    scope.launch {
                                        val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                        onExport(ExportFormat.JPEG, bitmap)
                                    }
                                }
                            )
                        }
                    }
                }

                // Settings icon
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = iconTint
                    )
                }

                // About icon
                IconButton(onClick = onNavigateToAbout) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "About",
                        tint = iconTint
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Texture Preview
        TexturePreview(
            params = uiState.params,
            generatedBitmap = uiState.generatedBitmap,
            isGenerating = uiState.isGenerating,
            graphicsLayer = graphicsLayer,
            gradientType = uiState.gradientType,
            modifier = Modifier.fillMaxSize()
        )

        // Main Navigation Drawer
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = activeItem == null,
            scrimColor = Color.Transparent,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .fillMaxWidth(),
                    drawerContainerColor = Color.Black.copy(alpha = DRAWER_BACKGROUND_ALPHA),
                    drawerShape = androidx.compose.ui.graphics.RectangleShape
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        EditorHeader()

                        if (isLandscape) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .navigationBarsPadding()
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    SwipeZone(
                                        isActive = activeItem != null,
                                        activeLabel = activeItem?.label,
                                        displayList = displayList,
                                        currentIndex = currentIndex,
                                        onDrag = onDrag,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(menuSize)
                                        .padding(horizontal = 4.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    MENU_GROUPS.forEachIndexed { groupIndex, group ->
                                        if (groupIndex > 0) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        group.forEach { id ->
                                            MenuItemById(
                                                id = id,
                                                items = uiState.items,
                                                onToggle = onToggleItem,
                                                onReset = onResetItem,
                                                modifier = Modifier.fillMaxHeight(),
                                                aspectRatio = 1f
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .navigationBarsPadding(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(menuSize)
                                        .padding(horizontal = 4.dp, vertical = 4.dp),
                                    verticalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    MENU_GROUPS.forEachIndexed { groupIndex, group ->
                                        if (groupIndex > 0) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                        }
                                        group.forEach { id ->
                                            MenuItemById(
                                                id = id,
                                                items = uiState.items,
                                                onToggle = onToggleItem,
                                                onReset = onResetItem,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(36.dp))
                                }

                                Box(modifier = Modifier.weight(1f)) {
                                    SwipeZone(
                                        isActive = activeItem != null,
                                        activeLabel = activeItem?.label,
                                        displayList = displayList,
                                        currentIndex = currentIndex,
                                        onDrag = onDrag,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                // Information bar for Portrait mode only
                                InteractionHint()
                            }
                        }
                    }
                }
            }
        ) {
            // Background Content (Empty because TexturePreview is at root Box level,
            // and icons are overlaid on top of the drawer below)
            Box(modifier = Modifier.fillMaxSize())
        }

        // Feedback Snackbar
        uiState.exportMessage?.let { message ->
            LaunchedEffect(message) {
                delay(SNACKBAR_DURATION_MS.milliseconds)
                onClearExportMessage()
            }
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                containerColor = BrushedMetalTheme.colors.snackbarBackground
            ) {
                Text(text = message, color = BrushedMetalTheme.colors.textPrimary)
            }
        }
    }
}

@Composable
private fun MenuItemById(
    id: Int,
    items: List<SwipeMenuItem>,
    onToggle: (Int) -> Unit,
    onReset: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 5f / 4f
) {
    val item = items.firstOrNull { it.id == id } ?: return
    MenuItemBox(
        item = item,
        isResetItem = item.id == ITEM_RESET,
        onToggle = { onToggle(item.id) },
        onReset = { onReset(false) },
        onResetAll = { onReset(true) },
        modifier = modifier,
        aspectRatio = aspectRatio
    )
}
@Composable
private fun InteractionHint() {
    Box(
        modifier = Modifier
            .fillMaxHeight(0.8f)
            .width(32.dp)
            .padding(top = 16.dp, bottom = 56.dp, start = 2.dp, end = 2.dp)
            .background(BrushedMetalTheme.colors.menuBackgroundDefault, RoundedCornerShape(4.dp))
            .border(1.dp, BrushedMetalTheme.colors.menuValueDefault, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tap a Menu Item and swipe up or down to adjust values. Swipe to close this drawer",
            color = BrushedMetalTheme.colors.textPrimary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            softWrap = false,
            modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(
                    constraints.copy(
                        minWidth = constraints.minHeight,
                        maxWidth = constraints.maxHeight,
                        minHeight = constraints.minWidth,
                        maxHeight = constraints.maxWidth,
                    )
                )
                layout(placeable.height, placeable.width) {
                    placeable.placeWithLayer(
                        x = -(placeable.width / 2 - placeable.height / 2),
                        y = -(placeable.height / 2 - placeable.width / 2)
                    ) {
                        rotationZ = 90f
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Portrait Phone", showBackground = true, widthDp = 412, heightDp = 892)
@Composable
fun EditorContentPortraitPreview() {
    val sampleUiState = EditorUiState()

    BrushedMetalCreatorTheme {
        EditorContent(
            uiState = sampleUiState,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(412.dp, 892.dp)),
            onToggleItem = {},
            onResetItem = {},
            onDrag = {},
            onExport = { _, _ -> },
            onClearExportMessage = {},
            onNavigateToAbout = {},
            onNavigateToSettings = {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Landscape Phone", showBackground = true, widthDp = 892, heightDp = 412)
@Composable
fun EditorContentLandscapePreview() {
    val sampleUiState = EditorUiState()

    BrushedMetalCreatorTheme {
        EditorContent(
            uiState = sampleUiState,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(892.dp, 412.dp)),
            onToggleItem = {},
            onResetItem = {},
            onDrag = {},
            onExport = { _, _ -> },
            onClearExportMessage = {},
            onNavigateToAbout = {},
            onNavigateToSettings = {}
        )
    }
}