// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle

/**
 * Composition local for providing a default [TextStyle] to [Text] components.
 *
 * Components can set this to propagate typography styles to their children.
 * When not explicitly provided, defaults to [TextStyle.Default].
 *
 * @since 0.6.0
 */
val LocalTextStyle = compositionLocalOf { TextStyle.Default }

/**
 * Provides a [TextStyle] to the composition, affecting all [Text] components
 * within the [content] block that don't explicitly override their style.
 *
 * @param style The [TextStyle] to provide.
 * @param content The composable content that will inherit this text style.
 *
 * @since 0.6.0
 */
@Composable
fun ProvidesTextStyle(
    style: TextStyle,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalTextStyle provides style, content = content)
}
