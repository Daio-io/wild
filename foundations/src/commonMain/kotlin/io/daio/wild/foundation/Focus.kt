package io.daio.wild.foundation

import androidx.compose.foundation.focusGroup
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequesterModifierNode
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.requestFocus
import androidx.compose.ui.focus.restoreFocusedChild
import androidx.compose.ui.focus.saveFocusedChild
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyInputModifierNode
import androidx.compose.ui.input.key.key
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Utility [Modifier] to request focus on initial layout. This Modifier calls to [requestFocus] when
 * [onGloballyPositioned] is set, caching the fact it has been positioned to make sure any request
 * to focus is only called once after positioning.
 *
 * This can be used on a [focusGroup] or on any focusable composable.
 *
 * @param enabled Whether or not the request to focus should be enabled. Setting this to false and
 * back to true will allow focus to be re-requested.
 * @param delay Optional delay before [requestFocus] is called. Long delay is safe here as the node
 * will cancel the request to requestFocus if detracted while delayed.
 *
 *
 * @since 0.3.0
 *
 * Example:
 * LazyColumn {
 *     items(20) {
 *         LazyRow(
 *             modifier = Modifier
 *                 .fillMaxSize()
 *                 .focusGroup()
 *                 .requestInitialFocus()
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
 */
fun Modifier.requestInitialFocus(
    enabled: Boolean = true,
    delay: Long = 0L,
): Modifier {
    return if (!enabled) Modifier else RequestFocusElement(delay)
}

private class RequestFocusNode(var focusDelay: Long) :
    FocusRequesterModifierNode,
    GlobalPositionAwareModifierNode,
    Modifier.Node() {
    private var positioned: Boolean = false

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        if (!positioned && isAttached) {
            positioned = true
            requestInitialFocus(focusDelay)
        }
    }

    private fun requestInitialFocus(focusDelay: Long) {
        if (focusDelay <= 0) {
            requestFocus()
            return
        }

        coroutineScope.launch {
            delay(focusDelay)
            requestFocus()
        }
    }
}

private class RequestFocusElement(val delay: Long) : ModifierNodeElement<RequestFocusNode>() {
    override fun create() = RequestFocusNode(delay)

    override fun update(node: RequestFocusNode) {
        node.focusDelay = delay
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "requestFocus"
        properties["delay"] = delay
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RequestFocusElement

        return delay == other.delay
    }

    override fun hashCode(): Int = delay.hashCode()
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
 */
@ExperimentalComposeUiApi
fun Modifier.restoreChildFocus(onRestoreFailed: (() -> FocusRequester)? = null): Modifier =
    this.focusGroup()
        .focusRestorer(onRestoreFailed) then RestoreChildFocusElement()

private class RestoreChildNode : FocusRequesterModifierNode,
    KeyInputModifierNode,
    GlobalPositionAwareModifierNode,
    Modifier.Node() {
    private var positioned: Boolean = false

    override fun onDetach() {
        positioned = false
        super.onDetach()
    }

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        if (!positioned && isAttached) {
            restoreFocusedChild()
            positioned = true
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean = false

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onPreKeyEvent(event: KeyEvent): Boolean {
        // TODO: explore more options here, this is a trick to detect whether an
        // action button is pressed on the child of a focus group, which my commonly
        // lead to a navigation event. So saving the child just before navigation
        // allows us to restore.
        if (HardwareEnterKeys.contains(event.key.keyCode)) {
            saveFocusedChild()
        }
        return false
    }
}

private class RestoreChildFocusElement : ModifierNodeElement<RestoreChildNode>() {
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
