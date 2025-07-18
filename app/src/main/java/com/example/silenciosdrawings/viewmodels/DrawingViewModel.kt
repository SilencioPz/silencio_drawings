package com.example.silenciosdrawings.viewmodels

import android.content.ContentValues
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.silenciosdrawings.models.DrawingPath
import com.example.silenciosdrawings.models.DrawingState
import com.example.silenciosdrawings.utils.ImageUtils
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DrawingViewModel : ViewModel() {
    private val _state = MutableStateFlow(DrawingState())
    val state: StateFlow<DrawingState> = _state.asStateFlow()

    fun startNewPath(offset: Offset) {
        _state.value = _state.value.copy(
            currentPath = DrawingPath(
                points = listOf(offset),
                color = if (_state.value.isErasing) Color.White else _state.value.currentColor,
                strokeWidth = _state.value.strokeWidth
            )
        )
    }

    fun updateCurrentPath(newOffset: Offset) {
        val current = _state.value.currentPath
        if (current != null) {
            _state.value = _state.value.copy(
                currentPath = current.copy(
                    points = current.points + newOffset
                )
            )
        }
    }

    fun finishCurrentPath() {
        _state.value.currentPath?.let { currentPath ->
            _state.value = _state.value.copy(
                paths = _state.value.paths + currentPath,
                currentPath = null
            )
        }
    }

    fun changeColor(color: Color) {
        _state.value = _state.value.copy(
            currentColor = color,
            isErasing = false
        )
    }

    fun changeStrokeWidth(width: Dp) {
        _state.value = _state.value.copy(strokeWidth = width)
    }

    fun toggleEraser() {
        _state.value = _state.value.copy(isErasing = !_state.value.isErasing)
    }

    fun undo() {
        if (_state.value.paths.isNotEmpty()) {
            _state.value = _state.value.copy(
                paths = _state.value.paths.dropLast(1)
            )
        }
    }

    fun clear() {
        _state.value = _state.value.copy(
            paths = emptyList(),
            currentPath = null
        )
    }

    fun saveDrawing(context: Context, width: Int, height: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("DrawingViewModel", "Initializing saving: ${width}x${height}")

                if (width <= 0 || height <= 0) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Invalid canvas dimensions", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)

                canvas.drawColor(android.graphics.Color.WHITE)

                state.value.backgroundImage?.let { image ->
                    try {
                        val backgroundBitmap = ImageUtils.imageBitmapToBitmap(image)
                        val scaledBitmap = Bitmap.createScaledBitmap(backgroundBitmap, width, height, true)
                        canvas.drawBitmap(scaledBitmap, 0f, 0f, null)
                        scaledBitmap.recycle()
                    } catch (e: Exception) {
                        Log.e("DrawingViewModel", "Error when drawing background: ${e.message}")
                    }
                }

                state.value.paths.forEach { path ->
                    if (path.points.isNotEmpty()) {
                        try {
                            val paint = Paint().apply {
                                color = path.color.toArgb()
                                strokeWidth = path.strokeWidth.value
                                style = Paint.Style.STROKE
                                isAntiAlias = true
                                strokeCap = Paint.Cap.ROUND
                                strokeJoin = Paint.Join.ROUND
                            }

                            for (i in 0 until path.points.size - 1) {
                                val start = path.points[i]
                                val end = path.points[i + 1]
                                canvas.drawLine(start.x, start.y, end.x, end.y, paint)
                            }
                        } catch (e: Exception) {
                            Log.e("DrawingViewModel", "Error when drawing path: ${e.message}")
                        }
                    }
                }

                val fileName = "SilenciosDrawing_${System.currentTimeMillis()}.png"

                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        val success = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

                        if (success) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                contentValues.clear()
                                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                                resolver.update(uri, contentValues, null, null)
                            }

                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Drawing saved successfully!", Toast.LENGTH_LONG).show()
                            }
                            Log.d("DrawingViewModel", "Image saved successfully: $fileName")
                        } else {
                            throw Exception("Failed to compress bitmap")
                        }
                    }
                } else {
                    throw Exception("Failed to create MediaStore entry")
                }

                bitmap.recycle()

            } catch (e: Exception) {
                Log.e("DrawingViewModel", "Complete error when saving: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error saving: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun importImage(uri: Uri, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                    if (bitmap != null) {
                        val imageBitmap = bitmap.asImageBitmap()
                        _state.value = _state.value.copy(
                            backgroundImage = imageBitmap
                        )
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Image imported successfully!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Failed to decode image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("DrawingViewModel", "Error when importing image: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error importing image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}