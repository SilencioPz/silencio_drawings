package com.example.silenciosdrawings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.silenciosdrawings.viewmodels.DrawingViewModel

@Composable
fun ToolsBar(
    viewModel: DrawingViewModel,
    modifier: Modifier = Modifier,
    onColorPickerClick: () -> Unit,
    onSaveClick: () -> Unit,
    onImportClick: () -> Unit
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    var showBrushSizeDialog by remember { mutableStateOf(false) }

    if (showBrushSizeDialog) {
        AlertDialog(
            onDismissRequest = { showBrushSizeDialog = false },
            title = { Text("Brush size") },
            text = {
                Column {
                    Text("Actual size: ${state.strokeWidth.value.toInt()}dp")
                    Spacer(modifier = Modifier.height(16.dp))

                    var sliderValue by remember { mutableStateOf(state.strokeWidth.value) }

                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 1f..50f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1dp")
                        Text("50dp")
                    }

                    LaunchedEffect(sliderValue) {
                        viewModel.changeStrokeWidth(sliderValue.dp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBrushSizeDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolButton(
                icon = Icons.Default.Undo,
                description = "Undo",
                onClick = { viewModel.undo() },
                enabled = state.paths.isNotEmpty()
            )

            ToolButton(
                icon = Icons.Default.Brush,
                description = "Brush size: ${state.strokeWidth.value.toInt()}dp",
                onClick = { showBrushSizeDialog = true }
            )

            ToolButton(
                icon = Icons.Default.Delete,
                description = "Erasure",
                onClick = { viewModel.toggleEraser() },
                isSelected = state.isErasing,
                selectedColor = Color.Red
            )

            ToolButton(
                icon = Icons.Default.Palette,
                description = "Color palette",
                onClick = { onColorPickerClick() }
            )

            ToolButton(
                icon = Icons.Default.Save,
                description = "Save",
                onClick = onSaveClick
            )

            ToolButton(
                icon = Icons.Default.ImportExport,
                description = "Import",
                onClick = onImportClick
            )
        }
    }
}

@Composable
private fun ToolButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isSelected: Boolean = false,
    selectedColor: Color = MaterialTheme.colorScheme.primary
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(48.dp)
            .then(
                if (isSelected) {
                    Modifier.background(
                        selectedColor.copy(alpha = 0.2f),
                        RoundedCornerShape(24.dp)
                    )
                } else Modifier
            )
    ) {
        Icon(
            imageVector = when(description) {
                "Undo" -> Icons.Default.Undo
                "Clean screen" -> Icons.Default.Clear
                "Brush" -> Icons.Default.Brush
                "Eraser" -> Icons.Default.Delete
                "Palette" -> Icons.Default.Palette
                "Save" -> Icons.Default.Save
                "Import" -> Icons.Default.ImportExport

                else -> Icons.Default.Brush
            },
            contentDescription = description,
            tint = if (isSelected) selectedColor else LocalContentColor.current.copy(
                alpha = if (enabled) 1f else 0.38f
            )
        )
    }
}