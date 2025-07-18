package com.example.silenciosdrawings.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DrawingPath(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Dp
)

data class DrawingState(
    val paths: List<DrawingPath> = emptyList(),
    val currentPath: DrawingPath? = null,
    val currentColor: Color = Color.Black,
    val strokeWidth: Dp = 5.dp,
    val isErasing: Boolean = false,
    val backgroundImage: ImageBitmap? = null
)