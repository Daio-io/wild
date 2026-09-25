// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.progress

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A determinate linear progress indicator.
 *
 * Provides progress semantics and passes the normalized progress value to [content]. By default
 * [content] draws [ProgressIndicatorDefaults.LinearIndicator]; replace the slot for fully custom
 * artwork. A [progress] value outside `0f..1f` is coerced into that range; `NaN` is treated as `0f`.
 *
 * @param progress Current progress from `0f` to `1f`.
 * @param modifier Modifier applied to the indicator container.
 * @param content Content receiving the normalized progress value. Defaults to
 *   [ProgressIndicatorDefaults.LinearIndicator].
 * @since 0.7.0
 *
 * Example:
 * ```
 * LinearProgressIndicator(progress = { 0.65f })
 *
 * LinearProgressIndicator(progress = { 0.65f }) { progress ->
 *     ProgressIndicatorDefaults.LinearIndicator(
 *         progress = progress,
 *         strokeHeight = 6.dp,
 *     )
 * }
 * ```
 */
@Composable
fun LinearProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(progress: Float) -> Unit = {
        ProgressIndicatorDefaults.LinearIndicator(progress = it)
    },
) {
    ProgressIndicator(
        progress = progress,
        modifier = modifier,
        content = { normalizedProgress -> content(requireNotNull(normalizedProgress)) },
    )
}

/**
 * An indeterminate linear progress indicator.
 *
 * Provides indeterminate progress semantics. By default [content] draws
 * [ProgressIndicatorDefaults.LinearIndeterminateIndicator]; replace the slot for fully custom
 * artwork and animation.
 *
 * @param modifier Modifier applied to the indicator container.
 * @param content Indicator content. Defaults to [ProgressIndicatorDefaults.LinearIndeterminateIndicator].
 * @since 0.7.0
 *
 * Example:
 * ```
 * LinearProgressIndicator()
 *
 * LinearProgressIndicator {
 *     ProgressIndicatorDefaults.LinearIndeterminateIndicator(strokeHeight = 6.dp)
 * }
 * ```
 */
@Composable
fun LinearProgressIndicator(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {
        ProgressIndicatorDefaults.LinearIndeterminateIndicator()
    },
) {
    ProgressIndicator(
        progress = null,
        modifier = modifier,
        content = { content() },
    )
}

/**
 * A determinate circular progress indicator.
 *
 * Provides progress semantics and passes the normalized progress value to [content]. By default
 * [content] draws [ProgressIndicatorDefaults.CircularIndicator]; replace the slot for fully custom
 * artwork. A [progress] value outside `0f..1f` is coerced into that range; `NaN` is treated as `0f`.
 *
 * @param progress Current progress from `0f` to `1f`.
 * @param modifier Modifier applied to the indicator container.
 * @param content Content receiving the normalized progress value. Defaults to
 *   [ProgressIndicatorDefaults.CircularIndicator].
 * @since 0.7.0
 *
 * Example:
 * ```
 * CircularProgressIndicator(progress = { 0.7f })
 *
 * CircularProgressIndicator(progress = { 0.7f }) { progress ->
 *     ProgressIndicatorDefaults.CircularIndicator(
 *         progress = progress,
 *         strokeWidth = 8.dp,
 *     )
 * }
 * ```
 */
@Composable
fun CircularProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(progress: Float) -> Unit = {
        ProgressIndicatorDefaults.CircularIndicator(progress = it)
    },
) {
    ProgressIndicator(
        progress = progress,
        modifier = modifier,
        content = { normalizedProgress -> content(requireNotNull(normalizedProgress)) },
    )
}

/**
 * An indeterminate circular progress indicator.
 *
 * Provides indeterminate progress semantics. By default [content] draws
 * [ProgressIndicatorDefaults.CircularIndeterminateIndicator]; replace the slot for fully custom
 * artwork and animation.
 *
 * @param modifier Modifier applied to the indicator container.
 * @param content Indicator content. Defaults to
 *   [ProgressIndicatorDefaults.CircularIndeterminateIndicator].
 * @since 0.7.0
 *
 * Example:
 * ```
 * CircularProgressIndicator()
 *
 * CircularProgressIndicator {
 *     ProgressIndicatorDefaults.CircularIndeterminateIndicator(strokeWidth = 8.dp)
 * }
 * ```
 */
@Composable
fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {
        ProgressIndicatorDefaults.CircularIndeterminateIndicator()
    },
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
