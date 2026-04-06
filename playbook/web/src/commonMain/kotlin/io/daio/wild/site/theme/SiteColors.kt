// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class SiteColors(
    val background: Color,
    val surface: Color,
    val accent: Color,
    val accentSubtle: Color,
    val accentText: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val codeBackground: Color,
    val demoBackground: Color,
)

val DarkSiteColors =
    SiteColors(
        background = Color(0xFF0F1117),
        surface = Color(0xFF161B22),
        accent = Color(0xFF94A996),
        accentSubtle = Color(0x3094A996),
        accentText = Color(0xFFB8C9BA),
        textPrimary = Color(0xFFE6EDF3),
        textSecondary = Color(0xFF8B949E),
        border = Color(0xFF2D333B),
        codeBackground = Color(0xFF0D1117),
        demoBackground = Color(0xFF3B5A41),
    )

val LightSiteColors =
    SiteColors(
        background = Color(0xFFFFFFFF),
        surface = Color(0xFFF6F8FA),
        accent = Color(0xFF5A7D5E),
        accentSubtle = Color(0x1A5A7D5E),
        accentText = Color(0xFF3D5940),
        textPrimary = Color(0xFF1F2328),
        textSecondary = Color(0xFF656D76),
        border = Color(0xFFD0D7DE),
        codeBackground = Color(0xFFF6F8FA),
        demoBackground = Color(0xFFD4E5D6),
    )

val LocalSiteColors = compositionLocalOf { DarkSiteColors }
