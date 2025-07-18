package com.example.silenciosdrawings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.silenciosdrawings.viewmodels.DrawingViewModel

@Composable
fun ColorPalette(
    viewModel: DrawingViewModel,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color.Red,
        Color.Blue,
        Color.Green,
        Color(0xFFFF9800),
        Color(0xFF9C27B0)
    )
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        colors.forEach { color ->

            val isSelected = state.currentColor == color && !state.isErasing

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (isSelected) 4.dp else 2.dp,
                        color = if (isSelected) Color.Black else Color.Gray,
                        shape = CircleShape
                    )
                    .clickable { viewModel.changeColor(color) }
            )
        }
    }
}