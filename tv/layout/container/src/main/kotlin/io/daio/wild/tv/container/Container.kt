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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import io.daio.wild.tv.foundation.Colors
import io.daio.wild.tv.foundation.Scale
import io.daio.wild.tv.foundation.Shapes
import io.daio.wild.tv.foundation.tvClickable
import io.daio.wild.tv.foundation.tvScale

@Composable
@NonRestartableComposable
fun Container(
    modifier: Modifier = Modifier,
    colors: Colors = ContainerDefaults.colors(),
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    scale: Scale = ContainerDefaults.scale(),
    shapes: Shapes = ContainerDefaults.shapes(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val pressed by interactionSource.collectIsPressedAsState()

//    val surfaceAlpha =
//        stateAlpha(enabled = enabled, focused = focused, pressed = pressed, selected = selected)

    val zIndex by animateFloatAsState(
        targetValue = if (focused) 0.5f else 0f,
        label = "zIndex",
    )

    val shape = shapes.shapeFor(enabled, focused, pressed)

    Box(
        modifier =
            modifier
                .tvClickable(
                    enabled = enabled,
                    onClick = onClick,
                    onLongClick = onLongClick,
                    interactionSource = interactionSource,
                )
                .tvScale(
                    scale = scale.scaleFor(enabled, focused, pressed),
                    interactionSource = interactionSource,
                )
                // Increasing the zIndex of this Surface when it is in the focused state to
                // avoid the glowIndication from being overlapped by subsequent items if
                // this Surface is inside a list composable (like a Row/Column).
                .zIndex(zIndex)
//                .ifElse(border != Border.None, Modifier.tvSurfaceBorder(shape, border))
                .background(colors.colorFor(enabled, focused, pressed), shape)
                .graphicsLayer {
//                    this.alpha = surfaceAlpha
                    this.shape = shape
                    this.clip = true
                    this.compositingStrategy = CompositingStrategy.Offscreen
                },
        propagateMinConstraints = true,
    ) {
        Box(
            modifier =
                Modifier.graphicsLayer {
                    this.alpha = if (!enabled) 0.8f else 1f
                },
            content = content,
        )
    }
}

object ContainerDefaults {
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

    fun scale(
        scale: Float = 1f,
        focusedScale: Float = scale,
        pressedScale: Float = scale,
    ): Scale = Scale(scale, focusedScale, pressedScale)
}
