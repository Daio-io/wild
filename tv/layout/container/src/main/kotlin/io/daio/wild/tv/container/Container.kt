package io.daio.wild.tv.container

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.daio.wild.foundation.clickable
import io.daio.wild.foundation.selectable
import io.daio.wild.modifier.thenIf
import io.daio.wild.style.Alpha
import io.daio.wild.style.Colors
import io.daio.wild.style.Scale
import io.daio.wild.style.Shapes
import io.daio.wild.style.Style
import io.daio.wild.style.StyleDefaults
import io.daio.wild.style.interactionStyle

/**
 * [Container] is a building block component that can be used for any Tv element or on its own as a
 * static or interactive container.
 *
 * @param modifier Modifier to be applied to the layout corresponding to the container
 * @param enabled Whether or not the container is enabled.
 * @param onClick callback to be called when the container is clicked. If this and [onLongClick]
 * are null the container will not be focusable on TV.
 * @param onLongClick callback to be called when the container is long clicked. If this and
 * [onClick] are null the container will not be focusable on TV.
 * @param colors Defines the background color based on the current state via it's [Colors.colorFor]
 * function.
 * @param scale Defines the container scale based on the current state via it's [Scale.scaleFor]
 * function.
 * @param borders Defines the border based on the current state via it's [Colors.colorFor]
 * function.
 * @param shapes Defines the container shape based on its current state via it's [Shapes.shapeFor]
 * function.
 * @param alpha Defines the container alpha based on its current state via it's [Alpha.alphaFor]
 * function. Note you can still set alpha yourself if needed via a [Modifier]. This parameter is
 * provided by convenience to help state driven Alpha.
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
                        onClick = onClick ?: {},
                        onLongClick = onLongClick,
                        interactionSource = interactionSource,
                    ),
            ).interactionStyle(
                interactionSource = interactionSource,
                style = style,
                enabled = enabled,
                selected = false,
            ),
        propagateMinConstraints = true,
        content = content,
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
 * @param colors Defines the background color based on the current state via it's [Colors.colorFor]
 * function.
 * @param scale Defines the container scale based on the current state via it's [Scale.scaleFor]
 * function.
 * @param borders Defines the border based on the current state via it's [Colors.colorFor]
 * function.
 * @param shapes Defines the container shape based on its current state via it's [Shapes.shapeFor]
 * function.
 * @param alpha Defines the container alpha based on its current state via it's [Alpha.alphaFor]
 * function. Note you can still set alpha yourself if needed via a [Modifier]. This parameter is
 * provided by convenience to help state driven Alpha.
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 * emitting [Interaction]s for this container.
 * @param content defines the [Composable] content inside the container.
 */
@Composable
@NonRestartableComposable
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
                onClick = onClick,
                interactionSource = interactionSource,
            ).interactionStyle(
                interactionSource = interactionSource,
                style = style,
                enabled = enabled,
                selected = selected,
            ),
        propagateMinConstraints = true,
        content = content,
    )
}
