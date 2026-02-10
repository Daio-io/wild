// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.button

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.daio.wild.container.Container
import io.daio.wild.style.Alpha
import io.daio.wild.style.Borders
import io.daio.wild.style.Colors
import io.daio.wild.style.Scale
import io.daio.wild.style.Shapes
import io.daio.wild.style.Style
import io.daio.wild.style.StyleDefaults

/**
 * Basic button component.
 *
 * @param onClick Callback to be invoked when the button is clicked.
 * @param modifier Modifier to be applied to the layout corresponding to the surface.
 * @param enabled Whether the button is enabled.
 * @param onLongClick Callback to be invoked when the button is long clicked.
 * @param onDoubleClick Optional callback to be invoked when the button is double clicked.
 * @param style The style of the button.
 * @param contentPadding [PaddingValues] to be set on the inner content.
 * @param interactionSource Optional [MutableInteractionSource] for observing and emitting [Interaction]s.
 * @param content Defines the [Composable] content inside the button.
 *
 * @since 0.2.0
 *
 * Example:
 * ```
 * Button(
 *     style =
 *         StyleDefaults.style(
 *             colors =
 *                 StyleDefaults.colors(
 *                     backgroundColor = Color.Black,
 *                     contentColor = Color.White,
 *                     focusedBackgroundColor = Color.Red,
 *                     focusedContentColor = Color.Black,
 *                     pressedBackgroundColor = Color.Black.copy(alpha = .6f),
 *                 ),
 *             scale = StyleDefaults.scale(focusedScale = 1.2f),
 *             shapes = StyleDefaults.shapes(RoundedCornerShape(12.dp)),
 *         ),
 *     modifier = modifier.width(200.dp),
 *     onClick = onClick,
 * ) {
 *     val color = LocalContentColor.current
 *     BasicText(text = title, color = { color })
 * }
 * ```
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    style: Style = ButtonDefaults.style(),
    contentPadding: PaddingValues = ButtonDefaults.contentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Container(
        modifier =
            modifier
                .defaultMinSize(ButtonDefaults.defaultWidth, ButtonDefaults.defaultHeight)
                .semantics { role = Role.Button },
        enabled = enabled,
        style = style,
        onClick = onClick,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        interactionSource = interactionSource,
        content = {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(contentPadding),
                contentAlignment = Alignment.Center,
                content = content,
            )
        },
    )
}

/**
 * Contains the default values used by [Button].
 *
 * @since 0.2.0
 */
object ButtonDefaults {
    /**
     * The default minimum height of the button.
     */
    val defaultHeight: Dp = 32.dp

    /**
     * The default minimum width of the button.
     */
    val defaultWidth: Dp = 100.dp

    /**
     * The default content padding applied inside the button.
     *
     * @since 0.5.0
     */
    val contentPadding: PaddingValues = PaddingValues(8.dp)

    /**
     * Creates a default [Style] for the button with customizable style properties.
     *
     * @param colors The colors for the button in different states.
     * @param borders The borders for the button in different states.
     * @param scale The scale for the button in different states.
     * @param shapes The shapes for the button in different states.
     * @param alpha The alpha for the button in different states.
     *
     * @since 0.5.0
     */
    fun style(
        colors: Colors = StyleDefaults.colors(),
        borders: Borders = StyleDefaults.borders(),
        scale: Scale = StyleDefaults.scale(),
        shapes: Shapes = StyleDefaults.shapes(),
        alpha: Alpha = StyleDefaults.alpha(),
    ): Style =
        StyleDefaults.style(
            colors = colors,
            borders = borders,
            scale = scale,
            shapes = shapes,
            alpha = alpha,
        )
}
