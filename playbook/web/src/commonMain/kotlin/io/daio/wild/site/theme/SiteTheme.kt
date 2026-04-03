// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.daio.wild.content.ProvidesContentColor

@Immutable
data class SiteTypography(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,
    val code: TextStyle,
    val label: TextStyle,
)

val DefaultSiteTypography =
    SiteTypography(
        h1 = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp),
        h2 = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold, lineHeight = 32.sp),
        h3 = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium, lineHeight = 26.sp),
        body = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 22.sp),
        bodySmall = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal, lineHeight = 18.sp),
        code = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.Monospace, lineHeight = 20.sp),
        label = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp),
    )

val LocalSiteTypography = staticCompositionLocalOf { DefaultSiteTypography }

@Immutable
data class SiteSpacing(
    val xs: Dp = 4.dp,
    val s: Dp = 8.dp,
    val m: Dp = 16.dp,
    val l: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
)

val LocalSiteSpacing = staticCompositionLocalOf { SiteSpacing() }

object SiteTheme {
    val colors: SiteColors
        @Composable get() = LocalSiteColors.current

    val typography: SiteTypography
        @Composable get() = LocalSiteTypography.current

    val spacing: SiteSpacing
        @Composable get() = LocalSiteSpacing.current
}

@Composable
fun SiteTheme(
    colors: SiteColors = DarkSiteColors,
    typography: SiteTypography = DefaultSiteTypography,
    spacing: SiteSpacing = SiteSpacing(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalSiteColors provides colors,
        LocalSiteTypography provides typography,
        LocalSiteSpacing provides spacing,
    ) {
        ProvidesContentColor(color = colors.textPrimary) {
            content()
        }
    }
}
