package com.example.silenciosdrawings.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageUtils {
    fun saveBitmapToFile(bitmap: Bitmap, file: File): Boolean {
        return try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()

            FileOutputStream(file).use { fos ->
                fos.write(byteArray)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun createBitmapFromCanvas(width: Int, height: Int, draw: (Canvas) -> Unit): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    fun imageBitmapToBitmap(imageBitmap: ImageBitmap): Bitmap {
        val androidBitmap = imageBitmap.asAndroidBitmap()

        val bitmap = androidBitmap.copy(Bitmap.Config.ARGB_8888, true)

        return bitmap
    }
}