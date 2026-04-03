// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class SiteColors(
    val background: Color,
    val surface: Color,
    val accent: Color,
    val accentSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val codeBackground: Color,
)

val DarkSiteColors =
    SiteColors(
        background = Color(0xFF0F1117),
        surface = Color(0xFF161B22),
        accent = Color(0xFF94A996),
        accentSubtle = Color(0x3094A996),
        textPrimary = Color(0xFFE6EDF3),
        textSecondary = Color(0xFF8B949E),
        border = Color(0xFF2D333B),
        codeBackground = Color(0xFF0D1117),
    )

val LocalSiteColors = staticCompositionLocalOf { DarkSiteColors }
