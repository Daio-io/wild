package io.daio.wild.style

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import io.daio.wild.foundation.clickable
import io.daio.wild.foundation.selectable
import io.daio.wild.modifier.thenIfNotNull

/**
 * Interop Modifier to support either [Modifier.selectable] or [Modifier.clickable], applying
 * the correct modifier based on the requirement for hardware input. For example if a Tv device
 * is detected it adds support for hardware clicks from remote controls. This has the added support
 * for [Style], applying [interactionStyle] to update the component based on the current
 * [InteractionSource] state.
 *
 * @param enabled Whether the click action handling is enabled.
 * @param selected Optional property to set the selected state. Setting this to a value will enable
 * selectable support.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.3.1
 */
fun Modifier.interactable(
    enabled: Boolean = true,
    selected: Boolean? = null,
    style: Style? = null,
    interactionSource: MutableInteractionSource? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit),
): Modifier =
    this then
        if (selected != null) {
            Modifier.selectable(
                selected = selected,
                style = style,
                enabled = enabled,
                interactionSource = interactionSource,
                role = role,
                onLongClick = onLongClick,
                onClick = onClick,
            )
        } else {
            Modifier.clickable(
                enabled = enabled,
                style = style,
                interactionSource = interactionSource,
                role = role,
                onClick = onClick,
                onLongClick = onLongClick,
            )
        }

/**
 * Interop Modifier.clickable to apply the correct clickable modifier based on the requirement for
 * hardware input. For example if a Tv device is detected it adds support for hardware clicks from
 * remote controls. This has the added support for [Style], applying [interactionStyle] to update
 * the component based on the current [InteractionSource] state.
 *
 * @param enabled Whether the click action handling is enabled.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param style Optional [Style] to apply with the clickable.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.2.0
 */
fun Modifier.clickable(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    style: Style? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit),
) = composed {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    Modifier.clickable(
        enabled = enabled,
        interactionSource = interactionSource,
        role = role,
        onClick = onClick,
        onLongClick = onLongClick,
    ).thenIfNotNull(
        style,
        ifNotNullModifier = {
            Modifier.interactionStyle(it, interactionSource, enabled)
        },
    )
}

/**
 * Interop Modifier.selectable to apply the correct selectable modifier based on the requirement for
 * hardware input. For example if a Tv device is detected it adds support for hardware clicks from
 * remote controls. This has the added support for [Style], applying [interactionStyle] to update
 *  * the component based on the current [InteractionSource] state.
 *
 * @param selected Whether the element is currently selected.
 * @param enabled Whether the click action handling is enabled.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param style Optional [Style] to apply with the selectable.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.2.0
 */
fun Modifier.selectable(
    selected: Boolean,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    style: Style? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit),
) = composed {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    Modifier.selectable(
        selected = selected,
        enabled = enabled,
        interactionSource = interactionSource,
        onLongClick = onLongClick,
        role = role,
        onClick = onClick,
    ).thenIfNotNull(
        value = style,
        ifNotNullModifier = {
            Modifier.interactionStyle(
                style = it,
                interactionSource = interactionSource,
                enabled = enabled,
                selected = selected,
            )
        },
    )
}
