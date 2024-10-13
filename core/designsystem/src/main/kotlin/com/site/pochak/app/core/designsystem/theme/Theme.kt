package com.site.pochak.app.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

/**
 * Light default theme color scheme
 */
val LightColorScheme = lightColorScheme(
    primary = Yellow01,
    onPrimary = Color.White,
    secondary = Navy03,
    onSecondary = Color.Black
)

/**
 * Dark default theme color scheme
 *
 * 추가 예정
 */
// TODO: 다크 테마 디자인 추가 후, 적용
val DarkColorScheme: ColorScheme = LightColorScheme

@Composable
fun PochakTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        // Gradient Color, Background Color, etc.
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PochakTypography,
            content = content
        )
    }
}