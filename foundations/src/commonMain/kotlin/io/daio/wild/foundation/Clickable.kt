// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.foundation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Indication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusEventModifierNode
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.KeyInputModifierNode
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.launch

/**
 * Interop Modifier to support either [Modifier.selectable] or [Modifier.clickable], applying
 * the correct modifier based on the requirement for hardware input. For example if a Tv device
 * is detected it adds support for hardware clicks from remote controls.
 *
 * @param enabled Whether the click action handling is enabled.
 * @param selected Optional property to set the selected state. Setting this to a value will enable
 * selectable support.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param indication Optional indication to apply with the clickable.
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
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit),
): Modifier =
    this then
        if (selected != null) {
            Modifier.selectable(
                selected = selected,
                enabled = enabled,
                interactionSource = interactionSource,
                indication = indication,
                role = role,
                onLongClick = onLongClick,
                onClick = onClick,
            )
        } else {
            Modifier.clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = indication,
                role = role,
                onClick = onClick,
                onLongClick = onLongClick,
            )
        }

/**
 * Interop Modifier.clickable to apply the correct clickable modifier based on the requirement for
 * hardware input. For example if a Tv device is detected it adds support for hardware clicks from
 * remote controls.
 *
 * @param enabled Whether the click action handling is enabled.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param indication Optional indication to apply with the clickable.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.2.0
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalWildApi::class)
fun Modifier.clickable(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit),
): Modifier =
    composed {
        @Suppress("NAME_SHADOWING")
        val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

        if (LocalPlatformInteractions.current.requiresHardwareInput) {
            Modifier.hardwareClickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = indication,
                role = role,
                onLongClick = onLongClick,
                onClick = onClick,
            )
        } else {
            Modifier.combinedClickable(
                interactionSource = interactionSource,
                indication = indication,
                enabled = enabled,
                role = role,
                onLongClick = onLongClick,
                onClick = onClick,
            ).focusable(enabled = enabled, interactionSource = interactionSource)
        }
    }

/**
 * Modifier to set up handling of click events using hardware input such as a Tv remote control.
 *
 * @param enabled Whether the click action handling is enabled.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.2.0
 */
fun Modifier.hardwareClickable(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    indication: Indication? = null,
    onClick: (() -> Unit)?,
): Modifier =
    handleHardwareInputEnter(
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick,
        onLongClick = onLongClick,
    ).hardwareInteractable(
        enabled = enabled,
        role = role,
        interactionSource = interactionSource,
        indication = indication,
        onClick = onClick,
        onLongClick = onLongClick,
    )

/**
 * Interop Modifier.selectable to apply the correct selectable modifier based on the requirement for
 * hardware input. For example if a Tv device is detected it adds support for hardware clicks from
 * remote controls.
 *
 * @param selected Whether the element is currently selected.
 * @param enabled Whether the click action handling is enabled.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param indication Optional indication to apply with the selectable.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.2.0
 */
@OptIn(ExperimentalWildApi::class)
fun Modifier.selectable(
    selected: Boolean,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit),
): Modifier =
    composed {
        @Suppress("NAME_SHADOWING")
        val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

        if (LocalPlatformInteractions.current.requiresHardwareInput) {
            Modifier.hardwareSelectable(
                selected = selected,
                enabled = enabled,
                interactionSource = interactionSource,
                role = role,
                onClick = onClick,
                onLongClick = onLongClick,
            )
        } else {
            // TODO need to support long click.
            Modifier.selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = indication,
                enabled = enabled,
                role = role,
                onClick = onClick,
            ).focusable(enabled = enabled, interactionSource = interactionSource)
        }
    }

/**
 * Modifier to set up handling of selecting events using hardware input such as a Tv remote control.
 *
 * @param selected Whether the item is currently selected.
 * @param enabled Whether the click action handling is enabled.
 * @param interactionSource The interaction source to emit interaction events to.
 * @param role The Role of the associated user interface element, typically used by Accessiblity
 * services.
 * @param onLongClick Optional callback to handle long click events.
 * @param onClick Callback when the element is clicked.
 *
 * @since 0.2.0
 */
fun Modifier.hardwareSelectable(
    selected: Boolean,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    indication: Indication? = null,
    onClick: (() -> Unit)?,
): Modifier =
    handleHardwareInputEnter(
        enabled = enabled,
        interactionSource = interactionSource,
        selected = selected,
        onClick = onClick,
        onLongClick = onLongClick,
    ).hardwareInteractable(
        enabled = enabled,
        role = role,
        interactionSource = interactionSource,
        indication = indication,
        selected = selected,
        onClick = onClick,
        onLongClick = onLongClick,
    )

