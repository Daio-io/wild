// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * CompositionLocal containing the preferred content color for a given position in the hierarchy.
 *
 * Defaults to Color. Black if no color has been explicitly set.
 *
 * Use with [ProvidesContentColor] to ensure all Material library LocalContentColor match.
 *
 * @since 0.2.0
 */
val LocalContentColor = compositionLocalOf { Color.Black }

/**
 * Sets [LocalContentColor] to the provided [color].
 *
 * This function also acts as a shim to set LocalContentColor from all Material libraries.
 *
 * @param color The color to set for the [LocalContentColor].
 * @param content Main composable content.
 *
 * @since 0.2.0
 */
@Composable
fun ProvidesContentColor(
    color: Color,
    content: @Composable () -> Unit,
) = CompositionLocalProvider(
    material3ContentColorLocal provides color,
    materialContentColorLocal provides color,
    LocalAlternatePlatformColor provides color,
    LocalContentColor provides color,
    content = content,
)

internal expect val LocalAlternatePlatformColor: ProvidableCompositionLocal<Color>

internal val material3ContentColorLocal: ProvidableCompositionLocal<Color> =
    try {
        androidx.compose.material3.LocalContentColor
    } catch (_: Throwable) {
        LocalContentColor
    }

internal val materialContentColorLocal: ProvidableCompositionLocal<Color> =
    try {
        androidx.compose.material.LocalContentColor
    } catch (_: Throwable) {
        LocalContentColor
    }
