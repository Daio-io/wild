// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.foundation

import androidx.compose.foundation.focusGroup
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequesterModifierNode
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.requestFocus
import androidx.compose.ui.focus.saveFocusedChild
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.KeyInputModifierNode
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import kotlinx.coroutines.launch

/**
 * Utility [Modifier] to request focus on initial layout. This Modifier calls to [onRequestFocus]
 * when [onGloballyPositioned] is set, caching the fact it has been positioned to make sure any
 * request to [onRequestFocus] is only called once after positioning.
 *
 * This can be used on a [focusGroup] or on any focusable composable.
 *
 * @param enabled Whether or not the request to focus should be enabled. Toggling [enabled] will
 * treat it like the modifier has only just been applied and cause [onRequestFocus] to be called
 * again. You can safely use this as a means to re-request focus.
 * @param onRequestFocus Optional suspending callback to add your own custom handling for requesting
 * focus. When providing [onRequestFocus], the [requestFocus] call will happen after the the
 * callback completes. to cancel/veto the request to focus you can return
 * [RequestFocusModifierScope.Cancel] within the [onRequestFocus] block.
 *
 * Note: When using lazy layouts you will need to wait for the children of the lazy to be laid out
 * first otherwise items will not be ready to focus. You can use the [enabled] flag or custom
 * [onRequestFocus] callback to help delay and wait for the lazy items to be visible. Alternatively
 * you can just add your own logic to request focus to the specific item you would like to request
 * focus for as it is laid out in the lazy layout.
 *
 * @since 0.3.0
 *
 * Example:
 * ```
 * Box(
 *     modifier = Modifier
 *         .fillMaxSize()
 *         .focusGroup()
 *         .requestInitialFocus(),
 * ) {
 *     repeat(20) {
 *         Button(onClick = onClick) {
 *             BasicText("Clickable")
 *         }
 *     }
 * }
 * ```
 *
 * Example custom [onRequestFocus] callback.
 * ```
 * Box(
 *     modifier = Modifier
 *         .fillMaxSize()
 *         .focusGroup()
 *         .requestInitialFocus {
 *             delay(1000)
 *             if (somethingIsNotRight) {
 *                 // Return CancelFocusRequest to veto the call to requestFocus().
 *                 return@requestInitialFocus Cancel
 *             }
 *         },
 * ) {
 *     repeat(20) {
 *         Button(onClick = onClick) {
 *             BasicText("Clickable")
 *         }
 *     }
 * }
 * ```
 */
@ExperimentalWildApi
fun Modifier.requestInitialFocus(
    enabled: Boolean = true,
    onRequestFocus: (suspend RequestFocusModifierScope.() -> Any?)? = null,
): Modifier {
    return this then (if (!enabled) Modifier else RequestFocusElement(onRequestFocus))
}

@DslMarker
private annotation class RequestFocusDsl

/**
 * Receiver scope for [requestInitialFocus].
 *
 * @since 0.3.0
 */
@RequestFocusDsl
interface RequestFocusModifierScope {
    /**
     * Return as part of [Modifier.requestInitialFocus] to veto/cancel the call to requestFocus.
     *
     * @since 0.3.0
     */
    val Cancel: Any
        get() = CancelFocus
}

private object CancelFocus

private class RequestFocusNode(
    var onRequestFocus: (suspend RequestFocusModifierScope.() -> Any?)? = null,
) :
    FocusRequesterModifierNode,
        GlobalPositionAwareModifierNode,
        RequestFocusModifierScope,
        Modifier.Node() {
    private var positioned: Boolean = false

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        if (!positioned && isAttached) {
            positioned = true
            requestFocusInternal(onRequestFocus)
        }
    }

    private fun RequestFocusNode.requestFocusInternal(onRequestFocus: (suspend RequestFocusModifierScope.() -> Any?)?) {
        // Skip CoroutineScope if it is not required.
        if (onRequestFocus == null) {
            requestFocus()
            return
        }

        coroutineScope.launch {
            val result = onRequestFocus.invoke(this@requestFocusInternal)
            // Check if the request was cancelled/vetoed.
            if (result !is CancelFocus) {
                requestFocus()
            }
        }
    }
}

private class RequestFocusElement(val onRequestFocus: (suspend RequestFocusModifierScope.() -> Any?)? = null) :
    ModifierNodeElement<RequestFocusNode>() {
    override fun create() = RequestFocusNode(onRequestFocus)

    override fun update(node: RequestFocusNode) {
        node.onRequestFocus = onRequestFocus
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "requestFocus"
        properties["onRequestFocus"] = onRequestFocus
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RequestFocusElement

        return onRequestFocus == other.onRequestFocus
    }

    override fun hashCode(): Int = onRequestFocus.hashCode()
}

/**
 * Utility [Modifier] to restore focus of a child within a focus group. A common use case
 * is to restore the last focus child when navigating from a nested LazyRow within a LazyColumn.
 *
 * This [Modifier] also applies [focusGroup] and [focusRestorer].
 *
 * @param onRestoreFailed A callback if focus restoration fails within the applied [focusRestorer].
 * @since 0.3.0
 *
 * Example:
 * ```
 * LazyColumn {
 *     items(20) {
 *         LazyRow(
 *             modifier = Modifier
 *                 .fillMaxSize()
 *                 .restoreChildFocus()
 *         ) {
 *             items(100) {
 *                 Button(onClick = onClick) {
 *                     BasicText("Clickable")
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * Example focusing the first item in a lazy row:
 *
 * ```
 * LazyColumn {
 *     items(20) {
 *         val rowFirstItemRequester = remember { FocusRequester() }
 *
 *         LazyRow(
 *             modifier = Modifier
 *                 .fillMaxSize()
 *                 // Use restore failed to only request focus to the first item
 *                 // if restoring previous focused item fails.
 *                 .restoreChildFocus(onRestoreFailed = { rowFirstItemRequester })
 *         ) {
 *             items(100) { index ->
 *                 // Only add the focus requester if it is the first item in the row.
 *                 Button(
 *                     modifier = Modifier.thenIf(
 *                         index == 0,
 *                         ifTrueModifier = Modifier.focusRequester(rowFirstItemRequester),
 *                     ),
 *                     onClick = onClick,
 *                 ) {
 *                     BasicText("Clickable")
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 */
@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalWildApi
fun Modifier.restoreChildFocus(onRestoreFailed: (() -> FocusRequester)? = null): Modifier =
    this then RestoreChildFocusElement().focusRestorer(onRestoreFailed)

private class RestoreChildNode : FocusRequesterModifierNode,
    KeyInputModifierNode,
    Modifier.Node() {
    override fun onKeyEvent(event: KeyEvent): Boolean = false

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onPreKeyEvent(event: KeyEvent): Boolean {
        // TODO: explore more options here, this is a trick to detect whether an
        // action button is pressed on the child of a focus group, which my commonly
        // lead to a navigation event. So saving the child just before navigation
        // allows us to restore.
        if (event.type == KeyEventType.KeyDown && HardwareEnterKeys.contains(event.key.keyCode)) {
            saveFocusedChild()
        }
        return false
    }
}

private class RestoreChildFocusElement :
    ModifierNodeElement<RestoreChildNode>() {
    override fun create() = RestoreChildNode()

    override fun update(node: RestoreChildNode) {}

    override fun InspectorInfo.inspectableProperties() {
        name = "restoreChild"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        return true
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }
}
