package com.site.pochak.app.core.designsystem.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.site.pochak.app.core.designsystem.R

private val Pretendard = FontFamily(
    Font(R.font.pretendard_thin, weight = FontWeight.Thin),
    Font(R.font.pretendard_extra_light, weight = FontWeight.ExtraLight),
    Font(R.font.pretendard_light, weight = FontWeight.Light),
    Font(R.font.pretendard_regular, weight = FontWeight.Normal),
    Font(R.font.pretendard_medium, weight = FontWeight.Medium),
    Font(R.font.pretendard_semi_bold, weight = FontWeight.SemiBold),
    Font(R.font.pretendard_bold, weight = FontWeight.Bold),
    Font(R.font.pretendard_extra_bold, weight = FontWeight.ExtraBold),
    Font(R.font.pretendard_black, weight = FontWeight.Black),
)

/**
 * Pochak typography.
 *
 * display: Bold, 26px, 30px, 0px       -> displaySmall
 * head1: Bold, 22px, 28px, 0px         -> headlineLarge
 * head2: Medium, 22px, 28px, 0px       -> headlineMedium
 * head3: Bold, 20px, 28px, 0px         -> headlineSmall
 * head4: Medium, 20px, 28px, 0px       -> titleLarge
 * body0: Bold, 18px, 24px, 0px         -> titleMedium
 * body1: Bold, 16px, 22px, 0px         -> titleSmall
 * body2: Medium, 16px, 22px, 0px       -> bodyLarge
 * body3: Regular, 14px, 20px, 0px      -> bodyMedium
 * body3-1: Bold, 14px, 20px, 0px       -> bodySmall
 * caption1: Bold, 12px, 16px, 0px      -> labelLarge
 * caption2: Medium, 12px, 16px, 0px    -> labelMedium
 *
 */
object PochakTypography {
    data class FontInfo(
        val fontFamily: FontFamily = Pretendard,
        val fontWeight: FontWeight,
        val fontSize: Float,
        val lineHeight: Float,
        val letterSpacing: Float = 0f
    ) {
        fun toTextStyle() = TextStyle(
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = fontSize.sp,
            lineHeight = lineHeight.sp,
            letterSpacing = letterSpacing.sp
        )

        fun toTextStyleDp(density: Density) = with(density) {
            TextStyle(
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                fontSize = fontSize.dp.toSp(),
                lineHeight = lineHeight.dp.toSp(),
                letterSpacing = letterSpacing.dp.toSp()
            )
        }
    }

    val displaySmall = FontInfo(
        fontWeight = FontWeight.Bold,
        fontSize = 26f,
        lineHeight = 30f,
    )
    val headlineLarge = FontInfo(
        fontWeight = FontWeight.Bold,
        fontSize = 22f,
        lineHeight = 28f,
    )
    val headlineMedium = FontInfo(
        fontWeight = FontWeight.Medium,
        fontSize = 22f,
        lineHeight = 28f,
    )
    val headlineSmall = FontInfo(
        fontWeight = FontWeight.Bold,
        fontSize = 20f,
        lineHeight = 28f,
    )
    val titleLarge = FontInfo(
        fontWeight = FontWeight.Medium,
        fontSize = 20f,
        lineHeight = 28f,
    )
    val titleMedium = FontInfo(
        fontWeight = FontWeight.Bold,
        fontSize = 18f,
        lineHeight = 24f,
        letterSpacing = 0.1f
    )
    val titleSmall = FontInfo(
        fontWeight = FontWeight.Bold,
        fontSize = 16f,
        lineHeight = 22f,
    )
    val bodyLarge = FontInfo(
        fontWeight = FontWeight.Medium,
        fontSize = 16f,
        lineHeight = 22f,
    )
    val bodyMedium = FontInfo(
        fontWeight = FontWeight.Normal,
        fontSize = 14f,
        lineHeight = 20f,
    )
    val bodySmall = FontInfo(
        fontWeight = FontWeight.Bold,
        fontSize = 14f,
        lineHeight = 20f,
    )
    val labelLarge = FontInfo(
        fontWeight = FontWeight.Bold,
        fontSize = 12f,
        lineHeight = 16f,
    )
    val labelMedium = FontInfo(
        fontWeight = FontWeight.Medium,
        fontSize = 12f,
        lineHeight = 16f,
    )
}
