// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalTime::class)

package io.daio.wild.foundation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusEventModifierNode
import androidx.compose.ui.focus.FocusProperties
import androidx.compose.ui.focus.FocusPropertiesModifierNode
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.invalidateFocusProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.KeyInputModifierNode
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.SemanticsModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateSemantics
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
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
) = this then PlatformFocusableElement(enabled, interactionSource) then
    HardwareEnterKeyElement(
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        observePlatformInteractions = true,
        selected = selected,
        role = role,
        onLongClickLabel = onLongClickLabel,
        onClickLabel = onClickLabel,
    )

private data class PlatformFocusableElement(
    val enabled: Boolean,
    val interactionSource: MutableInteractionSource?,
) : ModifierNodeElement<PlatformFocusableNode>() {
    override fun create(): PlatformFocusableNode = PlatformFocusableNode(enabled, interactionSource)

    override fun update(node: PlatformFocusableNode) {
        node.update(enabled, interactionSource)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "platformFocusable"
        properties["enabled"] = enabled
        properties["interactionSource"] = interactionSource
    }
}

@OptIn(ExperimentalWildApi::class)
private class PlatformFocusableNode(
    private var enabled: Boolean,
    private var interactionSource: MutableInteractionSource?,
) : DelegatingNode(),
    CompositionLocalConsumerModifierNode,
    ObserverModifierNode {
    private var focusableElement = focusableNodeElement(interactionSource)
    private val focusableNode = focusableElement.create()
    private var focusableNodeDelegated = false

    override fun onAttach() {
        updateFocusableDelegation()
    }

    override fun onObservedReadsChanged() {
        updateFocusableDelegation()
    }

    fun update(
        enabled: Boolean,
        interactionSource: MutableInteractionSource?,
    ) {
        if (this.interactionSource !== interactionSource) {
            this.interactionSource = interactionSource
            focusableElement = focusableNodeElement(interactionSource)
            focusableElement.update(focusableNode)
        }
        if (this.enabled != enabled) {
            this.enabled = enabled
            if (isAttached) updateFocusableDelegation()
        }
    }

    private fun updateFocusableDelegation() {
        var requiresHardwareInput = false
        observeReads {
            requiresHardwareInput = currentValueOf(LocalPlatformInteractions).requiresHardwareInput
        }
        val shouldDelegate = enabled || requiresHardwareInput
        if (shouldDelegate && !focusableNodeDelegated) {
            delegate(focusableNode)
            focusableNodeDelegated = true
        } else if (!shouldDelegate && focusableNodeDelegated) {
            undelegate(focusableNode)
            focusableNodeDelegated = false
        }
    }
}

// Compose 1.11.1 exposes focusable as one ordinary node element. Retain that complete node so its
// pinning, focused-bounds, semantics, and interaction lifecycle can be conditionally delegated.
@Suppress("UNCHECKED_CAST")
private fun focusableNodeElement(interactionSource: MutableInteractionSource?): ModifierNodeElement<Modifier.Node> {
    val element =
        Modifier.focusable(enabled = true, interactionSource = interactionSource)
            .foldIn<Modifier.Element?>(null) { previous, current ->
                check(previous == null) { "Expected Modifier.focusable to contain one element" }
                current
            }
    check(element is ModifierNodeElement<*>) { "Expected Modifier.focusable to use a modifier node" }
    return element as ModifierNodeElement<Modifier.Node>
}

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
    eventRepeatCount: ((KeyEvent) -> Int)? = null,
): Modifier =
    this then
        HardwareEnterKeyElement(
            enabled = enabled,
            interactionSource = interactionSource,
            onClick = onClick,
            onLongClick = onLongClick,
            onDoubleClick = onDoubleClick,
            eventRepeatCount = eventRepeatCount,
        )

