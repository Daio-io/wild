// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.foundation.InteractionState
import io.daio.wild.style.modifiers.BackgroundElement
import io.daio.wild.style.modifiers.BorderElement
import io.daio.wild.style.modifiers.ScaleLayoutElement
import io.daio.wild.style.modifiers.ShapeLayoutElement
import io.daio.wild.style.modifiers.StyleParentTraversalKey
import io.daio.wild.style.modifiers.StyleResolver
import io.daio.wild.style.modifiers.StyleScopeParentElement
import io.daio.wild.style.modifiers.border
import io.daio.wild.style.modifiers.interactionSourceNode
import io.daio.wild.style.modifiers.staticStyleBoundary

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
    private val DefaultColors =
        Colors(
            backgroundColor = Color.Black,
            focusedBackgroundColor = Color.Black,
            pressedBackgroundColor = Color.Black,
            hoveredBackgroundColor = Color.Black,
            selectedBackgroundColor = Color.Black,
            disabledBackgroundColor = Color.Black,
            focusedSelectedBackgroundColor = Color.Black,
            pressedSelectedBackgroundColor = Color.Black,
            hoveredSelectedBackgroundColor = Color.Black,
            focusedDisabledBackgroundColor = Color.Black,
            pressedDisabledBackgroundColor = Color.Black,
            hoveredDisabledBackgroundColor = Color.Black,
            contentColor = Color.White,
            focusedContentColor = Color.White,
            hoveredContentColor = Color.White,
            pressedContentColor = Color.White,
            selectedContentColor = Color.White,
            focusedSelectedContentColor = Color.White,
            pressedSelectedContentColor = Color.White,
            hoveredSelectedContentColor = Color.White,
            disabledContentColor = Color.White,
            focusedDisabledContentColor = Color.White,
            pressedDisabledContentColor = Color.White,
            hoveredDisabledContentColor = Color.White,
        )

    private val DefaultBorders =
        Borders(
            border = BorderDefaults.None,
            focusedBorder = BorderDefaults.None,
            hoveredBorder = BorderDefaults.None,
            pressedBorder = BorderDefaults.None,
            selectedBorder = BorderDefaults.None,
            disabledBorder = BorderDefaults.None,
            focusedSelectedBorder = BorderDefaults.None,
            pressedSelectedBorder = BorderDefaults.None,
            hoveredSelectedBorder = BorderDefaults.None,
            focusedDisabledBorder = BorderDefaults.None,
            pressedDisabledBorder = BorderDefaults.None,
            hoveredDisabledBorder = BorderDefaults.None,
        )

    private val DefaultScale =
        Scale(
            scale = 1f,
            focusedScale = 1f,
            hoveredScale = 1f,
            pressedScale = 1f,
            selectedScale = 1f,
            disabledScale = 1f,
            focusedSelectedScale = 1f,
            pressedSelectedScale = 1f,
            hoveredSelectedScale = 1f,
            focusedDisabledScale = 1f,
            pressedDisabledScale = 1f,
            hoveredDisabledScale = 1f,
            animationSpec = null,
        )

    private val DefaultShapes =
        Shapes(
            shape = RectangleShape,
            focusedShape = RectangleShape,
            hoveredShape = RectangleShape,
            pressedShape = RectangleShape,
            selectedShape = RectangleShape,
            disabledShape = RectangleShape,
            focusedSelectedShape = RectangleShape,
            pressedSelectedShape = RectangleShape,
            hoveredSelectedShape = RectangleShape,
            focusedDisabledShape = RectangleShape,
            pressedDisabledShape = RectangleShape,
            hoveredDisabledShape = RectangleShape,
        )

    private val DefaultAlpha =
        Alpha(
            alpha = 1f,
            focusedAlpha = 1f,
            hoveredAlpha = 1f,
            pressedAlpha = 1f,
            selectedAlpha = 1f,
            disabledAlpha = .6f,
            focusedSelectedAlpha = 1f,
            pressedSelectedAlpha = 1f,
            hoveredSelectedAlpha = 1f,
            focusedDisabledAlpha = .6f,
            pressedDisabledAlpha = .6f,
            hoveredDisabledAlpha = .6f,
        )

    val None =
        Style(
            colors = DefaultColors,
            borders = DefaultBorders,
            scale = DefaultScale,
            shapes = DefaultShapes,
            alpha = DefaultAlpha,
        )

    @Stable
    fun style(
        colors: Colors = DefaultColors,
        borders: Borders = DefaultBorders,
        scale: Scale = DefaultScale,
        shapes: Shapes = DefaultShapes,
        alpha: Alpha = DefaultAlpha,
    ): Style =
        if (
            colors === DefaultColors &&
            borders === DefaultBorders &&
            scale === DefaultScale &&
            shapes === DefaultShapes &&
            alpha === DefaultAlpha
        ) {
            None
        } else {
            Style(
                colors = colors,
                borders = borders,
                scale = scale,
                shapes = shapes,
                alpha = alpha,
            )
        }

    @Stable
    fun colors(
        /**
         * Background Colors.
         */
        backgroundColor: Color = DefaultColors.backgroundColor,
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
        contentColor: Color = DefaultColors.contentColor,
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
        if (
            isDefaultColors(
                backgroundColor,
                focusedBackgroundColor,
                pressedBackgroundColor,
                hoveredBackgroundColor,
                disabledBackgroundColor,
                selectedBackgroundColor,
                focusedSelectedBackgroundColor,
                pressedSelectedBackgroundColor,
                hoveredSelectedBackgroundColor,
                focusedDisabledBackgroundColor,
                pressedDisabledBackgroundColor,
                hoveredDisabledBackgroundColor,
                contentColor,
                focusedContentColor,
                pressedContentColor,
                hoveredContentColor,
                disabledContentColor,
                selectedContentColor,
                focusedSelectedContentColor,
                pressedSelectedContentColor,
                hoveredSelectedContentColor,
                pressedDisabledContentColor,
                focusedDisabledContentColor,
                hoveredDisabledContentColor,
            )
        ) {
            DefaultColors
        } else {
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
        }

    /**
     * Creates a [Shapes] instance with configurable shapes for different interaction states.
     *
     * @param shape The default shape.
     * @param focusedShape The shape when the element is focused.
     * @param hoveredShape The shape when the element is hovered.
     * @param pressedShape The shape when the element is pressed.
     * @param selectedShape The shape when the element is selected.
     * @param disabledShape The shape when the element is disabled.
     * @param focusedSelectedShape The shape when the element is both focused and selected.
     * @param pressedSelectedShape The shape when the element is both pressed and selected.
     * @param hoveredSelectedShape The shape when the element is both hovered and selected.
     * @param focusedDisabledShape The shape when the element is both focused and disabled.
     * @param pressedDisabledShape The shape when the element is both pressed and disabled.
     * @param hoveredDisabledShape The shape when the element is both hovered and disabled.
     */
    @Stable
    fun shapes(
        shape: Shape = DefaultShapes.shape,
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
        if (
            isDefaultShapes(
                shape,
                focusedShape,
                hoveredShape,
                pressedShape,
                selectedShape,
                disabledShape,
                focusedSelectedShape,
                pressedSelectedShape,
                hoveredSelectedShape,
                focusedDisabledShape,
                pressedDisabledShape,
                hoveredDisabledShape,
            )
        ) {
            DefaultShapes
        } else {
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
        }

    /**
     * Creates a [Scale] instance with configurable scale values for different interaction states.
     *
     * @param scale The default scale value.
     * @param focusedScale The scale when the element is focused.
     * @param hoveredScale The scale when the element is hovered.
     * @param pressedScale The scale when the element is pressed.
     * @param selectedScale The scale when the element is selected.
     * @param disabledScale The scale when the element is disabled.
     * @param focusedSelectedScale The scale when the element is both focused and selected.
     * @param pressedSelectedScale The scale when the element is both pressed and selected.
     * @param hoveredSelectedScale The scale when the element is both hovered and selected.
     * @param focusedDisabledScale The scale when the element is both focused and disabled.
     * @param pressedDisabledScale The scale when the element is both pressed and disabled.
     * @param hoveredDisabledScale The scale when the element is both hovered and disabled.
     */
    @Stable
    fun scale(
        scale: Float = DefaultScale.scale,
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
        animationSpec: AnimationSpec<Float>? = DefaultScale.animationSpec,
    ): Scale =
        if (
            isDefaultScale(
                scale,
                focusedScale,
                hoveredScale,
                pressedScale,
                selectedScale,
                disabledScale,
                focusedSelectedScale,
                pressedSelectedScale,
                hoveredSelectedScale,
                focusedDisabledScale,
                pressedDisabledScale,
                hoveredDisabledScale,
                animationSpec,
            )
        ) {
            DefaultScale
        } else {
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
                animationSpec = animationSpec,
            )
        }

    /**
     * Creates a [Borders] instance with configurable borders for different interaction states.
     *
     * @param border The default border.
     * @param focusedBorder The border when the element is focused.
     * @param hoveredBorder The border when the element is hovered.
     * @param pressedBorder The border when the element is pressed.
     * @param selectedBorder The border when the element is selected.
     * @param disabledBorder The border when the element is disabled.
     * @param focusedSelectedBorder The border when the element is both focused and selected.
     * @param pressedSelectedBorder The border when the element is both pressed and selected.
     * @param hoveredSelectedBorder The border when the element is both hovered and selected.
     * @param focusedDisabledBorder The border when the element is both focused and disabled.
     * @param pressedDisabledBorder The border when the element is both pressed and disabled.
     * @param hoveredDisabledBorder The border when the element is both hovered and disabled.
     */
    @Stable
    fun borders(
        border: Border = DefaultBorders.border,
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
        if (
            isDefaultBorders(
                border,
                focusedBorder,
                hoveredBorder,
                pressedBorder,
                selectedBorder,
                disabledBorder,
                focusedSelectedBorder,
                pressedSelectedBorder,
                hoveredSelectedBorder,
                focusedDisabledBorder,
                pressedDisabledBorder,
                hoveredDisabledBorder,
            )
        ) {
            DefaultBorders
        } else {
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
        }

    /**
     * Creates an [Alpha] instance with configurable alpha values for different interaction states.
     *
     * @param alpha The default alpha value.
     * @param focusedAlpha The alpha when the element is focused.
     * @param hoveredAlpha The alpha when the element is hovered.
     * @param pressedAlpha The alpha when the element is pressed.
     * @param selectedAlpha The alpha when the element is selected.
     * @param disabledAlpha The alpha when the element is disabled.
     * @param focusedSelectedAlpha The alpha when the element is both focused and selected.
     * @param pressedSelectedAlpha The alpha when the element is both pressed and selected.
     * @param hoveredSelectedAlpha The alpha when the element is both hovered and selected.
     * @param focusedDisabledAlpha The alpha when the element is both focused and disabled.
     * @param pressedDisabledAlpha The alpha when the element is both pressed and disabled.
     * @param hoveredDisabledAlpha The alpha when the element is both hovered and disabled.
     */
    @Stable
    fun alpha(
        alpha: Float = DefaultAlpha.alpha,
        focusedAlpha: Float = alpha,
        hoveredAlpha: Float = alpha,
        pressedAlpha: Float = alpha,
        selectedAlpha: Float = alpha,
        disabledAlpha: Float = DefaultAlpha.disabledAlpha,
        focusedSelectedAlpha: Float = focusedAlpha,
        pressedSelectedAlpha: Float = pressedAlpha,
        hoveredSelectedAlpha: Float = hoveredAlpha,
        focusedDisabledAlpha: Float = disabledAlpha,
        pressedDisabledAlpha: Float = disabledAlpha,
        hoveredDisabledAlpha: Float = disabledAlpha,
    ): Alpha =
        if (
            isDefaultAlpha(
                alpha,
                focusedAlpha,
                hoveredAlpha,
                pressedAlpha,
                selectedAlpha,
                disabledAlpha,
                focusedSelectedAlpha,
                pressedSelectedAlpha,
                hoveredSelectedAlpha,
                focusedDisabledAlpha,
                pressedDisabledAlpha,
                hoveredDisabledAlpha,
            )
        ) {
            DefaultAlpha
        } else {
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

    private fun isDefaultColors(
        backgroundColor: Color,
        focusedBackgroundColor: Color,
        pressedBackgroundColor: Color,
        hoveredBackgroundColor: Color,
        disabledBackgroundColor: Color,
        selectedBackgroundColor: Color,
        focusedSelectedBackgroundColor: Color,
        pressedSelectedBackgroundColor: Color,
        hoveredSelectedBackgroundColor: Color,
        focusedDisabledBackgroundColor: Color,
        pressedDisabledBackgroundColor: Color,
        hoveredDisabledBackgroundColor: Color,
        contentColor: Color,
        focusedContentColor: Color,
        pressedContentColor: Color,
        hoveredContentColor: Color,
        disabledContentColor: Color,
        selectedContentColor: Color,
        focusedSelectedContentColor: Color,
        pressedSelectedContentColor: Color,
        hoveredSelectedContentColor: Color,
        pressedDisabledContentColor: Color,
        focusedDisabledContentColor: Color,
        hoveredDisabledContentColor: Color,
    ): Boolean =
        backgroundColor == DefaultColors.backgroundColor &&
            focusedBackgroundColor == DefaultColors.focusedBackgroundColor &&
            pressedBackgroundColor == DefaultColors.pressedBackgroundColor &&
            hoveredBackgroundColor == DefaultColors.hoveredBackgroundColor &&
            disabledBackgroundColor == DefaultColors.disabledBackgroundColor &&
            selectedBackgroundColor == DefaultColors.selectedBackgroundColor &&
            focusedSelectedBackgroundColor == DefaultColors.focusedSelectedBackgroundColor &&
            pressedSelectedBackgroundColor == DefaultColors.pressedSelectedBackgroundColor &&
            hoveredSelectedBackgroundColor == DefaultColors.hoveredSelectedBackgroundColor &&
            focusedDisabledBackgroundColor == DefaultColors.focusedDisabledBackgroundColor &&
            pressedDisabledBackgroundColor == DefaultColors.pressedDisabledBackgroundColor &&
            hoveredDisabledBackgroundColor == DefaultColors.hoveredDisabledBackgroundColor &&
            contentColor == DefaultColors.contentColor &&
            focusedContentColor == DefaultColors.focusedContentColor &&
            pressedContentColor == DefaultColors.pressedContentColor &&
            hoveredContentColor == DefaultColors.hoveredContentColor &&
            disabledContentColor == DefaultColors.disabledContentColor &&
            selectedContentColor == DefaultColors.selectedContentColor &&
            focusedSelectedContentColor == DefaultColors.focusedSelectedContentColor &&
            pressedSelectedContentColor == DefaultColors.pressedSelectedContentColor &&
            hoveredSelectedContentColor == DefaultColors.hoveredSelectedContentColor &&
            pressedDisabledContentColor == DefaultColors.pressedDisabledContentColor &&
            focusedDisabledContentColor == DefaultColors.focusedDisabledContentColor &&
            hoveredDisabledContentColor == DefaultColors.hoveredDisabledContentColor

    private fun isDefaultBorders(
        border: Border,
        focusedBorder: Border,
        hoveredBorder: Border,
        pressedBorder: Border,
        selectedBorder: Border,
        disabledBorder: Border,
        focusedSelectedBorder: Border,
        pressedSelectedBorder: Border,
        hoveredSelectedBorder: Border,
        focusedDisabledBorder: Border,
        pressedDisabledBorder: Border,
        hoveredDisabledBorder: Border,
    ): Boolean =
        border == DefaultBorders.border &&
            focusedBorder == DefaultBorders.focusedBorder &&
            hoveredBorder == DefaultBorders.hoveredBorder &&
            pressedBorder == DefaultBorders.pressedBorder &&
            selectedBorder == DefaultBorders.selectedBorder &&
            disabledBorder == DefaultBorders.disabledBorder &&
            focusedSelectedBorder == DefaultBorders.focusedSelectedBorder &&
            pressedSelectedBorder == DefaultBorders.pressedSelectedBorder &&
            hoveredSelectedBorder == DefaultBorders.hoveredSelectedBorder &&
            focusedDisabledBorder == DefaultBorders.focusedDisabledBorder &&
            pressedDisabledBorder == DefaultBorders.pressedDisabledBorder &&
            hoveredDisabledBorder == DefaultBorders.hoveredDisabledBorder

    private fun isDefaultScale(
        scale: Float,
        focusedScale: Float,
        hoveredScale: Float,
        pressedScale: Float,
        selectedScale: Float,
        disabledScale: Float,
        focusedSelectedScale: Float,
        pressedSelectedScale: Float,
        hoveredSelectedScale: Float,
        focusedDisabledScale: Float,
        pressedDisabledScale: Float,
        hoveredDisabledScale: Float,
        animationSpec: AnimationSpec<Float>?,
    ): Boolean =
        scale == DefaultScale.scale &&
            focusedScale == DefaultScale.focusedScale &&
            hoveredScale == DefaultScale.hoveredScale &&
            pressedScale == DefaultScale.pressedScale &&
            selectedScale == DefaultScale.selectedScale &&
            disabledScale == DefaultScale.disabledScale &&
            focusedSelectedScale == DefaultScale.focusedSelectedScale &&
            pressedSelectedScale == DefaultScale.pressedSelectedScale &&
            hoveredSelectedScale == DefaultScale.hoveredSelectedScale &&
            focusedDisabledScale == DefaultScale.focusedDisabledScale &&
            pressedDisabledScale == DefaultScale.pressedDisabledScale &&
            hoveredDisabledScale == DefaultScale.hoveredDisabledScale &&
            animationSpec == DefaultScale.animationSpec

    private fun isDefaultShapes(
        shape: Shape,
        focusedShape: Shape,
        hoveredShape: Shape,
        pressedShape: Shape,
        selectedShape: Shape,
        disabledShape: Shape,
        focusedSelectedShape: Shape,
        pressedSelectedShape: Shape,
        hoveredSelectedShape: Shape,
        focusedDisabledShape: Shape,
        pressedDisabledShape: Shape,
        hoveredDisabledShape: Shape,
    ): Boolean =
        shape == DefaultShapes.shape &&
            focusedShape == DefaultShapes.focusedShape &&
            hoveredShape == DefaultShapes.hoveredShape &&
            pressedShape == DefaultShapes.pressedShape &&
            selectedShape == DefaultShapes.selectedShape &&
            disabledShape == DefaultShapes.disabledShape &&
            focusedSelectedShape == DefaultShapes.focusedSelectedShape &&
            pressedSelectedShape == DefaultShapes.pressedSelectedShape &&
            hoveredSelectedShape == DefaultShapes.hoveredSelectedShape &&
            focusedDisabledShape == DefaultShapes.focusedDisabledShape &&
            pressedDisabledShape == DefaultShapes.pressedDisabledShape &&
            hoveredDisabledShape == DefaultShapes.hoveredDisabledShape

    private fun isDefaultAlpha(
        alpha: Float,
        focusedAlpha: Float,
        hoveredAlpha: Float,
        pressedAlpha: Float,
        selectedAlpha: Float,
        disabledAlpha: Float,
        focusedSelectedAlpha: Float,
        pressedSelectedAlpha: Float,
        hoveredSelectedAlpha: Float,
        focusedDisabledAlpha: Float,
        pressedDisabledAlpha: Float,
        hoveredDisabledAlpha: Float,
    ): Boolean =
        alpha == DefaultAlpha.alpha &&
            focusedAlpha == DefaultAlpha.focusedAlpha &&
            hoveredAlpha == DefaultAlpha.hoveredAlpha &&
            pressedAlpha == DefaultAlpha.pressedAlpha &&
            selectedAlpha == DefaultAlpha.selectedAlpha &&
            disabledAlpha == DefaultAlpha.disabledAlpha &&
            focusedSelectedAlpha == DefaultAlpha.focusedSelectedAlpha &&
            pressedSelectedAlpha == DefaultAlpha.pressedSelectedAlpha &&
            hoveredSelectedAlpha == DefaultAlpha.hoveredSelectedAlpha &&
            focusedDisabledAlpha == DefaultAlpha.focusedDisabledAlpha &&
            pressedDisabledAlpha == DefaultAlpha.pressedDisabledAlpha &&
            hoveredDisabledAlpha == DefaultAlpha.hoveredDisabledAlpha
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
@OptIn(ExperimentalWildApi::class)
fun Modifier.interactionStyle(
    interactionSource: InteractionSource?,
    enabled: Boolean = true,
    selected: Boolean = false,
    style: Style,
): Modifier =
    this.interactionSourceNode(
        interactionSource = interactionSource,
        childTraversalKey = StyleParentTraversalKey,
    ) then
        StyleScopeParentElement(
            enabled = enabled,
            selected = selected,
            resolver = StyleResolver.Value(style),
        ) then
        ScaleLayoutElement() then
        BorderElement() then
        BackgroundElement() then
        ShapeLayoutElement()

/**
 * Sets a non-interactive [Style] on the element.
 *
 * @param style The [Style] to apply to the element.
 * @since 0.7.0
 */
fun Modifier.staticStyle(style: Style): Modifier =
    this.staticStyleBoundary() then
        StyleScopeParentElement(
            resolver = StyleResolver.Value(style),
        ) then
        ScaleLayoutElement() then
        BorderElement() then
        BackgroundElement() then
        ShapeLayoutElement()

@Deprecated(
    message = "Use interactionStyle instead. The node-based style system is now the default.",
    replaceWith = ReplaceWith("interactionStyle(interactionSource, enabled, selected, style)"),
    level = DeprecationLevel.WARNING,
)
fun Modifier.experimentalInteractionStyle(
    interactionSource: InteractionSource?,
    enabled: Boolean = true,
    selected: Boolean = false,
    style: Style,
): Modifier =
    interactionStyle(
        interactionSource = interactionSource,
        enabled = enabled,
        selected = selected,
        style = style,
    )

/**
 * Sets a [Style] on the element that reacts to interactions from the provided [interactionSource].
 *
 * Each time the [block] is evaluated, visual properties on [StyleScope] are reset to their defaults
 * before the block runs. Omitting a property in the block leaves that property at its default for
 * that invocation; values are not carried over from prior evaluations.
 *
 * Defaults: [StyleScope.color] = [Color.Unspecified], [StyleScope.alpha] = `1f`,
 * [StyleScope.scale] = `1f`, [StyleScope.shape] = [RectangleShape], [StyleScope.border] =
 * [BorderDefaults.None], [StyleScope.scaleAnimationSpec] = `null`.
 *
 * Interaction and component flags ([StyleScope.focused], [StyleScope.hovered],
 * [StyleScope.pressed], [StyleScope.selected], [StyleScope.enabled]) are inputs read by the block;
 * they are not reset as style outputs.
 *
 * Child nodes are notified when the resolved visual output or interaction inputs change. Duplicate
 * evaluations with identical resolved state are skipped.
 *
 * Snapshot state (e.g. [androidx.compose.runtime.State] / [androidx.compose.runtime.mutableStateOf])
 * read inside the block is observed; when those values change, the block is re-evaluated without
 * requiring recomposition or an interaction event.
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
@Deprecated(
    message = "Use interactionStyle instead. The node-based style system is now the default.",
    replaceWith = ReplaceWith("interactionStyle(interactionSource, enabled, selected, style)"),
    level = DeprecationLevel.WARNING,
)
fun Modifier.experimentalInteractionStyle(
    interactionSource: InteractionSource?,
    enabled: Boolean = true,
    selected: Boolean = false,
    block: StyleScope.() -> Unit,
): Modifier =
    this.interactionSourceNode(
        interactionSource = interactionSource,
        childTraversalKey = StyleParentTraversalKey,
    ) then StyleScopeParentElement(enabled, selected, StyleResolver.Block(block)) then
        ScaleLayoutElement() then
        BorderElement() then
        BackgroundElement() then
        ShapeLayoutElement()

/**
 * Sets a [Style] on the element that reacts to interactions from the provided [interactionSource].
 *
 * Each time the [block] is evaluated, visual properties on [StyleScope] are reset to their defaults
 * before the block runs. Omitting a property in the block leaves that property at its default for
 * that invocation; values are not carried over from prior evaluations.
 *
 * Defaults: [StyleScope.color] = [Color.Unspecified], [StyleScope.alpha] = `1f`,
 * [StyleScope.scale] = `1f`, [StyleScope.shape] = [RectangleShape], [StyleScope.border] =
 * [BorderDefaults.None], [StyleScope.scaleAnimationSpec] = `null`.
 *
 * Interaction and component flags ([StyleScope.focused], [StyleScope.hovered],
 * [StyleScope.pressed], [StyleScope.selected], [StyleScope.enabled]) are inputs read by the block;
 * they are not reset as style outputs.
 *
 * Child nodes are notified when the resolved visual output or interaction inputs change. Duplicate
 * evaluations with identical resolved state are skipped.
 *
 * Snapshot state (e.g. [androidx.compose.runtime.State] / [androidx.compose.runtime.mutableStateOf])
 * read inside the block is observed; when those values change, the block is re-evaluated without
 * requiring recomposition or an interaction event.
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
fun Modifier.interactionStyle(
    interactionSource: InteractionSource?,
    enabled: Boolean = true,
    selected: Boolean = false,
    block: StyleScope.() -> Unit,
): Modifier =
    this.interactionSourceNode(
        interactionSource = interactionSource,
        childTraversalKey = StyleParentTraversalKey,
    ) then StyleScopeParentElement(enabled, selected, StyleResolver.Block(block)) then
        ScaleLayoutElement() then
        BorderElement() then
        BackgroundElement() then
        ShapeLayoutElement()
