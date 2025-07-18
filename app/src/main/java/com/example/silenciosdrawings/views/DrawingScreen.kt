package com.example.silenciosdrawings.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.silenciosdrawings.components.ToolsBar
import com.example.silenciosdrawings.viewmodels.DrawingViewModel
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import com.example.silenciosdrawings.R
import com.example.silenciosdrawings.components.ColorPickerDialog

@Composable
fun DrawingScreen(context: Context) {
    val viewModel: DrawingViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var currentPath by remember { mutableStateOf(Path()) }
    var showColorPicker by remember { mutableStateOf(false) }
    var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
    val backgroundImage: ImageBitmap? = null

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.importImage(it, context) }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.silenciopz_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "SilenciosDrawings",
                style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            viewModel.startNewPath(offset)
                        },
                        onDrag = { change, _ ->
                            viewModel.updateCurrentPath(change.position)
                        },
                        onDragEnd = {
                            viewModel.finishCurrentPath()
                        }
                    )
                }
        ) {
            canvasSize = size

            drawRect(
                color = Color.White,
                size = size
            )

            state.backgroundImage?.let { image ->
                drawImage(
                    image = image,
                    dstSize = IntSize(size.width.toInt(), size.height.toInt())
                )
            }

            state.paths.forEach { path ->
                if (path.points.isNotEmpty()) {
                    val composePath = Path().apply {
                        moveTo(path.points[0].x, path.points[0].y)
                        path.points.forEach { point ->
                            lineTo(point.x, point.y)
                        }
                    }
                    drawPath(
                        composePath,
                        color = path.color,
                        style = Stroke(width = path.strokeWidth.toPx())
                    )
                }
            }

            state.currentPath?.let { current ->
                if (current.points.isNotEmpty()) {
                    val composePath = Path().apply {
                        moveTo(current.points[0].x, current.points[0].y)
                        current.points.forEach { point ->
                            lineTo(point.x, point.y)
                        }
                    }
                    drawPath(
                        composePath,
                        color = current.color,
                        style = Stroke(width = current.strokeWidth.toPx())
                    )
                }
            }
        }

        if (showColorPicker) {
            ColorPickerDialog(
                viewModel = viewModel,
                onDismiss = { showColorPicker = false }
            )
        }

        ToolsBar(
            viewModel = viewModel,
            onColorPickerClick = { showColorPicker = true },
            onSaveClick = {
                if (canvasSize != androidx.compose.ui.geometry.Size.Zero) {
                    viewModel.saveDrawing(
                        context,
                        canvasSize.width.toInt(),
                        canvasSize.height.toInt()
                    )
                }
            },
            onImportClick = { galleryLauncher.launch("image/*") }
        )
    }

    if (showColorPicker) {
        ColorPickerDialog(
            viewModel = viewModel,
            onDismiss = { showColorPicker = false }
        )
    }
}