private data class HardwareEnterKeyElement(
    val enabled: Boolean,
    val interactionSource: MutableInteractionSource?,
    val onClick: (() -> Unit)? = null,
    val onLongClick: (() -> Unit)? = null,
    val onDoubleClick: (() -> Unit)? = null,
    val observePlatformInteractions: Boolean = false,
    val selected: Boolean? = null,
    val role: Role? = null,
    val onLongClickLabel: String? = null,
    val onClickLabel: String? = null,
    val eventRepeatCount: ((KeyEvent) -> Int)? = null,
) : ModifierNodeElement<HardwareEnterKeyEventNode>() {
    override fun create(): HardwareEnterKeyEventNode =
        HardwareEnterKeyEventNode(
            enabled = enabled,
            interactionSource = interactionSource,
            onClick = onClick,
            onLongClick = onLongClick,
            onDoubleClick = onDoubleClick,
            observePlatformInteractions = observePlatformInteractions,
            selected = selected,
            role = role,
            onLongClickLabel = onLongClickLabel,
            onClickLabel = onClickLabel,
            eventRepeatCount = eventRepeatCount,
        )

    override fun update(node: HardwareEnterKeyEventNode) {
        node.update(
            enabled = enabled,
            interactionSource = interactionSource,
            onClick = onClick,
            onLongClick = onLongClick,
            onDoubleClick = onDoubleClick,
            observePlatformInteractions = observePlatformInteractions,
            selected = selected,
            role = role,
            onLongClickLabel = onLongClickLabel,
            onClickLabel = onClickLabel,
            eventRepeatCount = eventRepeatCount,
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "HardwareEnterKey"
        properties["enabled"] = enabled
        properties["interactionSource"] = interactionSource
        properties["onClick"] = onClick
        properties["onLongClick"] = onLongClick
        properties["onDoubleClick"] = onDoubleClick
        properties["observePlatformInteractions"] = observePlatformInteractions
        properties["selected"] = selected
        properties["role"] = role
        properties["onLongClickLabel"] = onLongClickLabel
        properties["onClickLabel"] = onClickLabel
        properties["eventRepeatCount"] = eventRepeatCount
    }
}

private class HardwareEnterKeyEventNode(
    private var enabled: Boolean,
    private var onClick: (() -> Unit)? = null,
    private var onLongClick: (() -> Unit)? = null,
    private var onDoubleClick: (() -> Unit)? = null,
    private var interactionSource: MutableInteractionSource?,
    private var observePlatformInteractions: Boolean = false,
    private var selected: Boolean? = null,
    private var role: Role? = null,
    private var onLongClickLabel: String? = null,
    private var onClickLabel: String? = null,
    private var eventRepeatCount: ((KeyEvent) -> Int)? = null,
    var timeNow: () -> Instant = { Clock.System.now() },
) : KeyInputModifierNode,
    FocusEventModifierNode,
    CompositionLocalConsumerModifierNode,
    ObserverModifierNode,
    SemanticsModifierNode,
    FocusPropertiesModifierNode,
    Modifier.Node() {
    private data class ActivePress(
        val interaction: PressInteraction.Press,
        val interactionSource: MutableInteractionSource?,
    )

    private var focusState: FocusState? = null
    private var activePress: ActivePress? = null
    private var isLongClick: Boolean = false

    // Double-click state
    private var firstClickTime: Instant = Instant.DISTANT_PAST
    private var awaitingSecondClick: Boolean = false
    private var doubleClickTimeoutJob: Job? = null
    private var doubleClickTimeout: Duration = 300.milliseconds
    private var hardwareInputRequired: Boolean = !observePlatformInteractions

    fun update(
        enabled: Boolean,
        interactionSource: MutableInteractionSource?,
        onClick: (() -> Unit)?,
        onLongClick: (() -> Unit)?,
        onDoubleClick: (() -> Unit)?,
        observePlatformInteractions: Boolean,
        selected: Boolean?,
        role: Role?,
        onLongClickLabel: String?,
        onClickLabel: String?,
        eventRepeatCount: ((KeyEvent) -> Int)?,
    ) {
        if (!enabled || (activePress != null && interactionSource !== this.interactionSource)) {
            terminateGesture()
        }

        this.enabled = enabled
        this.interactionSource = interactionSource
        this.onClick = onClick
        this.onLongClick = onLongClick
        this.onDoubleClick = onDoubleClick
        this.observePlatformInteractions = observePlatformInteractions
        this.selected = selected
        this.role = role
        this.onLongClickLabel = onLongClickLabel
        this.onClickLabel = onClickLabel
        this.eventRepeatCount = eventRepeatCount
        if (isAttached) updatePlatformConfiguration()
    }

    override fun onAttach() {
        updatePlatformConfiguration()
    }

    override fun onObservedReadsChanged() {
        updatePlatformConfiguration()
    }

    @OptIn(ExperimentalWildApi::class)
    fun updatePlatformConfiguration() {
        val previouslyRequiredHardwareInput = hardwareInputRequired
        observeReads {
            doubleClickTimeout =
                currentValueOf(LocalViewConfiguration).doubleTapTimeoutMillis.milliseconds
            hardwareInputRequired =
                !observePlatformInteractions ||
                currentValueOf(LocalPlatformInteractions).requiresHardwareInput
        }
        if (previouslyRequiredHardwareInput && !hardwareInputRequired && activePress != null) {
            resetDoubleClick()
            releasePressInteraction()
        }
        invalidateSemantics()
        invalidateFocusProperties()
    }

    override fun applyFocusProperties(focusProperties: FocusProperties) {
        if (hardwareInputRequired) {
            focusProperties.canFocus = false
        }
    }

    override fun SemanticsPropertyReceiver.applySemantics() {
        if (!observePlatformInteractions || !hardwareInputRequired) return

        this@HardwareEnterKeyEventNode.selected?.let { selected = it }
        this@HardwareEnterKeyEventNode.role?.let { role = it }
        onClick(label = onClickLabel) {
            this@HardwareEnterKeyEventNode.onClick?.invoke()
            this@HardwareEnterKeyEventNode.onClick != null
        }
        onLongClick(label = onLongClickLabel) {
            this@HardwareEnterKeyEventNode.onLongClick?.invoke()
            this@HardwareEnterKeyEventNode.onLongClick != null
        }
        if (!enabled) disabled()
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (hardwareInputRequired && HardwareEnterKeys.contains(event.key.keyCode) && enabled) {
            when (event.type) {
                KeyEventType.KeyDown -> {
                    when (eventRepeatCount?.invoke(event) ?: event.repeatCount) {
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
                    if (!isLongClick && activePress != null) {
                        releasePressInteraction()
                        if (onDoubleClick != null) {
                            handleAsDoubleClick()
                        } else {
                            onClick?.invoke()
                        }
                    } else {
                        terminateGesture()
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

            if (!focusState.isFocused && activePress != null) {
                resetDoubleClick()
                releasePressInteraction()
            }
        }
    }

    override fun onReset() {
        terminateGesture(synchronously = true)
    }

    override fun onDetach() {
        terminateGesture(synchronously = true)
    }

    private fun emitPressInteraction() {
        if (activePress != null) return

        val press = ActivePress(PressInteraction.Press(Offset.Zero), interactionSource)
        activePress = press
        coroutineScope.launch {
            press.interactionSource?.emit(press.interaction)
        }
    }

    private fun releasePressInteraction() {
        val press = activePress ?: return
        activePress = null
        coroutineScope.launch {
            press.interactionSource?.emit(PressInteraction.Release(press.interaction))
        }
    }

    private fun cancelPressInteraction(synchronously: Boolean) {
        val press = activePress ?: return
        activePress = null
        val cancel = PressInteraction.Cancel(press.interaction)
        if (synchronously) {
            press.interactionSource?.tryEmit(cancel)
        } else {
            coroutineScope.launch {
                press.interactionSource?.emit(cancel)
            }
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
                    delay(doubleClickTimeout)
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
        if (currentTime - firstClickTime <= doubleClickTimeout) {
            // We detected a second click within the double click timeout. We should cancel the
            // job handling the timeout to ensure the click is handled as a double.
            doubleClickTimeoutJob?.cancel()
        }
    }

    private fun terminateGesture(synchronously: Boolean = false) {
        resetDoubleClick()
        cancelPressInteraction(synchronously)
        isLongClick = false
    }

    private fun resetDoubleClick() {
        doubleClickTimeoutJob?.cancel()
        awaitingSecondClick = false
        firstClickTime = Instant.DISTANT_PAST
    }
}

expect val KeyEvent.repeatCount: Int
