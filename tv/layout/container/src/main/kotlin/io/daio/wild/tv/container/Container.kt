package io.daio.wild.tv.container

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import io.daio.wild.foundation.Alpha
import io.daio.wild.foundation.Border
import io.daio.wild.foundation.Borders
import io.daio.wild.foundation.Colors
import io.daio.wild.foundation.Scale
import io.daio.wild.foundation.Shapes
import io.daio.wild.foundation.animateInteractionScaleAsState
import io.daio.wild.foundation.wildBorder
import io.daio.wild.modifier.thenIf
import io.daio.wild.tv.base.tvClickable
import io.daio.wild.tv.base.tvSelectable

@Composable
@NonRestartableComposable
fun Container(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    colors: Colors = ContainerDefaults.colors(),
    scale: Scale = ContainerDefaults.scale(),
    borders: Borders = ContainerDefaults.borders(),
    shapes: Shapes = ContainerDefaults.shapes(),
    alpha: Alpha = ContainerDefaults.alpha(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    ContainerInternal(
        modifier =
            modifier.thenIf(
                onClick != null || onLongClick != null,
                ifTrueModifier =
                    Modifier.tvClickable(
                        enabled = enabled,
                        onClick = onClick,
                        onLongClick = onLongClick,
                        interactionSource = interactionSource,
                    ),
            ),
        enabled = enabled,
        selected = false,
        colors = colors,
        interactionSource = interactionSource,
        scale = scale,
        shapes = shapes,
        alpha = alpha,
        borders = borders,
        content = content,
    )
}

@Composable
@NonRestartableComposable
fun SelectableContainer(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    colors: Colors = ContainerDefaults.colors(),
    scale: Scale = ContainerDefaults.scale(),
    borders: Borders = ContainerDefaults.borders(),
    shapes: Shapes = ContainerDefaults.shapes(),
    alpha: Alpha = ContainerDefaults.alpha(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    ContainerInternal(
        modifier =
            modifier.tvSelectable(
                enabled = enabled,
                selected = selected,
                onClick = onClick,
                onLongClick = onLongClick,
                interactionSource = interactionSource,
            ),
        enabled = enabled,
        selected = selected,
        colors = colors,
        interactionSource = interactionSource,
        scale = scale,
        shapes = shapes,
        alpha = alpha,
        borders = borders,
        content = content,
    )
}

@Composable
private fun ContainerInternal(
    interactionSource: MutableInteractionSource?,
    scale: Scale,
    enabled: Boolean,
    selected: Boolean,
    shapes: Shapes,
    alpha: Alpha,
    borders: Borders,
    colors: Colors,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
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

    Box(
        modifier =
            modifier
                .graphicsLayer {
                    this.scaleX = animatedScale
                    this.scaleY = animatedScale
                }
                .zIndex(zIndex)
                .wildBorder(border, shape)
                .background(colors.colorFor(enabled, focused, pressed, selected), shape)
                .graphicsLayer {
                    this.alpha = containerAlpha
                    this.shape = shape
                    this.clip = true
                    this.compositingStrategy = CompositingStrategy.Offscreen
                },
        propagateMinConstraints = true,
    ) {
        Box(
            modifier = Modifier.graphicsLayer { this.alpha = containerAlpha },
            content = content,
        )
    }
}

object ContainerDefaults {
    @Stable
    fun colors(
        color: Color = Color.Black,
        focusedColor: Color = color,
        pressedColor: Color = color,
        disabledColor: Color = color,
        focusedDisabledColor: Color = disabledColor,
    ): Colors =
        Colors(
            color = color,
            focusedColor = focusedColor,
            pressedColor = pressedColor,
            disabledColor = disabledColor,
            focusedDisabledColor = focusedDisabledColor,
        )

    @Stable
    fun shapes(
        shape: Shape = RectangleShape,
        focusedShape: Shape = shape,
        pressedShape: Shape = shape,
        disabledShape: Shape = shape,
        focusedDisabledShape: Shape = disabledShape,
    ): Shapes =
        Shapes(
            shape = shape,
            focusedShape = focusedShape,
            pressedShape = pressedShape,
            disabledShape = disabledShape,
            focusedDisabledShape = focusedDisabledShape,
        )

    @Stable
    fun scale(
        scale: Float = 1f,
        focusedScale: Float = scale,
        pressedScale: Float = scale,
    ): Scale =
        Scale(
            scale = scale,
            focusedScale = focusedScale,
            pressedScale = pressedScale,
        )

    @Stable
    fun borders(
        border: Border = Border(),
        focusedBorder: Border = border,
        pressedBorder: Border = border,
    ): Borders =
        Borders(
            border = border,
            focusedBorder = focusedBorder,
            pressedBorder = pressedBorder,
        )

    @Stable
    fun alpha(
        alpha: Float = 1f,
        focusedAlpha: Float = alpha,
        pressedAlpha: Float = alpha,
        disabledAlpha: Float = .6f,
        focusedDisabledAlpha: Float = disabledAlpha,
    ): Alpha =
        Alpha(
            alpha = alpha,
            focusedAlpha = focusedAlpha,
            pressedAlpha = pressedAlpha,
            disabledAlpha = disabledAlpha,
            focusedDisabledAlpha = focusedDisabledAlpha,
        )
}
