// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalTime::class)

package io.daio.wild.foundation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import io.daio.wild.modifier.thenIf
import io.daio.wild.modifier.thenIfNotNull
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
 * @param onLongClickLabel Optional text label used by accessibility services to describe the long-press action.
 * @param onLongClick Optional callback to handle long click events.
 * @param onDoubleClick Optional callback to handle double click events.
 * @param onClickLabel Optional text label used by accessibility services to describe the click action.
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
    onDoubleClick: (() -> Unit)? = null,
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
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
                onDoubleClick = onDoubleClick,
                onLongClickLabel = onLongClickLabel,
                onLongClick = onLongClick,
                onClickLabel = onClickLabel,
                onClick = onClick,
            )
        } else {
            Modifier.clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = indication,
                role = role,
                onLongClickLabel = onLongClickLabel,
                onLongClick = onLongClick,
                onDoubleClick = onDoubleClick,
                onClickLabel = onClickLabel,
                onClick = onClick,
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
 * @param onLongClickLabel Optional text label used by accessibility services to describe the long-press action.
 * @param onLongClick Optional callback to handle long click events.
 * @param onDoubleClick Optional callback to handle double click events.
 * @param onClickLabel Optional text label used by accessibility services to describe the click action.
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
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    onClick: (() -> Unit),
): Modifier =
    this.handleTvInputIfRequired(
        enabled = enabled,
        interactionSource = interactionSource,
        onClickLabel = onClickLabel,
        onClick = onClick,
        onLongClickLabel = onLongClickLabel,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
    ).combinedClickable(
        interactionSource = interactionSource,
        indication = indication,
        enabled = enabled,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        onLongClickLabel = onLongClickLabel,
        role = role,
        onClickLabel = onClickLabel,
        onClick = onClick,
    )

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
@Deprecated(
    message = "Use Modifier.clickable() instead",
    replaceWith =
        ReplaceWith(
            expression =
                """
            clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = indication,
                role = role,
                onLongClick = onLongClick,
                onClick = onClick ?: {}
            )
            """,
        ),
)
fun Modifier.hardwareClickable(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    indication: Indication? = null,
    onClick: (() -> Unit),
): Modifier =
    this.clickable(
        enabled = enabled,
        interactionSource = interactionSource,
        role = role,
        onLongClick = onLongClick,
        indication = indication,
        onClick = onClick,
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
 * @param onLongClickLabel Optional text label used by accessibility services to describe the long-press action.
 * @param onLongClick Optional callback to handle long click events.
 * @param onDoubleClick Optional callback to handle double click events.
 * @param onClickLabel Optional text label used by accessibility services to describe the click action.
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
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    onClick: (() -> Unit),
): Modifier =
    this.handleTvInputIfRequired(
        enabled = enabled,
        selected = selected,
        interactionSource = interactionSource,
        onClickLabel = onClickLabel,
        onClick = onClick,
        onLongClickLabel = onLongClickLabel,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
    ).selectable(
        selected = selected,
        interactionSource = interactionSource,
        indication = indication,
        enabled = enabled,
        role = role,
        onClick = onClick,
    )

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
@Deprecated(
    message = "Use Modifier.selectable() instead",
    replaceWith =
        ReplaceWith(
            expression =
                """
            selectable(
                selected = selected,
                enabled = enabled,
                interactionSource = interactionSource,
                indication = indication,
                role = role,
                onLongClick = onLongClick,
                onClick = onClick ?: {}
            )
            """,
        ),
)
fun Modifier.hardwareSelectable(
    selected: Boolean,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    indication: Indication? = null,
    onClick: (() -> Unit),
): Modifier =
    this.selectable(
        selected = selected,
        enabled = enabled,
        interactionSource = interactionSource,
        role = role,
        onLongClick = onLongClick,
        indication = indication,
        onClick = onClick,
    )

