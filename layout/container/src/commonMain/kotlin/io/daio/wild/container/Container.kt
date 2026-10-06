// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.container

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import io.daio.wild.content.ProvidesContentColor
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.Alpha
import io.daio.wild.style.Border
import io.daio.wild.style.BorderDefaults
import io.daio.wild.style.Borders
import io.daio.wild.style.Colors
import io.daio.wild.style.Scale
import io.daio.wild.style.Shapes
import io.daio.wild.style.Style
import io.daio.wild.style.StyleDefaults
import io.daio.wild.style.StyleSpec
import io.daio.wild.style.contentColorBridge
import io.daio.wild.style.interactable
import io.daio.wild.style.staticStyle

/**
 * [Container] is a building block component that can be used for any static element or as an
 * interactive container.
 *
 * @param modifier Modifier to be applied to the container layout for styling and positioning.
 * @param color Background color of the container.
 * @param contentColor Color for content elements within the container.
 * @param shape Defines the shape of the container.
 * @param border Optional border to apply around the container.
 * @param content A [Composable] lambda that defines the content inside the container.
 *
 * @since 0.3.1
 */
@Composable
fun Container(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    shape: Shape = RectangleShape,
    border: Border = BorderDefaults.None,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier =
            modifier.staticStyle(
                style =
                    StyleDefaults.style(
                        colors =
                            StyleDefaults.colors(
                                backgroundColor = color,
                                contentColor = contentColor,
                            ),
                        shapes = StyleDefaults.shapes(shape = shape),
                        borders = StyleDefaults.borders(border = border),
                    ),
            ),
        propagateMinConstraints = true,
        content = {
            ProvidesContentColor(contentColor) {
                content()
            }
        },
    )
}

