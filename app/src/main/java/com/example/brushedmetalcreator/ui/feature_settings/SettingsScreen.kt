package com.example.brushedmetalcreator.ui.feature_settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.brushedmetalcreator.ui.feature_editor.MetalColor
import com.example.brushedmetalcreator.ui.theme.BrushedMetalCreatorTheme
import com.example.brushedmetalcreator.ui.theme.BrushedMetalTheme
import com.example.brushedmetalcreator.utilities.parseHexColor

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val customColors by viewModel.customColors.collectAsStateWithLifecycle()

    SettingsScreenContent(
        customColors = customColors,
        onNavigateBack = onNavigateBack,
        onSaveCustomColor = { index, color ->
            viewModel.saveCustomColor(index, color)
        },
        onResetToDefaults = {
            viewModel.resetToDefaults()
        }
    )
}

@Composable
private fun SettingsScreenContent(
    customColors: List<MetalColor>,
    onNavigateBack: () -> Unit,
    onSaveCustomColor: (Int, Color) -> Unit,
    onResetToDefaults: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BrushedMetalTheme.colors.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate Back",
                        tint = BrushedMetalTheme.colors.textPrimary
                    )
                }
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = BrushedMetalTheme.colors.textPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Custom Metal Colors",
                    style = MaterialTheme.typography.titleMedium,
                    color = BrushedMetalTheme.colors.textPrimary
                )
                Text(
                    text = "Enter 6-character hex codes (e.g. C0C0C0) to define your custom metal tints.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrushedMetalTheme.colors.iconInactive
                )

                Spacer(modifier = Modifier.height(16.dp))

                customColors.forEachIndexed { index, metalColor ->
                    CustomColorHexInputRow(
                        label = metalColor.name,
                        initialColor = metalColor.color,
                        onColorSaved = { newColor ->
                            onSaveCustomColor(index, newColor)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val buttonShape = RoundedCornerShape(4.dp)
                Button(
                    onClick = onResetToDefaults,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.White,
                            shape = buttonShape
                        ),
                    shape = buttonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrushedMetalTheme.colors.menuBackgroundDefault,
                        contentColor = BrushedMetalTheme.colors.textPrimary
                    )
                ) {
                    Text("Reset to Defaults")
                }
            }
        }
    }
}

@Composable
private fun CustomColorHexInputRow(
    label: String,
    initialColor: Color,
    onColorSaved: (Color) -> Unit
) {
    var hexText by remember(initialColor) {
        val argb = initialColor.toArgb()
        mutableStateOf(String.format("%06X", argb and 0xFFFFFF))
    }

    val parsedColor = parseHexColor(hexText)
    val isValid = parsedColor != null

    val colorBoxShape = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = parsedColor ?: Color.DarkGray,
                    shape = colorBoxShape
                )
                .border(
                    width = 1.dp,
                    color = if (isValid) BrushedMetalTheme.colors.textPrimary.copy(alpha = 0.5f) else Color.Red,
                    shape = colorBoxShape
                )
        )

        OutlinedTextField(
            value = hexText,
            onValueChange = { input ->
                if (input.length <= 8) {
                    hexText = input.uppercase()
                    parseHexColor(input)?.let { validColor ->
                        onColorSaved(validColor)
                    }
                }
            },
            label = { Text(label, color = BrushedMetalTheme.colors.textSecondary) },
            prefix = { Text("0xFF", color = BrushedMetalTheme.colors.iconInactive) },
            singleLine = true,
            isError = !isValid && hexText.isNotEmpty(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = BrushedMetalTheme.colors.textPrimary,
                unfocusedTextColor = BrushedMetalTheme.colors.textPrimary,
                focusedBorderColor = BrushedMetalTheme.colors.textPrimary,
                unfocusedBorderColor = BrushedMetalTheme.colors.iconInactive,
                errorBorderColor = Color.Red
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val sampleColors = listOf(
        MetalColor("CUSTOM 1", Color(0xFFA66A4C)),
        MetalColor("CUSTOM 2", Color(0xFFB08D57)),
        MetalColor("CUSTOM 3", Color(0xFF665546))
    )
    BrushedMetalCreatorTheme {
        SettingsScreenContent(
            customColors = sampleColors,
            onNavigateBack = {},
            onSaveCustomColor = { _, _ -> },
            onResetToDefaults = {}
        )
    }
}
