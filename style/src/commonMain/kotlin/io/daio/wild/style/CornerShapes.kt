// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr

/**
 * A shape describing the rectangle with rounded corners with additional radius based on a supplied
 * [cornerExpansion].
 *
 * @param topStart a size of the top start corner
 * @param topEnd a size of the top end corner
 * @param bottomEnd a size of the bottom end corner
 * @param bottomStart a size of the bottom start corner
 * @param cornerExpansion an additional length to apply to the radius.
 */
@Immutable
internal data class ExpandedRoundedCornerShape(
    val topStart: CornerSize,
    val topEnd: CornerSize,
    val bottomEnd: CornerSize,
    val bottomStart: CornerSize,
    val cornerExpansion: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val topStart = topStart.toPx(size, density)
        val topEnd = topEnd.toPx(size, density)
        val bottomStart = bottomStart.toPx(size, density)
        val bottomEnd = bottomEnd.toPx(size, density)
        if (topStart + topEnd + bottomEnd + bottomStart == 0.0f) {
            Outline.Rectangle(size.toRect())
        }

        val insetPx = with(density) { cornerExpansion.toPx() }

        val topStartWithInset = insetPx + topStart
        val topEndWithInset = insetPx + topEnd
        val bottomStartWithInset = insetPx + bottomStart
        val bottomEndWithInset = insetPx + bottomEnd

        return Outline.Rounded(
            RoundRect(
                rect = size.toRect(),
                topLeft = CornerRadius(if (layoutDirection == Ltr) topStartWithInset else topEndWithInset),
                topRight = CornerRadius(if (layoutDirection == Ltr) topEndWithInset else topStartWithInset),
                bottomRight = CornerRadius(if (layoutDirection == Ltr) bottomEndWithInset else bottomStartWithInset),
                bottomLeft = CornerRadius(if (layoutDirection == Ltr) bottomStartWithInset else bottomEndWithInset),
            ),
        )
    }
}

internal fun RoundedCornerShape.toExpandedCornerShape(cornerExpansion: Dp): ExpandedRoundedCornerShape =
    ExpandedRoundedCornerShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomStart = bottomStart,
        bottomEnd = bottomEnd,
        cornerExpansion = cornerExpansion,
    )
