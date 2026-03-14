// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.icon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor

/**
 * Icon component that draws a [Painter] with tinting support.
 *
 * By default, the icon uses [LocalContentColor] for tinting. Pass [Color.Unspecified]
 * to [tint] to disable tinting and use the painter's original colors.
 *
 * If the painter has an intrinsic size, it will be used. Otherwise, the icon defaults
 * to [IconDefaults.defaultSize].
 *
 * @param painter The [Painter] to draw.
 * @param contentDescription Accessibility description for the icon. Should be provided
 *   unless the icon is purely decorative. Pass null for decorative icons.
 * @param modifier Modifier to apply to the icon.
 * @param tint The tint color to apply. Defaults to [LocalContentColor].
 *   Pass [Color.Unspecified] to disable tinting.
 *
 * @since 0.6.0
 */
@Composable
fun Icon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    val colorFilter =
        remember(tint) {
            if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
        }
    Box(
        modifier
            .defaultSizeFor(painter)
            .paint(painter, colorFilter = colorFilter, contentScale = ContentScale.Fit)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics {
                        this.contentDescription = contentDescription
                        this.role = Role.Image
                    }
                } else {
                    Modifier
                },
            ),
    )
}

/**
 * Icon component that draws an [ImageVector] with tinting support.
 *
 * @param imageVector The [ImageVector] to draw.
 * @param contentDescription Accessibility description for the icon.
 * @param modifier Modifier to apply to the icon.
 * @param tint The tint color to apply. Defaults to [LocalContentColor].
 *
 * @since 0.6.0
 */
@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Icon(
        painter = rememberVectorPainter(imageVector),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

/**
 * Icon component that draws an [ImageBitmap] with tinting support.
 *
 * @param bitmap The [ImageBitmap] to draw.
 * @param contentDescription Accessibility description for the icon.
 * @param modifier Modifier to apply to the icon.
 * @param tint The tint color to apply. Defaults to [LocalContentColor].
 *
 * @since 0.6.0
 */
@Composable
fun Icon(
    bitmap: ImageBitmap,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    val painter = remember(bitmap) { BitmapPainter(bitmap) }
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

/**
 * Applies the default icon size only when the painter has no intrinsic size.
 * If the painter provides its own dimensions, those are respected.
 */
private fun Modifier.defaultSizeFor(painter: Painter): Modifier =
    this.then(
        if (painter.intrinsicSize == Size.Unspecified || painter.intrinsicSize.isInfinite()) {
            Modifier.size(IconDefaults.defaultSize)
        } else {
            Modifier
        },
    )

private fun Size.isInfinite() = width.isInfinite() && height.isInfinite()

/**
 * Contains the default values used by [Icon].
 *
 * @since 0.6.0
 */
object IconDefaults {
    /**
     * The default size of the icon, used when the painter has no intrinsic size.
     */
    val defaultSize: Dp = 24.dp
}
