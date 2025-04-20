package com.example.finalproject.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Green200 = Color(0xFF81C784)
val Green500 = Color(0xFF4CAF50)
val Green700 = Color(0xFF388E3C)

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)

val Teal200 = Color(0xFF03DAC5)

val DarkGray = Color(0xFF212121) // Dark background color

// Override the default colors
val LightColorScheme = lightColorScheme(
    primary = Green500,
    onPrimary = Color.White,
    secondary = Green200,
    onSecondary = Color.Black,
    background = Color.White,
    onBackground = DarkGray,
    surface = Color.White,
    onSurface = DarkGray,
    error = Color.Red,
    onError = Color.White
)

val DarkColorScheme = darkColorScheme(
    primary = Green500,
    onPrimary = Color.Black,
    secondary = Green200,
    onSecondary = Color.Black,
    background = DarkGray,
    onBackground = Color.White,
    surface = DarkGray,
    onSurface = Color.White,
    error = Color.Red,
    onError = Color.Black
)

@Composable
fun FinalProjectTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
