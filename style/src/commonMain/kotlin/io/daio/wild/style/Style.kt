// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.foundation.InteractionState
import io.daio.wild.style.modifiers.BackgroundElement
import io.daio.wild.style.modifiers.BorderElement
import io.daio.wild.style.modifiers.ScaleLayoutElement
import io.daio.wild.style.modifiers.ShapeLayoutElement
import io.daio.wild.style.modifiers.StyleParentTraversalKey
import io.daio.wild.style.modifiers.StyleScopeParentElement
import io.daio.wild.style.modifiers.border
import io.daio.wild.style.modifiers.interactionSourceNode

/**
 * Style class for components.
 *
 * @param colors Defines the background color based on the current state via it's [Colors.colorFor]
 * function.
 * @param scale Defines the button scale based on the current state via it's [Scale.scaleFor]
 * function.
 * @param borders Defines the border based on the current state via it's [Colors.colorFor]
 * function.
 * @param shapes Defines the button shape based on its current state via it's [Shapes.shapeFor]
 * function.
 * @param alpha Defines the button alpha based on its current state via it's [Alpha.alphaFor]
 * function. Note you can still set alpha yourself if needed via a [Modifier]. This parameter is
 * provided by convenience to help state driven Alpha.
 *
 * @since 0.2.0
 */
@Immutable
data class Style(
    val colors: Colors,
    val borders: Borders,
    val scale: Scale,
    val shapes: Shapes,
    val alpha: Alpha,
)

object StyleDefaults {
    val None: Style = style()

    @Stable
    fun style(
        colors: Colors = colors(),
        borders: Borders = borders(),
        scale: Scale = scale(),
        shapes: Shapes = shapes(),
        alpha: Alpha = alpha(),
    ): Style =
        Style(
            colors = colors,
            borders = borders,
            scale = scale,
            shapes = shapes,
            alpha = alpha,
        )

    @Stable
    fun colors(
        /**
         * Background Colors.
         */
        backgroundColor: Color = Color.Black,
        focusedBackgroundColor: Color = backgroundColor,
        pressedBackgroundColor: Color = backgroundColor,
        hoveredBackgroundColor: Color = backgroundColor,
        disabledBackgroundColor: Color = backgroundColor,
        selectedBackgroundColor: Color = backgroundColor,
        focusedSelectedBackgroundColor: Color = focusedBackgroundColor,
        pressedSelectedBackgroundColor: Color = pressedBackgroundColor,
        hoveredSelectedBackgroundColor: Color = hoveredBackgroundColor,
        focusedDisabledBackgroundColor: Color = disabledBackgroundColor,
        pressedDisabledBackgroundColor: Color = disabledBackgroundColor,
        hoveredDisabledBackgroundColor: Color = disabledBackgroundColor,
        /**
         * Content Colors.
         */
        contentColor: Color = Color.White,
        focusedContentColor: Color = contentColor,
        pressedContentColor: Color = contentColor,
        hoveredContentColor: Color = contentColor,
        disabledContentColor: Color = contentColor,
        selectedContentColor: Color = contentColor,
        focusedSelectedContentColor: Color = focusedContentColor,
        pressedSelectedContentColor: Color = pressedContentColor,
        hoveredSelectedContentColor: Color = hoveredContentColor,
        pressedDisabledContentColor: Color = disabledContentColor,
        focusedDisabledContentColor: Color = disabledContentColor,
        hoveredDisabledContentColor: Color = disabledContentColor,
    ): Colors =
        Colors(
            backgroundColor = backgroundColor,
            focusedBackgroundColor = focusedBackgroundColor,
            pressedBackgroundColor = pressedBackgroundColor,
            disabledBackgroundColor = disabledBackgroundColor,
            selectedBackgroundColor = selectedBackgroundColor,
            focusedDisabledBackgroundColor = focusedDisabledBackgroundColor,
            focusedSelectedBackgroundColor = focusedSelectedBackgroundColor,
            hoveredBackgroundColor = hoveredBackgroundColor,
            hoveredDisabledBackgroundColor = hoveredDisabledBackgroundColor,
            hoveredSelectedBackgroundColor = hoveredSelectedBackgroundColor,
            pressedDisabledBackgroundColor = pressedDisabledBackgroundColor,
            pressedSelectedBackgroundColor = pressedSelectedBackgroundColor,
            contentColor = contentColor,
            focusedContentColor = focusedContentColor,
            pressedContentColor = pressedContentColor,
            disabledContentColor = disabledContentColor,
            selectedContentColor = selectedContentColor,
            hoveredContentColor = hoveredContentColor,
            focusedSelectedContentColor = focusedSelectedContentColor,
            hoveredSelectedContentColor = hoveredSelectedContentColor,
            pressedSelectedContentColor = pressedSelectedContentColor,
            pressedDisabledContentColor = pressedDisabledContentColor,
            hoveredDisabledContentColor = hoveredDisabledContentColor,
            focusedDisabledContentColor = focusedDisabledContentColor,
        )

