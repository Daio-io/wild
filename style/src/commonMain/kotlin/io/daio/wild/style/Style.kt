// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.invalidatePlacement
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

/**
 * Style class for components.
 *
 * @param backgroundColor Defines the background color based on the current state via it's [Colors.colorFor]
 * function.
 * @param scale Defines the button scale based on the current state via it's [Scale.scaleFor]
 * function.
 * @param border Defines the border based on the current state via it's [Colors.colorFor]
 * function.
 * @param shape Defines the button shape based on its current state via it's [Shapes.shapeFor]
 * function.
 * @param alpha Defines the button alpha based on its current state via it's [Alpha.alphaFor]
 * function. Note you can still set alpha yourself if needed via a [Modifier]. This parameter is
 * provided by convenience to help state driven Alpha.
 *
 * @since 0.2.0
 */
@Immutable
data class Style(
    val backgroundColor: StyleProducer<Color>,
    val contentColor: StyleProducer<Color>,
    val border: StyleProducer<Border>,
    val scale: StyleProducer<Float>,
    val shape: StyleProducer<Shape>,
    val alpha: StyleProducer<Float>,
) {
    constructor(
        colors: Colors,
        borders: Borders,
        scale: Scale,
        shapes: Shapes,
        alpha: Alpha,
    ) : this(
        backgroundColor = { enabled, focused, selected, pressed, hovered ->
            colors.colorFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        },
        contentColor = { enabled, focused, selected, pressed, hovered ->
            colors.contentColorFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        },
        border = { enabled, focused, selected, pressed, hovered ->
            borders.borderFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        },
        scale = { enabled, focused, selected, pressed, hovered ->
            scale.scaleFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        },
        shape = { enabled, focused, selected, pressed, hovered ->
            shapes.shapeFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        },
        alpha = { enabled, focused, selected, pressed, hovered ->
            alpha.alphaFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        },
    )
}

fun interface StyleProducer<T> {
    /**
     * Return the style based on the provided state parameters.
     *
     * @param enabled Whether the style is enabled.
     * @param focused Whether the style is focused.
     * @param selected Whether the style is selected.
     * @param pressed Whether the style is pressed.
     * @param hovered Whether the style is hovered.
     *
     * @return The style value.
     */
    operator fun get(
        enabled: Boolean,
        focused: Boolean,
        selected: Boolean,
        pressed: Boolean,
        hovered: Boolean,
    ): T
}

object StyleDefaults {
    private val defaultColors = colors()
    private val defaultBorders = borders()
    private val defaultScale = scale()
    private val defaultShapes = shapes()
    private val defaultAlpha = alpha()

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
            backgroundColor = { enabled, focused, selected, pressed, hovered ->
                colors.colorFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            },
            contentColor = { enabled, focused, selected, pressed, hovered ->
                colors.contentColorFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            },
            border = { enabled, focused, selected, pressed, hovered ->
                borders.borderFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            },
            scale = { enabled, focused, selected, pressed, hovered ->
                scale.scaleFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            },
            shape = { enabled, focused, selected, pressed, hovered ->
                shapes.shapeFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            },
            alpha = { enabled, focused, selected, pressed, hovered ->
                alpha.alphaFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            },
        )

    @Stable
    fun style(
        backgroundColor: StyleProducer<Color> =
            StyleProducer { enabled, focused, selected, pressed, hovered ->
                defaultColors.colorFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            },
        contentColor: StyleProducer<Color> =
            StyleProducer { enabled, focused, selected, pressed, hovered ->
                defaultColors.contentColorFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            },
        border: StyleProducer<Border> =
            StyleProducer { enabled, focused, selected, pressed, hovered ->
                defaultBorders.borderFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            },
        scale: StyleProducer<Float> =
            StyleProducer { enabled, focused, selected, pressed, hovered ->
                defaultScale.scaleFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            },
        shape: StyleProducer<Shape> =
            StyleProducer { enabled, focused, selected, pressed, hovered ->
                defaultShapes.shapeFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            },
        alpha: StyleProducer<Float> =
            StyleProducer { enabled, focused, selected, pressed, hovered ->
                defaultAlpha.alphaFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            },
    ): Style =
        Style(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            border = border,
            scale = scale,
            shape = shape,
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
        pressedShape: Shape = shape,
        disabledShape: Shape = shape,
        selectedShape: Shape = shape,
        focusedDisabledShape: Shape = disabledShape,
    ): Shapes =
        Shapes(
            shape = shape,
            focusedShape = focusedShape,
            pressedShape = pressedShape,
            disabledShape = disabledShape,
            selectedShape = selectedShape,
            focusedDisabledShape = focusedDisabledShape,
        )

    @Stable
    fun scale(
        scale: Float = 1f,
        focusedScale: Float = scale,
        pressedScale: Float = scale,
        selectedScale: Float = scale,
        disabledScale: Float = scale,
        focusedDisabledScale: Float = focusedScale,
    ): Scale =
        Scale(
            scale = scale,
            focusedScale = focusedScale,
            pressedScale = pressedScale,
            selectedScale = selectedScale,
            disabledScale = disabledScale,
            focusedDisabledScale = focusedDisabledScale,
        )

    @Stable
    fun borders(
        border: Border = BorderDefaults.None,
        focusedBorder: Border = border,
        pressedBorder: Border = border,
        selectedBorder: Border = border,
        disabledBorder: Border = border,
        focusedDisabledBorder: Border = disabledBorder,
    ): Borders =
        Borders(
            border = border,
            focusedBorder = focusedBorder,
            pressedBorder = pressedBorder,
            selectedBorder = selectedBorder,
            disabledBorder = disabledBorder,
            focusedDisabledBorder = focusedDisabledBorder,
        )

    @Stable
    fun alpha(
        alpha: Float = 1f,
        focusedAlpha: Float = alpha,
        pressedAlpha: Float = alpha,
        selectedAlpha: Float = alpha,
        disabledAlpha: Float = .6f,
        focusedDisabledAlpha: Float = disabledAlpha,
    ): Alpha =
        Alpha(
            alpha = alpha,
            focusedAlpha = focusedAlpha,
            pressedAlpha = pressedAlpha,
            selectedAlpha = selectedAlpha,
            disabledAlpha = disabledAlpha,
            focusedDisabledAlpha = focusedDisabledAlpha,
        )
}

