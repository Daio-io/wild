// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import io.daio.wild.modifier.thenIfNotNull
import io.daio.wild.foundation.clickable as foundationClickable
import io.daio.wild.foundation.selectable as foundationSelectable

/**
 * Interop Modifier to support either [Modifier.selectable] or [Modifier.clickable], applying
 * the correct modifier based on the requirement for hardware input. For example if a Tv device
 * is detected it adds support for hardware clicks from remote controls. This has the added support
 * for [Style], applying [experimentalInteractionStyle] to update the component based on the current
 * [InteractionSource] state.
 *
 * @param enabled Whether the click action handling is enabled.
 * @param selected Optional property to set the selected state. Setting this to a value will enable
 * selectable support.
 * @param style Optional [Style] to apply with the interactable.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onDoubleClick Optional callback to handle double click events.
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
    onDoubleClick: (() -> Unit)? = null,
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
                onDoubleClick = onDoubleClick,
                onClick = onClick,
            )
        } else {
            Modifier.clickable(
                enabled = enabled,
                style = style,
                interactionSource = interactionSource,
                role = role,
                onLongClick = onLongClick,
                onDoubleClick = onDoubleClick,
                onClick = onClick,
            )
        }

/**
 * Interop Modifier to support either [Modifier.selectable] or [Modifier.clickable], applying
 * the correct modifier based on the requirement for hardware input. For example if a Tv device
 * is detected it adds support for hardware clicks from remote controls. This has the added support
 * for a [StyleScope] block, applying [interactionStyle] to update the component based on the
 * current [InteractionSource] state.
 *
 * @param enabled Whether the click action handling is enabled.
 * @param selected Optional property to set the selected state. Setting this to a value will enable
 * selectable support.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onDoubleClick Optional callback to handle double click events.
 * @param styleBlock Optional [StyleScope] block to apply with the interactable. Required (no
 * default) so this overload does not clash with the value [style] overload.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.7.0
 */
fun Modifier.interactable(
    enabled: Boolean = true,
    selected: Boolean? = null,
    interactionSource: MutableInteractionSource? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    styleBlock: (StyleScope.() -> Unit)?,
    onClick: (() -> Unit),
): Modifier =
    this then
        if (selected != null) {
            Modifier.selectable(
                selected = selected,
                enabled = enabled,
                interactionSource = interactionSource,
                role = role,
                onLongClick = onLongClick,
                onDoubleClick = onDoubleClick,
                styleBlock = styleBlock,
                onClick = onClick,
            )
        } else {
            Modifier.clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                role = role,
                onLongClick = onLongClick,
                onDoubleClick = onDoubleClick,
                styleBlock = styleBlock,
                onClick = onClick,
            )
        }

/**
 * Interop Modifier to support either [Modifier.selectable] or [Modifier.clickable], applying
 * the correct modifier based on the requirement for hardware input. For example if a Tv device
 * is detected it adds support for hardware clicks from remote controls. This has the added support
 * for [Style], applying [experimentalInteractionStyle] to update the component based on the current
 * [InteractionSource] state.
 *
 * @param enabled Whether the click action handling is enabled.
 * @param selected Optional property to set the selected state. Setting this to a value will enable
 * selectable support.
 * @param style Optional [Style] block to apply with the interactable.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onDoubleClick Optional callback to handle double click events.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.4.0
 */
