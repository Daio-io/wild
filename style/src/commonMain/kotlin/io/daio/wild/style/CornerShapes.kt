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
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.contracts.returns

/**
 * A shape describing a rectangle with rounded corners, where each corner radius is expanded by an
 * additional amount specified by [cornerExpansion].
 *
 * This shape is useful when you need to create rounded corners that account for additional spacing,
 * such as when applying borders with insets. The [cornerExpansion] value is added to each corner's
 * radius, ensuring that the rounded corners maintain their visual appearance even when the shape
 * needs to be adjusted for spacing requirements.
 *
 * @param topStart The size of the top start corner radius.
 * @param topEnd The size of the top end corner radius.
 * @param bottomEnd The size of the bottom end corner radius.
 * @param bottomStart The size of the bottom start corner radius.
 * @param cornerExpansion The additional length to apply to each corner radius. This value is added
 * to the base corner radius for all four corners.
 *
 * @since 0.5.0
 */
@Immutable
data class ExpandedRoundedCornerShape(
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

/**
 * Converts a [RoundedCornerShape] to an [ExpandedRoundedCornerShape] by applying the specified
 * [cornerExpansion] to all corners.
 *
 * This extension function provides a convenient way to create an expanded rounded corner shape
 * from an existing [RoundedCornerShape]. The resulting shape will have the same corner radii as
 * the original shape, but with the [cornerExpansion] value added to each corner.
 *
 * @param cornerExpansion The additional length to apply to each corner radius. This value is added
 * to the base corner radius for all four corners.
 * @return An [ExpandedRoundedCornerShape] with the same corner sizes as this [RoundedCornerShape],
 * but with the [cornerExpansion] applied to each corner.
 *
 * @since 0.5.0
 */
fun RoundedCornerShape.toExpandedCornerShape(cornerExpansion: Dp): ExpandedRoundedCornerShape =
    ExpandedRoundedCornerShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomStart = bottomStart,
        bottomEnd = bottomEnd,
        cornerExpansion = cornerExpansion,
    )

/**
 * Checks if this [Shape] is a [RoundedCornerShape].
 *
 * This function uses Kotlin contracts to enable smart casting, allowing the compiler to
 * automatically cast the shape to [RoundedCornerShape] when this function returns `true`.
 *
 * @return `true` if this shape is a [RoundedCornerShape], `false` otherwise.
 *
 * @since 0.5.0
 */
@OptIn(ExperimentalContracts::class)
fun Shape.isRoundedCornerShape(): Boolean {
    contract {
        returns(true) implies (this@isRoundedCornerShape is RoundedCornerShape)
    }
    return this is RoundedCornerShape
}

/**
 * Converts this [Shape] to an [ExpandedRoundedCornerShape] if it is a [RoundedCornerShape],
 * otherwise returns the shape unchanged.
 *
 * This extension function provides a safe way to expand rounded corner shapes when needed,
 * while preserving other shape types. If the shape is not a [RoundedCornerShape], no conversion
 * is performed and the original shape is returned.
 *
 * @param cornerExpansion The additional length to apply to each corner radius. This value is only
 * used if this shape is a [RoundedCornerShape].
 * @return An [ExpandedRoundedCornerShape] if this shape is a [RoundedCornerShape], otherwise
 * returns this shape unchanged.
 *
 * @since 0.5.0
 */
fun Shape.toExpandedCornerShapeOrSelf(cornerExpansion: Dp): Shape =
    if (this is RoundedCornerShape) {
        this.toExpandedCornerShape(cornerExpansion)
    } else {
        this
    }
