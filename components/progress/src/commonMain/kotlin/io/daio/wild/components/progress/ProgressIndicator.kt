// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.progress

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A caller-rendered determinate linear progress indicator.
 *
 * This primitive provides progress semantics and passes the normalized progress value to
 * [content]. It does not choose dimensions, colors, animation, or drawing. A [progress] value
 * outside `0f..1f` is coerced into that range; `NaN` is treated as `0f`.
 *
 * @param progress Current progress from `0f` to `1f`.
 * @param modifier Modifier applied to the indicator container.
 * @param content Caller-rendered content receiving the normalized progress value.
 * @since 0.7.0
 */
@Composable
fun LinearProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(progress: Float) -> Unit,
) {
    ProgressIndicator(
        progress = progress,
        modifier = modifier,
        content = { normalizedProgress -> content(requireNotNull(normalizedProgress)) },
    )
}

/**
 * A caller-rendered indeterminate linear progress indicator.
 *
 * This primitive provides indeterminate progress semantics and does not choose dimensions,
 * colors, animation, or drawing.
 *
 * @param modifier Modifier applied to the indicator container.
 * @param content Caller-rendered content.
 * @since 0.7.0
 */
@Composable
fun LinearProgressIndicator(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    ProgressIndicator(
        progress = null,
        modifier = modifier,
        content = { content() },
    )
}

/**
 * A caller-rendered determinate circular progress indicator.
 *
 * This primitive provides progress semantics and passes the normalized progress value to
 * [content]. It does not choose dimensions, colors, animation, or drawing. A [progress] value
 * outside `0f..1f` is coerced into that range; `NaN` is treated as `0f`.
 *
 * @param progress Current progress from `0f` to `1f`.
 * @param modifier Modifier applied to the indicator container.
 * @param content Caller-rendered content receiving the normalized progress value.
 * @since 0.7.0
 */
@Composable
fun CircularProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(progress: Float) -> Unit,
) {
    ProgressIndicator(
        progress = progress,
        modifier = modifier,
        content = { normalizedProgress -> content(requireNotNull(normalizedProgress)) },
    )
}

/**
 * A caller-rendered indeterminate circular progress indicator.
 *
 * This primitive provides indeterminate progress semantics and does not choose dimensions,
 * colors, animation, or drawing.
 *
 * @param modifier Modifier applied to the indicator container.
 * @param content Caller-rendered content.
 * @since 0.7.0
 */
@Composable
fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    ProgressIndicator(
        progress = null,
        modifier = modifier,
        content = { content() },
    )
}

private fun Float.normalizedProgress(): Float =
    if (isNaN()) {
        0f
    } else {
        coerceIn(0f, 1f)
    }

@Composable
private fun ProgressIndicator(
    progress: (() -> Float)?,
    modifier: Modifier,
    content: @Composable BoxScope.(progress: Float?) -> Unit,
) {
    if (progress == null) {
        Box(
            modifier = modifier.progressSemantics(),
            content = { content(null) },
        )
    } else {
        val normalizedProgress = progress().normalizedProgress()
        Box(
            modifier = modifier.progressSemantics(normalizedProgress, 0f..1f),
            content = { content(normalizedProgress) },
        )
    }
}