/**
 * Sets a [Style] on the element that reacts to interactions from the provided [interactionSource].
 *
 * @param style The [Style] to apply to the element.
 * @param interactionSource The [InteractionSource] used to listen to user interactions such as
 * pressed and focus.
 * @param enabled Whether the element is currently enabled.
 * @param selected Whether the element is currently selected.
 *
 * @since 0.2.0
 */
fun Modifier.interactionStyle(
    style: Style,
    interactionSource: InteractionSource?,
    enabled: Boolean = true,
    selected: Boolean = false,
) = composed {
    val (backgroundColor, _, borders, scale, shapes, alpha) = style

    val focused = interactionSource?.collectIsFocusedAsState()?.value ?: false
    val hovered = interactionSource?.collectIsHoveredAsState()?.value ?: false
    val pressed = interactionSource?.collectIsPressedAsState()?.value ?: false

    val zIndex by animateFloatAsState(
        targetValue = if (focused) 0.5f else 0f,
        label = "zIndex",
    )

    val animatedScale by animateInteractionScaleAsState(
        pressed = pressed,
        focused = focused,
        hovered = hovered,
        targetScale =
            scale.get(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            ),
    )
    val shape =
        shapes.get(
            enabled = enabled,
            focused = focused,
            hovered = hovered,
            pressed = pressed,
            selected = selected,
        )
    val containerAlpha =
        alpha.get(
            enabled = enabled,
            focused = focused,
            hovered = hovered,
            pressed = pressed,
            selected = selected,
        )
    val border =
        borders.get(
            enabled = enabled,
            focused = focused,
            hovered = hovered,
            pressed = pressed,
            selected = selected,
        )
    this
        .graphicsLayer {
            this.scaleX = animatedScale
            this.scaleY = animatedScale
        }
        .zIndex(zIndex)
        .border(
            shape = border.forInnerShape(innerShape = shape),
            width = border.width,
            borderStroke = border.borderStroke,
            inset = border.inset,
        )
        .background(backgroundColor[enabled, focused, hovered, pressed, selected], shape)
        .graphicsLayer {
            this.alpha = containerAlpha
            this.shape = shape
            this.clip = true
            this.compositingStrategy = CompositingStrategy.Offscreen
        }
}

/**
 * Ensures the border shape takes into account the inner shape when applying an inset.
 */
internal fun Border.forInnerShape(innerShape: Shape): Shape {
    // If the Border shapes is using the default we want to follow the innerShape as out outline.
    val borderShape = if (shape == BorderDefaults.BorderDefaultShape) innerShape else shape
    return if (inset > 0.dp && innerShape is RoundedCornerShape && borderShape is RoundedCornerShape) {
        borderShape.toExpandedCornerShape(inset)
    } else {
        borderShape
    }
}

/**
 * Experimental API to apply [Style] indication to a component based on the current interaction
 * state.
 *
 * See also [interactionStyle] Modifier.
 *
 * @param style The [Style] to use as indication.
 *
 * @since 0.2.0
 */
@Stable
fun hardwareInputStyleIndication(style: Style = StyleDefaults.style()): IndicationNodeFactory = FocusStyleIndication(style)

@Stable
private class FocusStyleIndication(private val style: Style) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode = FocusStyleNode(interactionSource, style)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FocusStyleIndication) return false

        return style == other.style
    }

    override fun hashCode(): Int = style.hashCode()
}

