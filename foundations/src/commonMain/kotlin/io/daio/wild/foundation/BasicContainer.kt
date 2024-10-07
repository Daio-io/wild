package io.daio.wild.foundation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
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

/**
 * [BasicContainer] is a building block component that can be used for any component or as a
 * standalone container. By default this container provides no interactive support.
 *
 * @param modifier Modifier to be applied to the layout corresponding to the container
 * @param enabled Whether or not the container is enabled.
 * @param style Style for the container based on its current state.
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 * emitting [Interaction]s for this container.
 * @param content defines the [Composable] content inside the container.
 */
@Composable
fun BasicContainer(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    style: Style = Style(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier =
            modifier.interactionStyle(
                interactionSource = interactionSource,
                style = style,
                enabled = enabled,
                selected = selected,
            ),
        propagateMinConstraints = true,
        content = content,
    )
}

object ContainerDefaults {
    @Stable
    fun colors(
        color: Color = Color.Black,
        focusedColor: Color = color,
        pressedColor: Color = color,
        disabledColor: Color = color,
        selectedColor: Color = color,
        focusedDisabledColor: Color = disabledColor,
    ): Colors =
        Colors(
            color = color,
            focusedColor = focusedColor,
            pressedColor = pressedColor,
            disabledColor = disabledColor,
            selectedColor = selectedColor,
            focusedDisabledColor = focusedDisabledColor,
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
    ): Scale =
        Scale(
            scale = scale,
            focusedScale = focusedScale,
            pressedScale = pressedScale,
            selectedScale = selectedScale,
        )

    @Stable
    fun borders(
        border: Border = BorderDefaults.None,
        focusedBorder: Border = border,
        pressedBorder: Border = border,
        selectedBorder: Border = border,
    ): Borders =
        Borders(
            border = border,
            focusedBorder = focusedBorder,
            pressedBorder = pressedBorder,
            selectedBorder = selectedBorder,
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

// TODO we need to be able to use the same interaction source as Clickable
// We should see about wrapping in an Indication that way it can be passed with the
// Clickable. The indication will then be passed the same interaction source.
fun Modifier.interactionStyle(
    style: Style,
    interactionSource: MutableInteractionSource? = null,
    enabled: Boolean = true,
    selected: Boolean = false,
) = composed {
    val (colors, borders, scale, shapes, alpha) = style

    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    val focused by interactionSource.collectIsFocusedAsState()
    val pressed by interactionSource.collectIsPressedAsState()

    val zIndex by animateFloatAsState(
        targetValue = if (focused) 0.5f else 0f,
        label = "zIndex",
    )

    val animatedScale by animateInteractionScaleAsState(
        targetScale =
            scale.scaleFor(
                enabled = enabled,
                focused = focused,
                pressed = pressed,
                selected = selected,
            ),
        interactionSource = interactionSource,
    )
    val shape =
        shapes.shapeFor(
            enabled = enabled,
            focused = focused,
            pressed = pressed,
            selected = selected,
        )
    val containerAlpha =
        alpha.alphaFor(
            enabled = enabled,
            focused = focused,
            pressed = pressed,
            selected = selected,
        )
    val border =
        borders.borderFor(
            enabled = enabled,
            focused = focused,
            pressed = pressed,
            selected = selected,
        )

    this
        .graphicsLayer {
            this.scaleX = animatedScale
            this.scaleY = animatedScale
        }
        .zIndex(zIndex)
        .border(border.shape, border.width, border.color, border.inset)
        .background(colors.colorFor(enabled, focused, pressed, selected), shape)
        .graphicsLayer {
            this.alpha = containerAlpha
            this.shape = shape
            this.clip = true
            this.compositingStrategy = CompositingStrategy.Offscreen
        }
}
