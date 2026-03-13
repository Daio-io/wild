// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import io.daio.wild.container.Container
import io.daio.wild.style.Alpha
import io.daio.wild.style.Borders
import io.daio.wild.style.Colors
import io.daio.wild.style.Scale
import io.daio.wild.style.Shapes
import io.daio.wild.style.Style
import io.daio.wild.style.StyleDefaults

/**
 * Base toggleable component for building selection controls (switches, checkboxes, radio buttons).
 *
 * This is the selection-control equivalent of [Container] - it handles checked/unchecked state,
 * maps it to the `selected` interaction state in the [Style] system, and applies the appropriate
 * accessibility semantics.
 *
 * The [checked] state maps to the `selected` interaction state, allowing different visuals
 * for checked vs unchecked via [Style.colors], [Style.borders], etc.
 *
 * Consumers build specific controls by wrapping this with:
 * - A semantic [Role] (e.g. [Role.Switch], [Role.Checkbox], [Role.RadioButton])
 * - Default dimensions appropriate to the control type
 * - Visual content (thumb, check mark, dot, etc.)
 *
 * @param checked Whether the control is currently in the "on" or "checked" state.
 * @param onCheckedChange Callback invoked when the checked state should change.
 * @param modifier Modifier to apply to the toggleable.
 * @param enabled Whether the control is enabled.
 * @param semanticRole The accessibility [Role] for this control (e.g. [Role.Switch], [Role.Checkbox]).
 * @param style The [Style] for interaction states. Use `selected` variants for the checked state.
 * @param interactionSource Optional [MutableInteractionSource] for observing [Interaction]s.
 * @param content Visual content of the control.
 *
 * @since 0.6.0
 *
 * Example - building a Switch:
 * ```
 * Toggleable(
 *     checked = isOn,
 *     onCheckedChange = { isOn = it },
 *     semanticRole = Role.Switch,
 *     modifier = Modifier.size(width = 48.dp, height = 24.dp),
 *     style = StyleDefaults.style(
 *         colors = StyleDefaults.colors(
 *             backgroundColor = Color.Gray,
 *             selectedBackgroundColor = Color.Green,
 *         ),
 *     ),
 * ) {
 *     // Draw your thumb here
 * }
 * ```
 *
 * Example - building a Checkbox:
 * ```
 * Toggleable(
 *     checked = isChecked,
 *     onCheckedChange = { isChecked = it },
 *     semanticRole = Role.Checkbox,
 *     modifier = Modifier.size(20.dp),
 *     style = myCheckboxStyle,
 * ) {
 *     if (isChecked) {
 *         Icon(Icons.Default.Check, contentDescription = null)
 *     }
 * }
 * ```
 */
@Composable
fun Toggleable(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    semanticRole: Role? = null,
    style: Style = ToggleableDefaults.style(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val semanticsModifier =
        Modifier.semantics {
            toggleableState = ToggleableState(checked)
            if (semanticRole != null) {
                role = semanticRole
            }
        }

    Container(
        onClick = { onCheckedChange(!checked) },
        modifier = modifier.then(semanticsModifier),
        enabled = enabled,
        selected = checked,
        style = style,
        interactionSource = interactionSource,
        content = content,
    )
}

/**
 * Base selectable component for building single-selection controls (radio buttons, tabs).
 *
 * Unlike [Toggleable], this does not toggle on click - it only selects. The parent is
 * responsible for managing which item is selected (single-selection semantics).
 *
 * @param selected Whether this item is currently selected.
 * @param onClick Callback invoked when the item is clicked.
 * @param modifier Modifier to apply to the selectable.
 * @param enabled Whether the control is enabled.
 * @param semanticRole The accessibility [Role] for this control (e.g. [Role.RadioButton]).
 * @param style The [Style] for interaction states.
 * @param interactionSource Optional [MutableInteractionSource] for observing [Interaction]s.
 * @param content Visual content of the control.
 *
 * @since 0.6.0
 *
 * Example - building a RadioButton:
 * ```
 * Selectable(
 *     selected = isSelected,
 *     onClick = onSelect,
 *     semanticRole = Role.RadioButton,
 *     modifier = Modifier.size(20.dp),
 *     style = myRadioStyle,
 * ) {
 *     // Draw your selection indicator here
 * }
 * ```
 */
@Composable
fun Selectable(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    semanticRole: Role? = null,
    style: Style = ToggleableDefaults.style(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val semanticsModifier =
        Modifier.semantics {
            this.selected = selected
            if (semanticRole != null) {
                role = semanticRole
            }
        }

    Container(
        onClick = onClick,
        modifier = modifier.then(semanticsModifier),
        enabled = enabled,
        selected = selected,
        style = style,
        interactionSource = interactionSource,
        content = content,
    )
}

/**
 * Contains the default values used by [Toggleable] and [Selectable].
 *
 * @since 0.6.0
 */
object ToggleableDefaults {
    /**
     * Creates a default [Style] for toggleable/selectable controls.
     */
    fun style(
        colors: Colors = StyleDefaults.colors(),
        borders: Borders = StyleDefaults.borders(),
        scale: Scale = StyleDefaults.scale(),
        shapes: Shapes = StyleDefaults.shapes(),
        alpha: Alpha = StyleDefaults.alpha(),
    ): Style =
        StyleDefaults.style(
            colors = colors,
            borders = borders,
            scale = scale,
            shapes = shapes,
            alpha = alpha,
        )
}
