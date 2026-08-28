package com.example.brushedmetalcreator.ui.feature_editor.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brushedmetalcreator.ui.feature_editor.SwipeMenuItem
import com.example.brushedmetalcreator.ui.theme.BrushedMetalTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenuItemBox(
    item: SwipeMenuItem,
    isResetItem: Boolean,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    onResetAll: () -> Unit,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 5f / 4f
) {
    var isPressed by remember { mutableStateOf(false) }

    val backgroundColor = when {
        isResetItem && isPressed -> BrushedMetalTheme.colors.menuBackgroundResetPressed
        isResetItem -> BrushedMetalTheme.colors.menuBackgroundReset
        item.isActive -> BrushedMetalTheme.colors.menuBackgroundActive
        else -> BrushedMetalTheme.colors.menuBackgroundDefault
    }

    val borderColor = when {
        isResetItem && isPressed -> BrushedMetalTheme.colors.menuValueResetPressed
        isResetItem -> BrushedMetalTheme.colors.menuValueReset
        item.isActive -> BrushedMetalTheme.colors.menuValueActive
        else -> BrushedMetalTheme.colors.menuValueDefault
    }

    val textColor = when {
        isResetItem -> BrushedMetalTheme.colors.menuLabelReset
        item.isActive -> BrushedMetalTheme.colors.textPrimary
        else -> BrushedMetalTheme.colors.textPrimary
    }

    Box(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(aspectRatio)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = {
                    if (isResetItem) {
                        isPressed = true
                        onReset()
                    } else {
                        onToggle()
                    }
                },
                onLongClick = {
                    if (isResetItem) {
                        isPressed = true
                        onResetAll()
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.label,
                color = textColor,
                fontSize = 11.sp,
                lineHeight = 12.sp,
                textAlign = TextAlign.Center,
                softWrap = true,
                maxLines = 3,
                fontWeight = if (item.isActive) FontWeight.Bold else FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(300.milliseconds)
            isPressed = false
        }
    }
}