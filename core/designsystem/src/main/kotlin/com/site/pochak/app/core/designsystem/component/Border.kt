package com.site.pochak.app.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.site.pochak.app.core.designsystem.theme.Gray02
import com.site.pochak.app.core.designsystem.theme.Yellow00

@Composable
fun Modifier.drawBottomBorder(
    enabled: Boolean = true,
    color: Color = Gray02,
    strokeWidth: Dp = 0.5.dp,
): Modifier = drawBehind {
    if (enabled) {
        val strokeWidth = strokeWidth.toPx()
        val y = size.height - strokeWidth / 2

        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = strokeWidth
        )
    }
}

@Composable
fun Modifier.drawTopBorder(
    enabled: Boolean = true,
    color: Color = Gray02,
    strokeWidth: Dp = 0.5.dp,
): Modifier = drawBehind {
    if (enabled) {
        val strokeWidth = strokeWidth.toPx()
        val y = strokeWidth / 2

        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = strokeWidth
        )
    }
}