private class FocusStyleElement(
    private val style: Style,
    private var interactionSource: InteractionSource,
) : ModifierNodeElement<FocusStyleNode>() {
    override fun create(): FocusStyleNode {
        return FocusStyleNode(
            interactionSource = interactionSource,
            style = style,
        )
    }

    override fun update(node: FocusStyleNode) {
        node.style = style
        node.interactionSource = interactionSource
    }

    override fun InspectorInfo.inspectableProperties() {
        debugInspectorInfo {
            name = "interaction-style"
            properties["style"] = style
            properties["interactionSource"] = interactionSource
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FocusStyleElement

        if (style != other.style) return false
        if (interactionSource != other.interactionSource) return false

        return true
    }

    override fun hashCode(): Int {
        var result = style.hashCode()
        result = 31 * result + interactionSource.hashCode()
        return result
    }
}

private class FocusStyleNode(
    var interactionSource: InteractionSource,
    var style: Style,
) : DrawModifierNode, ObserverModifierNode, LayoutModifierNode,
    BorderNode(
        style.border.get(
            enabled = true,
            focused = false,
            selected = false,
            pressed = false,
            hovered = false,
        ).shape,
        style.border.get(
            enabled = true,
            focused = false,
            selected = false,
            pressed = false,
            hovered = false,
        ).borderStroke,
        style.border.get(
            enabled = true,
            focused = false,
            selected = false,
            pressed = false,
            hovered = false,
        ).inset,
    ) {
    var focused = false
    var hovered = false
    var pressed = false
    var selected = false
    var enabled = true

    private var scale = style.scale.get(enabled, focused, hovered, pressed, selected)
    private var color = style.backgroundColor.get(enabled, focused, hovered, pressed, selected)
    private var alpha = style.alpha.get(enabled, focused, hovered, pressed, selected)
    private var shapes = style.shape.get(enabled, focused, hovered, pressed, selected)
    private var borders = style.border.get(enabled, focused, hovered, pressed, selected)
    private val zIndexState = AnimationState(initialValue = if (focused) 0.5f else 0f)
    private val scaleState = AnimationState(initialValue = scale)

    override val shouldAutoInvalidate: Boolean
        get() = false

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> pressed = true
                    is PressInteraction.Release -> pressed = false
                    is PressInteraction.Cancel -> pressed = false
                    is HoverInteraction.Enter -> hovered = true
                    is HoverInteraction.Exit -> hovered = false
                    is FocusInteraction.Focus -> focused = true
                    is FocusInteraction.Unfocus -> focused = false
                }
                updateStates()
                launch {
                    scaleState.animateTo(scale)
                    zIndexState.animateTo(if (focused) 0.5f else 0f)
                }
            }
        }
    }

    private fun updateStates() {
        scale = style.scale.get(enabled, focused, hovered, pressed, selected)
        color = style.backgroundColor.get(enabled, focused, hovered, pressed, selected)
        alpha = style.alpha.get(enabled, focused, hovered, pressed, selected)
        shapes = style.shape.get(enabled, focused, hovered, pressed, selected)
        borders = style.border.get(enabled, focused, hovered, pressed, selected)

        updateBorder(borders.shape, borders.borderStroke, borders.inset)

        invalidatePlacement()
    }

    fun setState(
        enabled: Boolean,
        selected: Boolean,
    ) {
        this.enabled = enabled
        this.selected = selected
        updateStates()
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0, zIndex = zIndexState.value) {
                scaleX = scaleState.value
                scaleY = scaleState.value
                shape = shapes
                this.alpha = alpha
                clip = true
                compositingStrategy = CompositingStrategy.Offscreen
            }
        }
    }

    //region Draw Background
    private var lastSize: Size = Size.Unspecified
    private var lastLayoutDirection: LayoutDirection? = null
    private var lastOutline: Outline? = null
    private var lastShape: Shape? = null

    override fun ContentDrawScope.draw() {
        drawBackground()
        drawContent()
        drawBorder()
    }

    override fun onObservedReadsChanged() {
        // Reset cached properties
        lastSize = Size.Unspecified
        lastLayoutDirection = null
        lastOutline = null
        lastShape = null
        // Invalidate draw so we build the cache again - this is needed because observeReads within
        // the draw scope obscures the state reads from the draw scope's observer
        invalidateDraw()
    }

    private fun ContentDrawScope.drawBackground() {
        if (shapes === RectangleShape) {
            // shortcut to avoid Outline calculation and allocation
            drawRect()
        } else {
            drawOutline()
        }
    }

    private fun ContentDrawScope.drawRect() {
        if (color != Color.Unspecified) drawRect(color = color)
    }

    private fun ContentDrawScope.drawOutline() {
        val outline = getOutline()
        if (color != Color.Unspecified) drawOutline(outline, color = color)
    }

    private fun ContentDrawScope.getOutline(): Outline {
        var outline: Outline? = null
        if (size == lastSize && layoutDirection == lastLayoutDirection && lastShape == shapes) {
            outline = lastOutline!!
        } else {
            // Manually observe reads so we can directly invalidate the outline when it changes
            observeReads {
                outline = shapes.createOutline(size, layoutDirection, this)
            }
        }
        lastOutline = outline
        lastSize = size
        lastLayoutDirection = layoutDirection
        lastShape = shapes
        return outline!!
    }
    //endregion
}
