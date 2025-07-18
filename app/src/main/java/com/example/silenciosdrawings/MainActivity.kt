package com.example.silenciosdrawings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.silenciosdrawings.ui.theme.SilenciosDrawingsTheme
import com.example.silenciosdrawings.views.DrawingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SilenciosDrawingsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DrawingScreen(context = LocalContext.current)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DrawingScreenPreview() {
    SilenciosDrawingsTheme {
        DrawingScreen(context = LocalContext.current)
    }
}