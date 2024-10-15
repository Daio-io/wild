package io.daio.wild.container

import androidx.compose.foundation.interaction.Interaction
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
import io.daio.wild.content.ProvidesContentColor
import io.daio.wild.modifier.thenIf
import io.daio.wild.style.Style
import io.daio.wild.style.StyleDefaults
import io.daio.wild.style.clickable
import io.daio.wild.style.selectable

/**
 * [Container] is a building block component that can be used for any static element or as a
 * as an or interactive container.
 *
 * @param modifier Modifier to be applied to the layout corresponding to the container
 * @param enabled Whether or not the container is enabled.
 * @param onClick callback to be called when the container is clicked. If this and [onLongClick]
 * are null the container will not be focusable on TV.
 * @param onLongClick callback to be called when the container is long clicked. If this and
 * [onClick] are null the container will not be focusable on TV.
 * @param style The [Style] to supply to the Container. See [StyleDefaults.style].
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 * emitting [Interaction]s for this container.
 * @param content defines the [Composable] content inside the container.
 */
@Composable
@NonRestartableComposable
fun Container(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    style: Style = StyleDefaults.style(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    Box(
        modifier =
            modifier.thenIf(
                onClick != null || onLongClick != null,
                ifTrueModifier =
                    Modifier.clickable(
                        enabled = enabled,
                        style = style,
                        onClick = onClick ?: {},
                        onLongClick = onLongClick,
                        interactionSource = interactionSource,
                    ),
            ),
        propagateMinConstraints = true,
        content = {
            val focused by interactionSource.collectIsFocusedAsState()
            val pressed by interactionSource.collectIsPressedAsState()
            ProvidesContentColor(
                style.colors.contentColorFor(
                    enabled = enabled,
                    focused = focused,
                    pressed = pressed,
                    selected = false,
                ),
            ) {
                content()
            }
        },
    )
}

/**
 * [SelectableContainer] is a building block component that can be used for any selectable Tv
 * element or on its own as a selectable container. The [SelectableContainer] handles an additional
 * state compared to [Container] to indicate whether it is currently selected.
 *
 * @param onClick callback to be called when the container is clicked. If this and [onLongClick]
 * are null the container will not be focusable on TV.
 * @param modifier Modifier to be applied to the layout corresponding to the container
 * @param enabled Whether or not the container is enabled.
 * @param selected Whether or not the container is currently selected.
 * @param onLongClick callback to be called when the container is long clicked. If this and
 * [onClick] are null the container will not be focusable on TV.
 * @param style The [Style] to supply to the Container. See [StyleDefaults.style].
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 * emitting [Interaction]s for this container.
 * @param content defines the [Composable] content inside the container.
 */
@Composable
fun SelectableContainer(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    style: Style = StyleDefaults.style(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    Box(
        modifier =
            modifier.selectable(
                selected = selected,
                enabled = enabled,
                style = style,
                onClick = onClick,
                interactionSource = interactionSource,
            ),
        propagateMinConstraints = true,
        content = {
            val focused by interactionSource.collectIsFocusedAsState()
            val pressed by interactionSource.collectIsPressedAsState()
            ProvidesContentColor(
                style.colors.contentColorFor(
                    enabled = enabled,
                    focused = focused,
                    pressed = pressed,
                    selected = selected,
                ),
            ) {
                content()
            }
        },
    )
}