/**
 * [Container] is a building block component that can be used for any selectable TV
 * element or on its own as a selectable container. The [Container] handles an additional
 * state compared to [Container] to indicate whether it is currently selected.
 *
 * @param onClick Callback to be called when the container is clicked. If this and [onLongClick]
 * are null, the container will not be focusable on TV.
 * @param modifier Modifier to be applied to the layout corresponding to the container.
 * @param enabled Whether or not the container is enabled.
 * @param selected Whether or not the container is currently selected.
 * @param onLongClick Callback to be called when the container is long clicked. If this and
 * [onClick] are null, the container will not be focusable on TV.
 * @param onDoubleClick Optional callback to be called when the container is double clicked.
 * @param style The [Style] to supply to the Container. See [StyleDefaults.style].
 * @param interactionSource An optional hoisted [MutableInteractionSource] for observing and
 * emitting [Interaction]s for this container.
 * @param content Defines the [Composable] content inside the container.
 *
 * Example:
 * ```
 * Container(
 *     style =
 *         StyleDefaults.style(
 *             colors = StyleDefaults.colors(
 *                 backgroundColor = Color.Black,
 *                 contentColor = Color.White,
 *                 focusedBackgroundColor = Color.Blue,
 *                 focusedContentColor = Color.Yellow,
 *                 pressedBackgroundColor = Color.Black.copy(alpha = 0.6f)
 *             ),
 *             scale = StyleDefaults.scale(focusedScale = 1.1f),
 *             shapes = StyleDefaults.shapes(RoundedCornerShape(8.dp))
 *         ),
 *     modifier = Modifier.size(300.dp, 100.dp),
 *     onClick = { /* Handle click */ },
 *     onLongClick = { /* Handle long click */ }
 * ) {
 *     val color = LocalContentColor.current
 *     BasicText(text = "Interactive Container", color = { color })
 * }
 * ```
 *
 * Example usage as a selectable container.
 * ```
 * var selected by remember { mutableStateOf(false) }
 * Container(
 *     onClick = {
 *         selected = !selected
 *     },
 *     selected = selected,
 *     style =
 *         StyleDefaults.style(
 *             colors = StyleDefaults.colors(
 *                 backgroundColor = if (selected) Color.Green else Color.Black,
 *                 contentColor = Color.White,
 *                 focusedBackgroundColor = Color.Red,
 *                 focusedContentColor = Color.Black,
 *                 pressedBackgroundColor = Color.Black.copy(alpha = 0.6f)
 *             ),
 *             scale = StyleDefaults.scale(focusedScale = 1.1f),
 *             shapes = StyleDefaults.shapes(RoundedCornerShape(8.dp))
 *         )
 * ) {
 *     val color = LocalContentColor.current
 *     BasicText(
 *         text = if (selected) "Selected Container" else "Unselected Container",
 *         color = { color },
 *     )
 * }
 * ```
 */
@OptIn(ExperimentalWildApi::class)
@Composable
fun Container(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    style: Style = StyleDefaults.None,
    interactionSource: MutableInteractionSource? = null,
    selected: Boolean? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val selectedFlag = selected ?: false
    val binding =
        remember(style, enabled, selectedFlag) {
            ComponentStyleBinding(
                initial =
                    style.colors.contentColorFor(
                        enabled = enabled,
                        focused = false,
                        hovered = false,
                        pressed = false,
                        selected = selectedFlag,
                    ),
            )
        }

    Box(
        modifier =
            modifier
                .interactable(
                    selected = selected,
                    enabled = enabled,
                    style = style,
                    onClick = onClick,
                    onLongClick = onLongClick,
                    onDoubleClick = onDoubleClick,
                    interactionSource = interactionSource,
                )
                .contentColorBridge(binding),
        propagateMinConstraints = true,
        content = {
            ProvidesContentColor(binding.contentColor.value) {
                content()
            }
        },
    )
}

/**
 * Interactive [Container] that applies an experimental [StyleSpec] for chrome and content-color
 * propagation through the equality-gated bridge.
 *
 * Owns one [MutableInteractionSource] and one style chain. Prefer a stable [StyleSpec] (hoisted
 * or remembered callbacks).
 *
 * @param onClick Callback when the container is clicked.
 * @param style Required [StyleSpec] distinguishing this overload from the value [Style] overload.
 * @param modifier Modifier applied outside the style chain.
 * @param enabled Whether the container is enabled.
 * @param onLongClick Optional long-click callback.
 * @param onDoubleClick Optional double-click callback.
 * @param interactionSource Optional hoisted interaction source; when null, Container owns one.
 * @param selected Optional selected flag for selectable surfaces.
 * @param content Content inside the container.
 *
 * Example:
 * ```
 * val spec = styleSpec(StyleDefaults.style()) {
 *     if (focused) {
 *         scale = 1.1f
 *         contentColor = Color.Yellow
 *     }
 * }
 * Container(onClick = { }, style = spec) {
 *     Text("Spec container")
 * }
 * ```
 *
 * @since 0.8.0
 */
@ExperimentalWildApi
@Composable
fun Container(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    style: StyleSpec,
    interactionSource: MutableInteractionSource? = null,
    selected: Boolean? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val selectedFlag = selected ?: false
    val binding =
        remember(style, enabled, selectedFlag) {
            ComponentStyleBinding(
                initial =
                    style.base.colors.contentColorFor(
                        enabled = enabled,
                        focused = false,
                        hovered = false,
                        pressed = false,
                        selected = selectedFlag,
                    ),
            )
        }

    Box(
        modifier =
            modifier
                .interactable(
                    selected = selected,
                    enabled = enabled,
                    style = style,
                    onClick = onClick,
                    onLongClick = onLongClick,
                    onDoubleClick = onDoubleClick,
                    interactionSource = interactionSource,
                )
                .contentColorBridge(binding),
        propagateMinConstraints = true,
        content = {
            ProvidesContentColor(binding.contentColor.value) {
                content()
            }
        },
    )
}

/**
 * [ExperimentalContainer] is a building block component that can be used for any selectable TV
 * element or on its own as a selectable container. The [Container] handles an additional
 * state compared to [Container] to indicate whether it is currently selected.
 *
 * The experimental version uses the []
 *
 * @param onClick Callback to be called when the container is clicked. If this and [onLongClick]
 * are null, the container will not be focusable on TV.
 * @param modifier Modifier to be applied to the layout corresponding to the container.
 * @param enabled Whether or not the container is enabled.
 * @param selected Whether or not the container is currently selected.
 * @param onLongClick Callback to be called when the container is long clicked. If this and
 * [onClick] are null, the container will not be focusable on TV.
 * @param onDoubleClick Optional callback to be called when the container is double clicked.
 * @param style The [Style] to supply to the Container. See [StyleDefaults.style].
 * @param interactionSource An optional hoisted [MutableInteractionSource] for observing and
 * emitting [Interaction]s for this container.
 * @param content Defines the [Composable] content inside the container.
 *
 * Example:
 * ```
 * ExperimentalContainer(
 *     style =
 *         StyleDefaults.style(
 *             colors = StyleDefaults.colors(
 *                 backgroundColor = Color.Black,
 *                 contentColor = Color.White,
 *                 focusedBackgroundColor = Color.Blue,
 *                 focusedContentColor = Color.Yellow,
 *                 pressedBackgroundColor = Color.Black.copy(alpha = 0.6f)
 *             ),
 *             scale = StyleDefaults.scale(focusedScale = 1.1f),
 *             shapes = StyleDefaults.shapes(RoundedCornerShape(8.dp))
 *         ),
 *     modifier = Modifier.size(300.dp, 100.dp),
 *     onClick = { /* Handle click */ },
 *     onLongClick = { /* Handle long click */ }
 * ) {
 *     val color = LocalContentColor.current
 *     BasicText(text = "Interactive Container", color = { color })
 * }
 * ```
 *
 * Example usage as a selectable container.
 * ```
 * var selected by remember { mutableStateOf(false) }
 * ExperimentalContainer(
 *     onClick = {
 *         selected = !selected
 *     },
 *     selected = selected,
 *     style =
 *         StyleDefaults.style(
 *             colors = StyleDefaults.colors(
 *                 backgroundColor = if (selected) Color.Green else Color.Black,
 *                 contentColor = Color.White,
 *                 focusedBackgroundColor = Color.Red,
 *                 focusedContentColor = Color.Black,
 *                 pressedBackgroundColor = Color.Black.copy(alpha = 0.6f)
 *             ),
 *             scale = StyleDefaults.scale(focusedScale = 1.1f),
 *             shapes = StyleDefaults.shapes(RoundedCornerShape(8.dp))
 *         )
 * ) {
 *     val color = LocalContentColor.current
 *     BasicText(
 *         text = if (selected) "Selected Container" else "Unselected Container",
 *         color = { color },
 *     )
 * }
 * ```
 * @since 0.4.0
 */
@Deprecated(
    message = "ExperimentalContainer is no longer needed. Container now uses the node-based style system internally.",
    replaceWith =
        ReplaceWith(
            "Container(onClick, modifier, enabled, onLongClick, onDoubleClick, style, interactionSource, selected, content)",
        ),
    level = DeprecationLevel.WARNING,
)
@OptIn(ExperimentalWildApi::class)
@Composable
fun ExperimentalContainer(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    style: Style = StyleDefaults.None,
    interactionSource: MutableInteractionSource? = null,
    selected: Boolean? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Container(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        style = style,
        interactionSource = interactionSource,
        selected = selected,
        content = content,
    )
}

/**
 * Contains the default values used by [Container].
 *
 * @since 0.5.0
 */
object ContainerDefaults {
    /**
     * The default background color for static containers.
     */
    val color: Color = Color.Unspecified

    /**
     * The default content color for static containers.
     */
    val contentColor: Color = Color.Unspecified

    /**
     * The default shape for containers.
     */
    val shape: Shape = RectangleShape

    /**
     * The default border for containers.
     */
    val border: Border = BorderDefaults.None

    /**
     * Creates a default [Style] for interactive containers with customizable style properties.
     *
     * @param colors The colors for the container in different states.
     * @param borders The borders for the container in different states.
     * @param scale The scale for the container in different states.
     * @param shapes The shapes for the container in different states.
     * @param alpha The alpha for the container in different states.
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
