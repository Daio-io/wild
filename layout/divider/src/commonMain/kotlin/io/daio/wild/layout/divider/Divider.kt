// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.layout.divider

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
 * ```kotlin
 * Column {
 *     Text("Account")
 *     HorizontalDivider()
 *     Text("Notifications")
 * }
 * ```
 *
 * @param modifier Modifier to apply to the divider.
 * @param color Color of the divider line. Defaults to [LocalContentColor].
 * @param thickness Thickness of the divider line.
 *
 * @since 0.6.0
 * @guidance Place a HorizontalDivider between groups stacked in a Column and give it bounded width.
 */
@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    thickness: Dp = DividerDefaults.thickness,
) {
    Canvas(modifier.fillMaxWidth().height(thickness)) {
        drawLine(
            color = color,
            strokeWidth = size.height,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
        )
    }
}

/**
 * A vertical divider line for separating content.
 *
 * By default uses [LocalContentColor] for the divider color, allowing it to
 * adapt when placed inside styled containers.
 *
 * ```kotlin
 * Row(Modifier.height(32.dp)) {
 *     Text("Monthly")
 *     VerticalDivider()
 *     Text("Yearly")
 * }
 * ```
 *
 * @param modifier Modifier to apply to the divider.
 * @param color Color of the divider line. Defaults to [LocalContentColor].
 * @param thickness Thickness of the divider line.
 *
 * @since 0.6.0
 * @guidance Place a VerticalDivider between items in a Row with a bounded height.
 */
@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    thickness: Dp = DividerDefaults.thickness,
) {
    Canvas(modifier.fillMaxHeight().width(thickness)) {
        drawLine(
            color = color,
            strokeWidth = size.width,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
        )
    }
}

/**
 * Contains the default values used by [HorizontalDivider] and [VerticalDivider].
 *
 * @since 0.6.0
 */
object DividerDefaults {
    /**
     * Default line thickness for both horizontal and vertical dividers.
     */
    val thickness: Dp = 1.dp
}
