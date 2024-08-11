package io.daio.wild.tv.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.Alpha
import io.daio.wild.foundation.Colors
import io.daio.wild.foundation.Scale
import io.daio.wild.foundation.Shapes
import io.daio.wild.tv.container.Container

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    colors: Colors = ButtonDefaults.colors(),
    scale: Scale = ButtonDefaults.scale(),
    shapes: Shapes = ButtonDefaults.shapes(),
    alpha: Alpha = ButtonDefaults.alpha(),
    contentPadding: PaddingValues = PaddingValues(8.dp),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Container(
        modifier =
            modifier
                .size(ButtonDefaults.defaultWidth, ButtonDefaults.defaultHeight)
                .semantics { role = Role.Button },
        colors = colors,
        enabled = enabled,
        onClick = onClick,
        onLongClick = onLongClick,
        scale = scale,
        shapes = shapes,
        alpha = alpha,
        interactionSource = interactionSource,
        content = {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .fillMaxSize()
                        .padding(contentPadding),
                contentAlignment = Alignment.Center,
                content = content,
            )
        },
    )
}

object ButtonDefaults {
    val defaultHeight: Dp = 32.dp
    val defaultWidth: Dp = 100.dp

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
    ): Scale =
        Scale(
            scale = scale,
            focusedScale = focusedScale,
            pressedScale = pressedScale,
        )

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
