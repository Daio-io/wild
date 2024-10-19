package io.daio.wild.foundation

import androidx.compose.foundation.focusGroup
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequesterModifierNode
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.restoreFocusedChild
import androidx.compose.ui.focus.saveFocusedChild
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyInputModifierNode
import androidx.compose.ui.input.key.key
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

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

internal class RestoreChildNode : CompositionLocalConsumerModifierNode,
    FocusRequesterModifierNode,
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
        if (positioned) {
            return
        }

        restoreFocusedChild()
        positioned = true
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