@Deprecated(
    message = "Use interactable instead. The node-based style system is now the default.",
    replaceWith =
        ReplaceWith(
            "interactable(enabled, selected, interactionSource, role, onLongClick, onDoubleClick, styleBlock = style, onClick = onClick)",
        ),
    level = DeprecationLevel.WARNING,
)
fun Modifier.experimentalInteractable(
    enabled: Boolean = true,
    selected: Boolean? = null,
    style: (StyleScope.() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: (() -> Unit),
): Modifier =
    interactable(
        enabled = enabled,
        selected = selected,
        interactionSource = interactionSource,
        role = role,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        styleBlock = style,
        onClick = onClick,
    )

/**
 * Interop Modifier to support either [Modifier.selectable] or [Modifier.clickable], applying
 * the correct modifier based on the requirement for hardware input. For example if a Tv device
 * is detected it adds support for hardware clicks from remote controls. This has the added support
 * for [Style], applying [experimentalInteractionStyle] to update the component based on the current
 * [InteractionSource] state.
 *
 * @param enabled Whether the click action handling is enabled.
 * @param selected Optional property to set the selected state. Setting this to a value will enable
 * selectable support.
 * @param style Optional [Style] to apply with the interactable.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.4.0
 */
@Deprecated(
    message = "Use interactable instead. The node-based style system is now the default.",
    replaceWith = ReplaceWith("interactable(enabled, selected, style, interactionSource, role, onLongClick, onDoubleClick, onClick)"),
    level = DeprecationLevel.WARNING,
)
fun Modifier.experimentalInteractable(
    enabled: Boolean = true,
    selected: Boolean? = null,
    style: Style? = null,
    interactionSource: MutableInteractionSource? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: (() -> Unit),
): Modifier =
    this then
        if (selected != null) {
            Modifier.experimentalSelectable(
                selected = selected,
                style = style,
                enabled = enabled,
                interactionSource = interactionSource,
                role = role,
                onLongClick = onLongClick,
                onDoubleClick = onDoubleClick,
                onClick = onClick,
            )
        } else {
            Modifier.experimentalClickable(
                enabled = enabled,
                style = style,
                interactionSource = interactionSource,
                role = role,
                onClick = onClick,
                onDoubleClick = onDoubleClick,
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
 * @param onDoubleClick Optional callback to handle double click events.
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
    onDoubleClick: (() -> Unit)? = null,
    onClick: (() -> Unit),
): Modifier =
    if (interactionSource != null) {
        clickableWithStyle(enabled, interactionSource, style, role, onLongClick, onDoubleClick, onClick)
    } else {
        // Compatibility path for callers relying on the nullable-source API. Keep one source for
        // the lifetime of this modifier instance and share it between input and style observation.
        composed {
            val rememberedInteractionSource = remember { MutableInteractionSource() }
            clickableWithStyle(enabled, rememberedInteractionSource, style, role, onLongClick, onDoubleClick, onClick)
        }
    }

/**
 * Interop Modifier.clickable to apply the correct clickable modifier based on the requirement for
 * hardware input. For example if a Tv device is detected it adds support for hardware clicks from
 * remote controls. This has the added support for a [StyleScope] block, applying [interactionStyle]
 * to update the component based on the current [InteractionSource] state.
 *
 * @param enabled Whether the click action handling is enabled.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onDoubleClick Optional callback to handle double click events.
 * @param styleBlock Optional [StyleScope] block to apply with the clickable. Required (no default)
 * so this overload does not clash with the value [style] overload.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.7.0
 */
fun Modifier.clickable(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    styleBlock: (StyleScope.() -> Unit)?,
    onClick: (() -> Unit),
): Modifier =
    if (interactionSource != null) {
        clickableWithStyleBlock(enabled, interactionSource, styleBlock, role, onLongClick, onDoubleClick, onClick)
    } else {
        // Compatibility path for callers relying on the nullable-source API. Keep one source for
        // the lifetime of this modifier instance and share it between input and style observation.
        composed {
            val rememberedInteractionSource = remember { MutableInteractionSource() }
            clickableWithStyleBlock(
                enabled,
                rememberedInteractionSource,
                styleBlock,
                role,
                onLongClick,
                onDoubleClick,
                onClick,
            )
        }
    }

/**
 * Interop Modifier.clickable to apply the correct clickable modifier based on the requirement for
 * hardware input. For example if a Tv device is detected it adds support for hardware clicks from
 * remote controls. This has the added support for [Style], applying [experimentalInteractionStyle] to update
 * the component based on the current [InteractionSource] state.
 *
 * @param enabled Whether the click action handling is enabled.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param style Optional [Style] to apply with the clickable.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onDoubleClick Optional callback to handle double click events.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.4.0
 */
@Deprecated(
    message = "Use clickable instead. The node-based style system is now the default.",
    replaceWith = ReplaceWith("clickable(enabled, interactionSource, style, role, onLongClick, onDoubleClick, onClick)"),
    level = DeprecationLevel.WARNING,
)
fun Modifier.experimentalClickable(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    style: Style? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: (() -> Unit),
): Modifier =
    if (interactionSource != null) {
        experimentalClickableWithStyle(enabled, interactionSource, style, role, onLongClick, onDoubleClick, onClick)
    } else {
        // Compatibility path for callers relying on the nullable-source API. Keep one source for
        // the lifetime of this modifier instance and share it between input and style observation.
        composed {
            val rememberedInteractionSource = remember { MutableInteractionSource() }
            experimentalClickableWithStyle(
                enabled,
                rememberedInteractionSource,
                style,
                role,
                onLongClick,
                onDoubleClick,
                onClick,
            )
        }
    }

/**
 * Interop Modifier.clickable to apply the correct clickable modifier based on the requirement for
 * hardware input. For example if a Tv device is detected it adds support for hardware clicks from
 * remote controls. This has the added support for [Style], applying [experimentalInteractionStyle] to update
 * the component based on the current [InteractionSource] state.
 *
 * @param enabled Whether the click action handling is enabled.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param style Optional [Style] block to apply with the clickable.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.4.0
 */
@Deprecated(
    message = "Use clickable instead. The node-based style system is now the default.",
    replaceWith =
        ReplaceWith(
            "clickable(enabled, interactionSource, role, onLongClick, onDoubleClick, styleBlock = style, onClick = onClick)",
        ),
    level = DeprecationLevel.WARNING,
)
fun Modifier.experimentalClickable(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    style: (StyleScope.() -> Unit)? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: (() -> Unit),
): Modifier =
    clickable(
        enabled = enabled,
        interactionSource = interactionSource,
        role = role,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        styleBlock = style,
        onClick = onClick,
    )

/**
 * Interop Modifier.selectable to apply the correct selectable modifier based on the requirement for
 * hardware input. For example if a Tv device is detected it adds support for hardware clicks from
 * remote controls. This has the added support for [Style], applying [experimentalInteractionStyle] to update
 *  * the component based on the current [InteractionSource] state.
 *
 * @param selected Whether the element is currently selected.
 * @param enabled Whether the click action handling is enabled.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param style Optional [Style] to apply with the selectable.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onDoubleClick Optional callback to handle double click events.
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
    onDoubleClick: (() -> Unit)? = null,
    onClick: (() -> Unit),
): Modifier =
    if (interactionSource != null) {
        selectableWithStyle(selected, enabled, interactionSource, style, role, onLongClick, onDoubleClick, onClick)
    } else {
        // Compatibility path for callers relying on the nullable-source API. Keep one source for
        // the lifetime of this modifier instance and share it between input and style observation.
        composed {
            val rememberedInteractionSource = remember { MutableInteractionSource() }
            selectableWithStyle(
                selected,
                enabled,
                rememberedInteractionSource,
                style,
                role,
                onLongClick,
                onDoubleClick,
                onClick,
            )
        }
    }

/**
 * Interop Modifier.selectable to apply the correct selectable modifier based on the requirement for
 * hardware input. For example if a Tv device is detected it adds support for hardware clicks from
 * remote controls. This has the added support for a [StyleScope] block, applying [interactionStyle]
 * to update the component based on the current [InteractionSource] state.
 *
 * @param selected Whether the element is currently selected.
 * @param enabled Whether the click action handling is enabled.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onDoubleClick Optional callback to handle double click events.
 * @param styleBlock Optional [StyleScope] block to apply with the selectable. Required (no default)
 * so this overload does not clash with the value [style] overload.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.7.0
 */
fun Modifier.selectable(
    selected: Boolean,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    styleBlock: (StyleScope.() -> Unit)?,
    onClick: (() -> Unit),
): Modifier =
    if (interactionSource != null) {
        selectableWithStyleBlock(
            selected,
            enabled,
            interactionSource,
            styleBlock,
            role,
            onLongClick,
            onDoubleClick,
            onClick,
        )
    } else {
        // Compatibility path for callers relying on the nullable-source API. Keep one source for
        // the lifetime of this modifier instance and share it between input and style observation.
        composed {
            val rememberedInteractionSource = remember { MutableInteractionSource() }
            selectableWithStyleBlock(
                selected,
                enabled,
                rememberedInteractionSource,
                styleBlock,
                role,
                onLongClick,
                onDoubleClick,
                onClick,
            )
        }
    }

/**
 * Interop Modifier.selectable to apply the correct selectable modifier based on the requirement for
 * hardware input. For example if a Tv device is detected it adds support for hardware clicks from
 * remote controls. This has the added support for [Style], applying [experimentalInteractionStyle] to update
 *  * the component based on the current [InteractionSource] state.
 *
 * @param selected Whether the element is currently selected.
 * @param enabled Whether the click action handling is enabled.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param style Optional [Style] to apply with the selectable.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onDoubleClick Optional callback to handle double click events.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.4.0
 */
@Deprecated(
    message = "Use selectable instead. The node-based style system is now the default.",
    replaceWith = ReplaceWith("selectable(selected, enabled, interactionSource, style, role, onLongClick, onDoubleClick, onClick)"),
    level = DeprecationLevel.WARNING,
)
fun Modifier.experimentalSelectable(
    selected: Boolean,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    style: Style? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: (() -> Unit),
): Modifier =
    if (interactionSource != null) {
        experimentalSelectableWithStyle(
            selected,
            enabled,
            interactionSource,
            style,
            role,
            onLongClick,
            onDoubleClick,
            onClick,
        )
    } else {
        // Compatibility path for callers relying on the nullable-source API. Keep one source for
        // the lifetime of this modifier instance and share it between input and style observation.
        composed {
            val rememberedInteractionSource = remember { MutableInteractionSource() }
            experimentalSelectableWithStyle(
                selected,
                enabled,
                rememberedInteractionSource,
                style,
                role,
                onLongClick,
                onDoubleClick,
                onClick,
            )
        }
    }

private fun Modifier.clickableWithStyle(
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    style: Style?,
    role: Role?,
    onLongClick: (() -> Unit)?,
    onDoubleClick: (() -> Unit)?,
    onClick: () -> Unit,
): Modifier =
    foundationClickable(
        enabled = enabled,
        interactionSource = interactionSource,
        role = role,
        onClick = onClick,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
    ).thenIfNotNull(style, ifNotNullModifier = {
        Modifier.interactionStyle(interactionSource, enabled, style = it)
    })

private fun Modifier.clickableWithStyleBlock(
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    styleBlock: (StyleScope.() -> Unit)?,
    role: Role?,
    onLongClick: (() -> Unit)?,
    onDoubleClick: (() -> Unit)?,
    onClick: () -> Unit,
): Modifier =
    foundationClickable(
        enabled = enabled,
        interactionSource = interactionSource,
        role = role,
        onClick = onClick,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
    ).thenIfNotNull(styleBlock, ifNotNullModifier = {
        Modifier.interactionStyle(interactionSource, enabled, block = it)
    })

private fun Modifier.experimentalClickableWithStyle(
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    style: Style?,
    role: Role?,
    onLongClick: (() -> Unit)?,
    onDoubleClick: (() -> Unit)?,
    onClick: () -> Unit,
): Modifier =
    foundationClickable(
        enabled = enabled,
        interactionSource = interactionSource,
        role = role,
        onClick = onClick,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
    ).thenIfNotNull(style, ifNotNullModifier = {
        Modifier.experimentalInteractionStyle(interactionSource, enabled, style = it)
    })

private fun Modifier.selectableWithStyle(
    selected: Boolean,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    style: Style?,
    role: Role?,
    onLongClick: (() -> Unit)?,
    onDoubleClick: (() -> Unit)?,
    onClick: () -> Unit,
): Modifier =
    foundationSelectable(
        selected = selected,
        enabled = enabled,
        interactionSource = interactionSource,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        role = role,
        onClick = onClick,
    ).thenIfNotNull(style, ifNotNullModifier = {
        Modifier.interactionStyle(
            style = it,
            interactionSource = interactionSource,
            enabled = enabled,
            selected = selected,
        )
    })

private fun Modifier.selectableWithStyleBlock(
    selected: Boolean,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    styleBlock: (StyleScope.() -> Unit)?,
    role: Role?,
    onLongClick: (() -> Unit)?,
    onDoubleClick: (() -> Unit)?,
    onClick: () -> Unit,
): Modifier =
    foundationSelectable(
        selected = selected,
        enabled = enabled,
        interactionSource = interactionSource,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        role = role,
        onClick = onClick,
    ).thenIfNotNull(styleBlock, ifNotNullModifier = {
        Modifier.interactionStyle(
            interactionSource = interactionSource,
            enabled = enabled,
            selected = selected,
            block = it,
        )
    })

private fun Modifier.experimentalSelectableWithStyle(
    selected: Boolean,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    style: Style?,
    role: Role?,
    onLongClick: (() -> Unit)?,
    onDoubleClick: (() -> Unit)?,
    onClick: () -> Unit,
): Modifier =
    foundationSelectable(
        selected = selected,
        enabled = enabled,
        interactionSource = interactionSource,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        role = role,
        onClick = onClick,
    ).thenIfNotNull(style, ifNotNullModifier = {
        Modifier.experimentalInteractionStyle(
            style = it,
            interactionSource = interactionSource,
            enabled = enabled,
            selected = selected,
        )
    })

/**
 * Interop Modifier.selectable to apply the correct selectable modifier based on the requirement for
 * hardware input. For example if a Tv device is detected it adds support for hardware clicks from
 * remote controls. This has the added support for [Style], applying [experimentalInteractionStyle] to update
 *  * the component based on the current [InteractionSource] state.
 *
 * @param selected Whether the element is currently selected.
 * @param enabled Whether the click action handling is enabled.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param style Optional [Style] block apply with the selectable.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.4.0
 */
@Deprecated(
    message = "Use selectable instead. The node-based style system is now the default.",
    replaceWith =
        ReplaceWith(
            "selectable(selected, enabled, interactionSource, role, onLongClick, onDoubleClick, styleBlock = style, onClick = onClick)",
        ),
    level = DeprecationLevel.WARNING,
)
fun Modifier.experimentalSelectable(
    selected: Boolean,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    style: (StyleScope.() -> Unit)? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: (() -> Unit),
): Modifier =
    selectable(
        selected = selected,
        enabled = enabled,
        interactionSource = interactionSource,
        role = role,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        styleBlock = style,
        onClick = onClick,
    )
