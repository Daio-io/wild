package io.daio.wild.tv.base

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusEventModifierNode
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyInputModifierNode
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.launch

private val AcceptableKeys =
    intArrayOf(
        NativeKeyEvent.KEYCODE_DPAD_CENTER,
        NativeKeyEvent.KEYCODE_ENTER,
        NativeKeyEvent.KEYCODE_NUMPAD_ENTER,
    )

/**
 * Modifier to set up handling Tv remote Dpad Enter.
 */
fun Modifier.handleDpadEnter(
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    selected: Boolean = false,
): Modifier = this then TvDpadEnterElement(enabled, interactionSource, onClick, onLongClick, selected)

/**
 * Modifier to set up handling of clickable on Tv.
 */
fun Modifier.tvClickable(
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)?,
) = handleDpadEnter(
    enabled = enabled,
    interactionSource = interactionSource,
    onClick = onClick,
    onLongClick = onLongClick,
)
    // We are not using "clickable" modifier here because if we set "enabled" to false
    // then the Surface won't be focusable as well. But, in TV use case, a disabled surface
    // should be focusable
    .focusable(interactionSource = interactionSource)
    .semantics(mergeDescendants = true) {
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
    }

/**
 * Modifier to set up handling of selectables on Tv.
 */
fun Modifier.tvSelectable(
    enabled: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
    interactionSource: MutableInteractionSource,
) = handleDpadEnter(
    enabled = enabled,
    interactionSource = interactionSource,
    selected = selected,
    onClick = onClick,
    onLongClick = onLongClick,
)
    // We are not using "selectable" modifier here because if we set "enabled" to false
    // then the Surface won't be focusable as well. But, in TV use case, a disabled surface
    // should be focusable
    .focusable(interactionSource = interactionSource)
    .semantics(mergeDescendants = true) {
        this.selected = selected
        onClick {
            onClick()
            true
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
    }

private data class TvDpadEnterElement(
    val enabled: Boolean,
    val interactionSource: MutableInteractionSource,
    val onClick: (() -> Unit)? = null,
    val onLongClick: (() -> Unit)? = null,
    val selected: Boolean = false,
) : ModifierNodeElement<TvDpadEnterEventNode>() {
    override fun create(): TvDpadEnterEventNode =
        TvDpadEnterEventNode(
            enabled = enabled,
            interactionSource = interactionSource,
            onClick = onClick,
            onLongClick = onLongClick,
            selected = selected,
        )

    override fun update(node: TvDpadEnterEventNode) {
        node.enabled = enabled
        node.interactionSource = interactionSource
        node.onClick = onClick
        node.onLongClick = onLongClick
        node.selected = selected
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "TvDpadEnter"
        properties["enabled"] = enabled
        properties["interactionSource"] = interactionSource
        properties["onClick"] = onClick
        properties["onLongClick"] = onLongClick
        properties["selected"] = selected
    }
}

private class TvDpadEnterEventNode(
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
        if (AcceptableKeys.contains(event.nativeKeyEvent.keyCode)) {
            when (event.nativeKeyEvent.action) {
                NativeKeyEvent.ACTION_DOWN -> {
                    when (event.nativeKeyEvent.repeatCount) {
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

                NativeKeyEvent.ACTION_UP -> {
                    if (!isLongClick) {
                        coroutineScope.launch {
                            interactionSource.emit(
                                PressInteraction.Release(pressInteraction),
                            )
                            pressed = false
                        }
                        onClick?.invoke()
                    } else {
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
