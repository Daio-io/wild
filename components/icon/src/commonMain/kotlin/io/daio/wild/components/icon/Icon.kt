// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
 * to [tint] to disable tinting.
 *
 * @param painter The [Painter] to draw.
 * @param contentDescription Accessibility description for the icon.
 * @param modifier Modifier to apply to the icon.
 * @param tint The tint color to apply. Defaults to [LocalContentColor].
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
    val colorFilter = if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
    val semanticsModifier =
        if (contentDescription != null) {
            Modifier.semantics {
                this.contentDescription = contentDescription
                this.role = Role.Image
            }
        } else {
            Modifier
        }

    Box(
        modifier =
            modifier
                .size(IconDefaults.defaultSize)
                .then(semanticsModifier),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            colorFilter = colorFilter,
        )
    }
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
 * Contains the default values used by [Icon].
 *
 * @since 0.6.0
 */
object IconDefaults {
    /**
     * The default size of the icon.
     */
    val defaultSize: Dp = 24.dp
}