@OptIn(ExperimentalWildApi::class)
private fun Modifier.handleTvInputIfRequired(
    enabled: Boolean,
    interactionSource: MutableInteractionSource?,
    selected: Boolean = false,
    role: Role? = null,
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    onClick: (() -> Unit),
) = this.composed {
    val hardwareInputDevice = LocalPlatformInteractions.current.requiresHardwareInput
    // On Tv we disable click actions but maintain focusable to ensure navigation through UI.
    val focusEnabled = enabled || hardwareInputDevice

    this.focusable(enabled = focusEnabled, interactionSource = interactionSource)
        .thenIf(
            condition = hardwareInputDevice,
            ifTrueModifier =
                Modifier.handleHardwareInputEnter(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    onClick = onClick,
                    onLongClick = onLongClick,
                    onDoubleClick = onDoubleClick,
                ).hardwareSemantics(
                    enabled = enabled,
                    selected = selected,
                    role = role,
                    interactionSource = interactionSource,
                    onLongClickLabel = onLongClickLabel,
                    onLongClick = onLongClick,
                    onClickLabel = onClickLabel,
                    onClick = onClick,
                ),
        )
}

private fun Modifier.hardwareSemantics(
    enabled: Boolean,
    role: Role?,
    interactionSource: MutableInteractionSource?,
    indication: Indication? = null,
    selected: Boolean? = null,
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    onClick: (() -> Unit)? = null,
): Modifier =
    this.semantics(mergeDescendants = true) {
        if (selected != null) {
            this.selected = selected
        }

        if (role != null) {
            this.role = role
        }
        onClick(label = onClickLabel) {
            onClick?.let { nnOnClick ->
                nnOnClick()
                return@onClick true
            }
            false
        }
        onLongClick(label = onLongClickLabel) {
            onLongClick?.let { nnOnLongClick ->
                nnOnLongClick()
                return@onLongClick true
            }
            false
        }
        if (!enabled) {
            disabled()
        }
    }.thenIfNotNull(
        value = interactionSource,
        ifNotNullModifier = { interactionSource ->
            Modifier.indication(interactionSource, indication)
        },
    )

/**
 * Modifier to set up handling of hardware input enter keys, such as Tv remote Dpad enter and
 * keyboard enter key.
 */
internal fun Modifier.handleHardwareInputEnter(
    enabled: Boolean,
    interactionSource: MutableInteractionSource?,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
): Modifier =
    this then
        HardwareEnterKeyElement(
            enabled,
            interactionSource,
            onClick,
            onLongClick,
            onDoubleClick,
        )

private data class HardwareEnterKeyElement(
    val enabled: Boolean,
    val interactionSource: MutableInteractionSource?,
    val onClick: (() -> Unit)? = null,
    val onLongClick: (() -> Unit)? = null,
    val onDoubleClick: (() -> Unit)? = null,
) : ModifierNodeElement<HardwareEnterKeyEventNode>() {
    override fun create(): HardwareEnterKeyEventNode =
        HardwareEnterKeyEventNode(
            enabled = enabled,
            interactionSource = interactionSource,
            onClick = onClick,
            onLongClick = onLongClick,
            onDoubleClick = onDoubleClick,
        )

    override fun update(node: HardwareEnterKeyEventNode) {
        node.enabled = enabled
        node.interactionSource = interactionSource
        node.onClick = onClick
        node.onLongClick = onLongClick
        node.onDoubleClick = onDoubleClick
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "HardwareEnterKey"
        properties["enabled"] = enabled
        properties["interactionSource"] = interactionSource
        properties["onClick"] = onClick
        properties["onLongClick"] = onLongClick
        properties["onDoubleClick"] = onDoubleClick
    }
}