    @Stable
    fun shapes(
        shape: Shape = RectangleShape,
        focusedShape: Shape = shape,
        hoveredShape: Shape = focusedShape,
        pressedShape: Shape = focusedShape,
        selectedShape: Shape = shape,
        disabledShape: Shape = shape,
        focusedSelectedShape: Shape = focusedShape,
        pressedSelectedShape: Shape = pressedShape,
        hoveredSelectedShape: Shape = hoveredShape,
        focusedDisabledShape: Shape = disabledShape,
        pressedDisabledShape: Shape = disabledShape,
        hoveredDisabledShape: Shape = disabledShape,
    ): Shapes =
        Shapes(
            shape = shape,
            focusedShape = focusedShape,
            hoveredShape = hoveredShape,
            pressedShape = pressedShape,
            selectedShape = selectedShape,
            disabledShape = disabledShape,
            focusedSelectedShape = focusedSelectedShape,
            pressedSelectedShape = pressedSelectedShape,
            hoveredSelectedShape = hoveredSelectedShape,
            focusedDisabledShape = focusedDisabledShape,
            pressedDisabledShape = pressedDisabledShape,
            hoveredDisabledShape = hoveredDisabledShape,
        )

    @Stable
    fun scale(
        scale: Float = 1f,
        focusedScale: Float = scale,
        hoveredScale: Float = focusedScale,
        pressedScale: Float = focusedScale,
        selectedScale: Float = scale,
        disabledScale: Float = scale,
        focusedSelectedScale: Float = focusedScale,
        pressedSelectedScale: Float = pressedScale,
        hoveredSelectedScale: Float = hoveredScale,
        focusedDisabledScale: Float = disabledScale,
        pressedDisabledScale: Float = disabledScale,
        hoveredDisabledScale: Float = disabledScale,
    ): Scale =
        Scale(
            scale = scale,
            focusedScale = focusedScale,
            hoveredScale = hoveredScale,
            pressedScale = pressedScale,
            selectedScale = selectedScale,
            disabledScale = disabledScale,
            focusedSelectedScale = focusedSelectedScale,
            pressedSelectedScale = pressedSelectedScale,
            hoveredSelectedScale = hoveredSelectedScale,
            focusedDisabledScale = focusedDisabledScale,
            pressedDisabledScale = pressedDisabledScale,
            hoveredDisabledScale = hoveredDisabledScale,
        )

    @Stable
    fun borders(
        border: Border = BorderDefaults.None,
        focusedBorder: Border = border,
        hoveredBorder: Border = focusedBorder,
        pressedBorder: Border = focusedBorder,
        selectedBorder: Border = border,
        disabledBorder: Border = border,
        focusedSelectedBorder: Border = focusedBorder,
        pressedSelectedBorder: Border = pressedBorder,
        hoveredSelectedBorder: Border = hoveredBorder,
        focusedDisabledBorder: Border = disabledBorder,
        pressedDisabledBorder: Border = disabledBorder,
        hoveredDisabledBorder: Border = disabledBorder,
    ): Borders =
        Borders(
            border = border,
            focusedBorder = focusedBorder,
            hoveredBorder = hoveredBorder,
            pressedBorder = pressedBorder,
            selectedBorder = selectedBorder,
            disabledBorder = disabledBorder,
            focusedSelectedBorder = focusedSelectedBorder,
            pressedSelectedBorder = pressedSelectedBorder,
            hoveredSelectedBorder = hoveredSelectedBorder,
            focusedDisabledBorder = focusedDisabledBorder,
            pressedDisabledBorder = pressedDisabledBorder,
            hoveredDisabledBorder = hoveredDisabledBorder,
        )

    @Stable
    fun alpha(
        alpha: Float = 1f,
        focusedAlpha: Float = alpha,
        hoveredAlpha: Float = alpha,
        pressedAlpha: Float = alpha,
        selectedAlpha: Float = alpha,
        disabledAlpha: Float = .6f,
        focusedSelectedAlpha: Float = focusedAlpha,
        pressedSelectedAlpha: Float = pressedAlpha,
        hoveredSelectedAlpha: Float = hoveredAlpha,
        focusedDisabledAlpha: Float = disabledAlpha,
        pressedDisabledAlpha: Float = disabledAlpha,
        hoveredDisabledAlpha: Float = disabledAlpha,
    ): Alpha =
        Alpha(
            alpha = alpha,
            focusedAlpha = focusedAlpha,
            hoveredAlpha = hoveredAlpha,
            pressedAlpha = pressedAlpha,
            selectedAlpha = selectedAlpha,
            disabledAlpha = disabledAlpha,
            focusedSelectedAlpha = focusedSelectedAlpha,
            pressedSelectedAlpha = pressedSelectedAlpha,
            hoveredSelectedAlpha = hoveredSelectedAlpha,
            focusedDisabledAlpha = focusedDisabledAlpha,
            pressedDisabledAlpha = pressedDisabledAlpha,
            hoveredDisabledAlpha = hoveredDisabledAlpha,
        )
}

