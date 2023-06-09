package com.alberto.recipegram.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle

@SuppressLint("ConflictingOnColor")
private val DarkColorScheme = darkColors(
    background = Black,
    primary = BrightOrange,
    onPrimary = White,
    secondary = BrightOrange,
    surface = Black,
    onBackground = White
)

@SuppressLint("ConflictingOnColor")
private val LightColorScheme = lightColors(
    background = White,
    onBackground = Black,
    primary = BrightOrange,
    onPrimary = White,
    secondary = BrightOrange,
    onSecondary = White,
    surface = White

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun RSRecetasTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val textFieldColor = TextFieldDefaults.textFieldColors(
        textColor = MaterialTheme.colors.onBackground
    )

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = {
            ProvideTextStyle(value = TextStyle(color = colors.onBackground)) {
                content()
            }
        }
    )
}