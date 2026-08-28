package com.example.brushedmetalcreator.ui.feature_editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brushedmetalcreator.ui.theme.BrushedMetalTheme
import androidx.compose.ui.tooling.preview.Preview
import com.example.brushedmetalcreator.ui.theme.BrushedMetalCreatorTheme

/**
 * A full-screen interaction zone that captures vertical drag gestures.
 * When an item is selected from the HUD, this component handles the input 
 * for adjusting that item's specific parameter.
 */
@Composable
fun SwipeZone(
    isActive: Boolean,
    activeLabel: String?,
    displayList: List<String>,
    currentIndex: Int,
    onDrag: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.8f)
                // Gesture detection is only active when an item is selected.
                .pointerInput(isActive) {
                    if (isActive) {
                        detectVerticalDragGestures { _, dragAmount ->
                            onDrag(dragAmount)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (isActive && activeLabel != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    displayList.forEachIndexed { index, label ->
                        val isCurrent = index == currentIndex
                        Box(
                            modifier = Modifier
                                // Staggered UI effect: 
                                // The central active item is wider than the peek values.
                                .fillMaxWidth(if (isCurrent) 0.9f else 0.7f)
                                .background(
                                    color = BrushedMetalTheme.colors.menuBackgroundDefault,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = BrushedMetalTheme.colors.textPrimary,
                                fontSize = if (isCurrent) 24.sp else 16.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                softWrap = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun SwipeZoneActivePreview() {
    BrushedMetalCreatorTheme {
        SwipeZone(
            isActive = true,
            activeLabel = "Intensity",
            displayList = listOf("Low", "Medium", "High"),
            currentIndex = 1,
            onDrag = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun SwipeZoneLongNamePreview() {
    BrushedMetalCreatorTheme {
        SwipeZone(
            isActive = true,
            activeLabel = "Base Metal",
            displayList = listOf("Stainless Steel", "Black Stainless", "Cool Alloy"),
            currentIndex = 1,
            onDrag = {}
        )
    }
}