private class HardwareEnterKeyEventNode(
    var enabled: Boolean,
    var onClick: (() -> Unit)? = null,
    var onLongClick: (() -> Unit)? = null,
    var onDoubleClick: (() -> Unit)? = null,
    var interactionSource: MutableInteractionSource?,
    var timeNow: () -> Instant = { Clock.System.now() },
) : KeyInputModifierNode,
    FocusEventModifierNode,
    CompositionLocalConsumerModifierNode,
    Modifier.Node() {
    private val pressInteraction = PressInteraction.Press(Offset.Zero)
    private var focusState: FocusState? = null
    private var pressed: Boolean = false
    private var isLongClick: Boolean = false

    // Double-click state
    private var firstClickTime: Instant = Instant.DISTANT_PAST
    private var awaitingSecondClick: Boolean = false
    private var doubleClickTimeoutJob: Job? = null

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (HardwareEnterKeys.contains(event.key.keyCode) && enabled) {
            when (event.type) {
                KeyEventType.KeyDown -> {
                    when (event.repeatCount) {
                        0 -> {
                            // If we are handling double clicks we should check if this
                            // key down event is potentially a second click.
                            if (onDoubleClick != null && awaitingSecondClick) {
                                checkDoubleClickTimeout()
                            }

                            emitPressInteraction()
                        }

                        1 ->
                            onLongClick?.let {
                                isLongClick = true
                                resetDoubleClick()

                                releasePressInteraction()
                                it.invoke()
                            }
                    }
                }

                KeyEventType.KeyUp -> {
                    if (!isLongClick && pressed) {
                        releasePressInteraction()
                        if (onDoubleClick != null) {
                            handleAsDoubleClick()
                        } else {
                            onClick?.invoke()
                        }
                    } else {
                        pressed = false
                        isLongClick = false
                    }
                }
            }
            return true
        }

        return false
    }

    override fun onPreKeyEvent(event: KeyEvent): Boolean = false

    override fun onFocusEvent(focusState: FocusState) {
        if (this.focusState != focusState) {
            this.focusState = focusState

            if (!focusState.isFocused && pressed) {
                resetDoubleClick()
                releasePressInteraction()
            }
        }
    }

    override fun onReset() {
        reset()
    }

    override fun onDetach() {
        reset()
    }

    private fun emitPressInteraction() {
        coroutineScope.launch {
            interactionSource?.emit(pressInteraction)
            pressed = true
        }
    }

    private fun releasePressInteraction() {
        coroutineScope.launch {
            interactionSource?.emit(PressInteraction.Release(pressInteraction))
            pressed = false
        }
    }

    /**
     * Ensures the incoming click is attempts double click handling.
     *
     * If the incoming click is a second click the function will call [onDoubleClick] otherwise
     * a timeout will be setup to wait based on the [LocalViewConfiguration].doubleTapTimeoutMillis.
     * If no second click event is received within the timeout the default [onClick] will be called.
     */
    @OptIn(ExperimentalTime::class)
    private fun handleAsDoubleClick() {
        if (awaitingSecondClick) {
            // If we detect we were awaiting a second click we know the click is a second click and
            // onDoubleClick should be called.
            resetDoubleClick()
            onDoubleClick?.invoke()
        } else {
            resetDoubleClick()
            firstClickTime = timeNow()
            awaitingSecondClick = true
            doubleClickTimeoutJob =
                coroutineScope.launch {
                    delay(doubleClickTimeoutMillis())
                    // No second click detected before timeout so the click is not a double click
                    // and should call onClick instead.
                    if (awaitingSecondClick) {
                        awaitingSecondClick = false
                        onClick?.invoke()
                    }
                }
        }
    }

    /**
     * Checks for a potential double click and cancels any timeout delay awaiting for the second click.
     *
     * The timeout is calculated and can be overridden on the
     * [LocalViewConfiguration].doubleTapTimeoutMillis. This typically defaults to 300ms.
     */
    @OptIn(ExperimentalTime::class)
    private fun checkDoubleClickTimeout() {
        val currentTime = timeNow()
        if (currentTime - firstClickTime <= doubleClickTimeoutMillis()) {
            // We detected a second click within the double click timeout. We should cancel the
            // job handling the timeout to ensure the click is handled as a double.
            doubleClickTimeoutJob?.cancel()
        }
    }

    private fun reset() {
        resetDoubleClick()
        pressed = false
        isLongClick = false
    }

    private fun resetDoubleClick() {
        doubleClickTimeoutJob?.cancel()
        awaitingSecondClick = false
        firstClickTime = Instant.DISTANT_PAST
    }

    private fun doubleClickTimeoutMillis(): Duration = currentValueOf(LocalViewConfiguration).doubleTapTimeoutMillis.milliseconds
}

expect val KeyEvent.repeatCount: Int
