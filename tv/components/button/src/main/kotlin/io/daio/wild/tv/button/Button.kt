package io.daio.wild.tv.button

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
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
import io.daio.wild.style.Alpha
import io.daio.wild.style.Border
import io.daio.wild.style.BorderDefaults
import io.daio.wild.style.Borders
import io.daio.wild.style.Colors
import io.daio.wild.style.Scale
import io.daio.wild.style.Shapes
import io.daio.wild.style.Style
import io.daio.wild.tv.container.Container

/**
 * [Button] Simple button component for Tv.
 *
 * @param onClick callback to be called when the button is clicked.
 * @param modifier Modifier to be applied to the layout corresponding to the surface
 * @param enabled Whether or not the button is enabled.
 * @param onLongClick callback to be called when the button is long clicked.
 * @param style The style of the button.
 * @param contentPadding [PaddingValues] to be set on the inner content.
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 * emitting [Interaction]s for this button.
 * @param content defines the [Composable] content inside the button.
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    style: Style = ButtonDefaults.style(),
    contentPadding: PaddingValues = PaddingValues(8.dp),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Container(
        modifier =
            modifier
                .size(ButtonDefaults.defaultWidth, ButtonDefaults.defaultHeight)
                .semantics { role = Role.Button },
        enabled = enabled,
        style = style,
        onClick = onClick,
        onLongClick = onLongClick,
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
