package com.example.ethan.ui.gui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
        primary = Purple200,
        primaryVariant = Purple700,
        secondary = Teal200
)

private val LightColorPalette = lightColors(
        primary = Purple500,
        primaryVariant = Purple700,
        secondary = Teal200

        /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun ETHANTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
    )
}
class MyClass{
    fun foo(a: Int): Int {
        var b = a + 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        b += 1
        return b
    }
}