private fun Modifier.hardwareInteractable(
    enabled: Boolean,
    role: Role?,
    interactionSource: MutableInteractionSource,
    indication: Indication? = null,
    selected: Boolean? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
): Modifier =

// We are not using "clickable" modifier here because if we set "enabled" to false
// then the Surface won't be focusable as well. But, in TV use case, a disabled surface
// should be focusable
    focusable(interactionSource = interactionSource)
        .semantics(mergeDescendants = true) {
            if (selected != null) {
                this.selected = selected
            }

            if (role != null) {
                this.role = role
            }
            onClick {
                onClick?.let { nnOnClick ->
                    nnOnClick()
                    return@onClick true
                }
                false
            }
            onLongClick {
                onLongClick?.let { nnOnLongClick ->
                    nnOnLongClick()
                    return@onLongClick true
                }
                false
            }
            if (!enabled) {
                disabled()
            }
        }.indication(interactionSource, indication)

/**
 * Modifier to set up handling of hardware input enter keys, such as Tv remote Dpad enter and
 * keyboard enter key.
 */
internal fun Modifier.handleHardwareInputEnter(
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    selected: Boolean = false,
): Modifier = this then HardwareEnterKeyElement(enabled, interactionSource, onClick, onLongClick, selected)

private data class HardwareEnterKeyElement(
    val enabled: Boolean,
    val interactionSource: MutableInteractionSource,
    val onClick: (() -> Unit)? = null,
    val onLongClick: (() -> Unit)? = null,
    val selected: Boolean = false,
) : ModifierNodeElement<HardwareEnterKeyEventNode>() {
    override fun create(): HardwareEnterKeyEventNode =
        HardwareEnterKeyEventNode(
            enabled = enabled,
            interactionSource = interactionSource,
            onClick = onClick,
            onLongClick = onLongClick,
            selected = selected,
        )

    override fun update(node: HardwareEnterKeyEventNode) {
        node.enabled = enabled
        node.interactionSource = interactionSource
        node.onClick = onClick
        node.onLongClick = onLongClick
        node.selected = selected
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "HardwareEnterKey"
        properties["enabled"] = enabled
        properties["interactionSource"] = interactionSource
        properties["onClick"] = onClick
        properties["onLongClick"] = onLongClick
        properties["selected"] = selected
    }
}

private class HardwareEnterKeyEventNode(
    var enabled: Boolean,
    var interactionSource: MutableInteractionSource,
    var onClick: (() -> Unit)? = null,
    var onLongClick: (() -> Unit)? = null,
    var selected: Boolean = false,
) : KeyInputModifierNode, FocusEventModifierNode, Modifier.Node() {
    val pressInteraction = PressInteraction.Press(Offset.Zero)
    private var focusState: FocusState? = null
    private var pressed: Boolean = false
    private var isLongClick: Boolean = false

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (HardwareEnterKeys.contains(event.key.keyCode)) {
            when (event.type) {
                KeyEventType.KeyDown -> {
                    when (event.repeatCount) {
                        0 ->
                            coroutineScope.launch {
                                interactionSource.emit(pressInteraction)
                                pressed = true
                            }

                        1 ->
                            onLongClick?.let {
                                isLongClick = true
                                coroutineScope.launch {
                                    interactionSource.emit(
                                        PressInteraction.Release(pressInteraction),
                                    )
                                    pressed = false
                                }
                                it.invoke()
                            }
                    }
                }

                KeyEventType.KeyUp -> {
                    if (!isLongClick && pressed) {
                        coroutineScope.launch {
                            interactionSource.emit(
                                PressInteraction.Release(pressInteraction),
                            )
                            pressed = false
                        }
                        onClick?.invoke()
                    } else {
                        pressed = false
                        isLongClick = false
                    }
                }
            }
            // Stop propagation
            return true
        }

        // Continue
        return false
    }

    override fun onPreKeyEvent(event: KeyEvent): Boolean = false

    override fun onFocusEvent(focusState: FocusState) {
        if (this.focusState != focusState) {
            this.focusState = focusState

            if (!focusState.isFocused && pressed) {
                coroutineScope.launch {
                    interactionSource.emit(PressInteraction.Release(pressInteraction))
                    pressed = false
                }
            }
        }
    }
}

expect val KeyEvent.repeatCount: Int
