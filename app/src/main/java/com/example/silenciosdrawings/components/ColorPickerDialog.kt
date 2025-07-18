package com.example.silenciosdrawings.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.silenciosdrawings.viewmodels.DrawingViewModel
import kotlin.math.*

@Composable
fun ColorPickerDialog(
    viewModel: DrawingViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedColor by remember { mutableStateOf(state.currentColor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick a color:") },
        text = {
            Column {
                Text("Basic colors:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))

                BasicColorPalette(
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Personalized color:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))

                ColorWheel(
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Selected color: ")
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(selectedColor)
                            .border(2.dp, Color.Gray, CircleShape)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.changeColor(selectedColor)
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun BasicColorPalette(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val basicColors = listOf(
        Color.Black, Color.White, Color.Red, Color.Green,
        Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta,
        Color(0xFFFF9800), // Orange
        Color(0xFF9C27B0), // Purple
        Color(0xFF795548), // Brown
        Color(0xFF607D8B), // Blue Grey
        Color(0xFFE91E63), // Pink
        Color(0xFF4CAF50), // Light Green
        Color(0xFF2196F3), // Light Blue
        Color(0xFFFF5722)  // Deep Orange
    )

    LazyVerticalGrid(
        columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(8),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.height(80.dp)
    ) {
        items(basicColors.size) { index ->
            val color = basicColors[index]
            val isSelected = selectedColor == color

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) Color.Black else Color.Gray,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

@Composable
private fun ColorWheel(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var center by remember { mutableStateOf(Offset.Zero) }
    var radius by remember { mutableStateOf(0f) }

    Canvas(
        modifier = modifier
            .size(200.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val distance = sqrt(
                        (offset.x - center.x).pow(2) + (offset.y - center.y).pow(2)
                    )
                    if (distance <= radius) {
                        val color = getColorFromPosition(offset, center, radius)
                        onColorSelected(color)
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val distance = sqrt(
                            (offset.x - center.x).pow(2) + (offset.y - center.y).pow(2)
                        )
                        if (distance <= radius) {
                            val color = getColorFromPosition(offset, center, radius)
                            onColorSelected(color)
                        }
                    },
                    onDrag = { change, _ ->
                        val distance = sqrt(
                            (change.position.x - center.x).pow(2) +
                                    (change.position.y - center.y).pow(2)
                        )
                        if (distance <= radius) {
                            val color = getColorFromPosition(change.position, center, radius)
                            onColorSelected(color)
                        }
                    }
                )
            }
    ) {
        center = Offset(size.width / 2, size.height / 2)
        radius = size.minDimension / 2

        drawColorWheel(center, radius)
    }
}

private fun DrawScope.drawColorWheel(center: Offset, radius: Float) {
    val steps = 360
    val stepAngle = 360f / steps

    for (i in 0 until steps) {
        val startAngle = i * stepAngle
        val hue = startAngle
        val color = Color.hsv(hue, 1f, 1f)

        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = stepAngle,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )
    }

    for (i in 0..100 step 5) {
        val saturation = 1f - (i / 100f)
        val alpha = (i / 100f) * 0.5f
        val whiteColor = Color.White.copy(alpha = alpha)

        drawCircle(
            color = whiteColor,
            radius = radius * saturation,
            center = center
        )
    }
}

private fun getColorFromPosition(position: Offset, center: Offset, radius: Float): Color {
    val dx = position.x - center.x
    val dy = position.y - center.y
    val distance = sqrt(dx * dx + dy * dy)

    if (distance > radius) return Color.Black

    val angle = (atan2(dy, dx) * 180 / PI).let {
        if (it < 0) it + 360 else it
    }

    val saturation = (distance / radius).coerceIn(0f, 1f)

    val brightness = 1f

    return Color.hsv(
        hue = angle.toFloat(),
        saturation = saturation,
        value = brightness
    )
}