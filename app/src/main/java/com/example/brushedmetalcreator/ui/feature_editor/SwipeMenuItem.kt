package com.example.brushedmetalcreator.ui.feature_editor

data class SwipeMenuItem(
    val id: Int,
    val label: String,
    val value: Int = 50,
    val isActive: Boolean = false
)