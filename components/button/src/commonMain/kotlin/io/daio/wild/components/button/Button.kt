package io.daio.wild.components.button

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
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
import io.daio.wild.style.Style
import io.daio.wild.style.StyleDefaults

/**
 * Simple button component for TV.
 *
 * @param onClick Callback to be invoked when the button is clicked.
 * @param modifier Modifier to be applied to the layout corresponding to the surface.
 * @param enabled Whether the button is enabled.
 * @param onLongClick Callback to be invoked when the button is long clicked.
 * @param style The style of the button.
 * @param contentPadding [PaddingValues] to be set on the inner content.
 * @param interactionSource Optional [MutableInteractionSource] for observing and emitting [Interaction]s.
 * @param content Defines the [Composable] content inside the button.
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
    style: Style = StyleDefaults.style(),
    contentPadding: PaddingValues = PaddingValues(8.dp),
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
}
