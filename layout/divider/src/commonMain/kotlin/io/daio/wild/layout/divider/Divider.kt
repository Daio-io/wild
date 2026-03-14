// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.layout.divider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor

/**
 * A horizontal divider line for separating content.
 *
 * By default uses [LocalContentColor] for the divider color, allowing it to
 * adapt when placed inside styled containers.
 *
 * @param modifier Modifier to apply to the divider.
 * @param color Color of the divider line. Defaults to [LocalContentColor].
 * @param thickness Thickness of the divider line.
 *
 * @since 0.6.0
 */
@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    thickness: Dp = DividerDefaults.thickness,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(thickness)
                .background(color),
    )
}

/**
 * A vertical divider line for separating content.
 *
 * By default uses [LocalContentColor] for the divider color, allowing it to
 * adapt when placed inside styled containers.
 *
 * @param modifier Modifier to apply to the divider.
 * @param color Color of the divider line. Defaults to [LocalContentColor].
 * @param thickness Thickness of the divider line.
 *
 * @since 0.6.0
 */
@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    thickness: Dp = DividerDefaults.thickness,
) {
    Box(
        modifier =
            modifier
                .fillMaxHeight()
                .width(thickness)
                .background(color),
    )
}

/**
 * Contains the default values used by [HorizontalDivider] and [VerticalDivider].
 *
 * @since 0.6.0
 */
object DividerDefaults {
    /**
     * The default thickness of the divider.
     */
    val thickness: Dp = 1.dp
}