/**
 * Sets a [Style] on the element that reacts to interactions from the provided [interactionSource].
 *
 * @param interactionSource The [InteractionSource] used to listen to user interactions such as
 * pressed and focus.
 * @param enabled Whether the element is currently enabled.
 * @param selected Whether the element is currently selected.
 * @param style The [Style] to apply to the element.
 * @since 0.2.0
 */
fun Modifier.interactionStyle(
    interactionSource: InteractionSource?,
    enabled: Boolean = true,
    selected: Boolean = false,
    style: Style,
): Modifier =
    this.interactionStyle(
        interactionSource = interactionSource,
        enabled = enabled,
        selected = selected,
        block = {
            color =
                style.colors.colorFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            scale =
                style.scale.scaleFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            alpha =
                style.alpha.alphaFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            shape =
                style.shapes.shapeFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            border =
                style.borders.borderFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
        },
    )

fun Modifier.experimentalInteractionStyle(
    interactionSource: InteractionSource?,
    enabled: Boolean = true,
    selected: Boolean = false,
    style: Style,
): Modifier =
    this.experimentalInteractionStyle(
        interactionSource = interactionSource,
        enabled = enabled,
        selected = selected,
        block = {
            color =
                style.colors.colorFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            scale =
                style.scale.scaleFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            alpha =
                style.alpha.alphaFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            shape =
                style.shapes.shapeFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            border =
                style.borders.borderFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
        },
    )

/**
 * Sets a [Style] on the element that reacts to interactions from the provided [interactionSource].
 *
 * @param interactionSource The [InteractionSource] used to listen to user interactions such as
 * pressed and focus.
 * @param enabled Whether the element is currently enabled.
 * @param selected Whether the element is currently selected.
 * @param block Lambda to apply style properties. The block provides access to the elements current
 * [InteractionState] (focused, pressed, selected, etc) through [StyleScope].
 *
 * @since 0.4.0
 */
@OptIn(ExperimentalWildApi::class)
fun Modifier.experimentalInteractionStyle(
    interactionSource: InteractionSource?,
    enabled: Boolean = true,
    selected: Boolean = false,
    block: StyleScope.() -> Unit,
): Modifier =
    this.interactionSourceNode(
        interactionSource = interactionSource,
        childTraversalKey = StyleParentTraversalKey,
    ) then StyleScopeParentElement(enabled, selected, block) then
        ScaleLayoutElement() then
        BorderElement() then
        BackgroundElement() then
        ShapeLayoutElement()

/**
 * Sets a [Style] on the element that reacts to interactions from the provided [interactionSource].
 *
 * @param interactionSource The [InteractionSource] used to listen to user interactions such as
 * pressed and focus.
 * @param enabled Whether the element is currently enabled.
 * @param selected Whether the element is currently selected.
 * @param block Lambda to apply style properties. The block provides access to the elements current
 * [InteractionState] (focused, pressed, selected, etc) through [StyleScope].
 *
 * @since 0.4.0
 */
fun Modifier.interactionStyle(
    interactionSource: InteractionSource?,
    enabled: Boolean = true,
    selected: Boolean = false,
    block: StyleScope.() -> Unit,
): Modifier =
    composed {
        // TODO:
        // This is in the process of being moved to ensure this whole Modifier
        // uses the Node api and migrates from composed.
        val style = remember { DefaultStyleScope() }

        val focused = interactionSource?.collectIsFocusedAsState()?.value ?: false
        val hovered = interactionSource?.collectIsHoveredAsState()?.value ?: false
        val pressed = interactionSource?.collectIsPressedAsState()?.value ?: false

        style.updateState(
            enabled = enabled,
            focused = focused,
            selected = selected,
            pressed = pressed,
            hovered = hovered,
        )

        block.invoke(style)

        val zIndex by animateFloatAsState(
            targetValue = if (focused) 0.5f else 0f,
            label = "zIndex",
        )

        val animatedScale by animateInteractionScaleAsState(
            pressed = pressed,
            focused = focused,
            hovered = hovered,
            targetScale = style.scale,
        )

        val border = style.border

        this
            .graphicsLayer {
                this.scaleX = animatedScale
                this.scaleY = animatedScale
            }
            .zIndex(zIndex)
            .border(
                shape = border.forInnerShape(innerShape = style.shape),
                width = border.width,
                borderStroke = border.borderStroke,
                inset = border.inset,
            )
            .background(color = style.color, shape = style.shape)
            .graphicsLayer {
                this.alpha = style.alpha
                this.shape = shape
                this.clip = true
                this.compositingStrategy = CompositingStrategy.Offscreen
            }
    }
