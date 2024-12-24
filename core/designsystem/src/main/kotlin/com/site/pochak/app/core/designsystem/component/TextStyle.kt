package com.site.pochak.app.core.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

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
object PochakTextStyle {
    val display: TextStyle
        @Composable get() = MaterialTheme.typography.displaySmall

    val head1: TextStyle
        @Composable get() = MaterialTheme.typography.headlineLarge

    val head2: TextStyle
        @Composable get() = MaterialTheme.typography.headlineMedium

    val head3: TextStyle
        @Composable get() = MaterialTheme.typography.headlineSmall

    val head4: TextStyle
        @Composable get() = MaterialTheme.typography.titleLarge

    val body0: TextStyle
        @Composable get() = MaterialTheme.typography.titleMedium

    val body1: TextStyle
        @Composable get() = MaterialTheme.typography.titleSmall

    val body2: TextStyle
        @Composable get() = MaterialTheme.typography.bodyLarge

    val body3: TextStyle
        @Composable get() = MaterialTheme.typography.bodyMedium

    val body3_1: TextStyle
        @Composable get() = MaterialTheme.typography.bodySmall

    val caption1: TextStyle
        @Composable get() = MaterialTheme.typography.labelLarge

    val caption2: TextStyle
        @Composable get() = MaterialTheme.typography.labelMedium
}
