// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import io.daio.wild.container.Container
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.Alpha
import io.daio.wild.style.Borders
import io.daio.wild.style.Colors
import io.daio.wild.style.ComponentStyleScope
import io.daio.wild.style.Scale
import io.daio.wild.style.Shapes
import io.daio.wild.style.Style
import io.daio.wild.style.StyleDefaults
import io.daio.wild.style.StyleSpec
import io.daio.wild.style.styleSpec as newStyleSpec

/**
 * Base toggleable component for building selection controls (switches, checkboxes, radio buttons).
 *
 * This is the selection-control equivalent of [Container] - it handles checked/unchecked state,
 * maps it to the `selected` interaction state in the [Style] system, and applies the appropriate
 * accessibility semantics.
 *
 * The [checked] state maps to the `selected` interaction state, allowing different visuals
 * for checked vs unchecked via [Style.colors], [Style.borders], etc.
 * The Boolean overload maps `true` to [ToggleableState.On] and `false` to
 * [ToggleableState.Off]. Use the [ToggleableState] overload when an indeterminate state is
 * required.
 *
 * Consumers build specific controls by wrapping this with:
 * - A semantic role via `Modifier.semantics { role = Role.Switch }` etc.
 * - Default dimensions appropriate to the control type
 * - Visual content (thumb, check mark, dot, etc.)
 *
 * @param checked Whether the control is currently in the "on" or "checked" state.
 * @param onCheckedChange Callback invoked when the checked state should change.
 * @param modifier Modifier to apply to the toggleable.
 * @param enabled Whether the control is enabled.
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
 *     modifier = Modifier
 *         .size(width = 48.dp, height = 24.dp)
 *         .semantics { role = Role.Switch },
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
 *     modifier = Modifier
 *         .size(20.dp)
 *         .semantics { role = Role.Checkbox },
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
    style: Style = ToggleableDefaults.style(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    ToggleableImpl(
        state = ToggleableState(checked),
        onClick = { onCheckedChange(!checked) },
        modifier = modifier,
        enabled = enabled,
        style = style,
        interactionSource = interactionSource,
        content = content,
    )
}

/**
 * Boolean [Toggleable] with an experimental [StyleSpec] for chrome and content-color propagation.
 *
 * @param checked Whether the control is currently checked.
 * @param onCheckedChange Callback invoked when the checked state should change.
 * @param style Required [StyleSpec] distinguishing this overload from the value [Style] overload.
 * Prefer [ToggleableDefaults.styleSpec] when building the Spec.
 * @param modifier Modifier to apply to the toggleable.
 * @param enabled Whether the control is enabled.
 * @param interactionSource Optional interaction source; when null, Container owns one.
 * @param content Visual content of the control.
 *
 * Example:
 * ```
 * val spec = ToggleableDefaults.styleSpec {
 *     if (selected) scale = 1.1f
 * }
 * Toggleable(checked = checked, onCheckedChange = onCheckedChange, style = spec) {
 *     // Draw checkbox mark
 * }
 * ```
 *
 * @since 0.8.0
 */
@ExperimentalWildApi
@Composable
fun Toggleable(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    style: StyleSpec,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    ToggleableImpl(
        state = ToggleableState(checked),
        onClick = { onCheckedChange(!checked) },
        modifier = modifier,
        enabled = enabled,
        style = style,
        interactionSource = interactionSource,
        content = content,
    )
}

/**
 * Base toggleable component that supports on, off, and indeterminate states.
 *
 * The caller owns the state transition policy: [onClick] is invoked once for an enabled
 * interaction, and this overload does not cycle [state] automatically. The [state] is exposed
 * through `toggleableState`, while [ToggleableState.On] and [ToggleableState.Indeterminate] both
 * use the selected [Style] branch. A disabled [Container] suppresses [onClick].
 *
 * @param state The current on, off, or indeterminate state.
 * @param onClick Callback invoked when the enabled control is clicked.
 * @param modifier Modifier to apply to the toggleable.
 * @param enabled Whether the control is enabled.
 * @param style The [Style] for interaction states. Use `selected` variants for on and
 *     indeterminate states.
 * @param interactionSource Optional [MutableInteractionSource] for observing [Interaction]s.
 * @param content Visual content of the control.
 *
 * @since 0.6.0
 *
 * Example - building a caller-controlled tri-state checkbox:
 * ```
 * var state by remember { mutableStateOf(ToggleableState.Off) }
 * Toggleable(
 *     state = state,
 *     onClick = {
 *         state = when (state) {
 *             ToggleableState.Off -> ToggleableState.On
 *             ToggleableState.On -> ToggleableState.Indeterminate
 *             ToggleableState.Indeterminate -> ToggleableState.Off
 *         }
 *     },
 * ) {
 *     // Render a distinct mark for ToggleableState.Indeterminate.
 * }
 * ```
 */
@Composable
fun Toggleable(
    state: ToggleableState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: Style = ToggleableDefaults.style(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    ToggleableImpl(
        state = state,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        style = style,
        interactionSource = interactionSource,
        content = content,
    )
}

/**
 * Tri-state [Toggleable] with an experimental [StyleSpec] for chrome and content-color propagation.
 *
 * @param state The current on, off, or indeterminate state.
 * @param onClick Callback invoked when the enabled control is clicked.
 * @param style Required [StyleSpec] distinguishing this overload from the value [Style] overload.
 * Prefer [ToggleableDefaults.styleSpec] when building the Spec.
 * @param modifier Modifier to apply to the toggleable.
 * @param enabled Whether the control is enabled.
 * @param interactionSource Optional interaction source; when null, Container owns one.
 * @param content Visual content of the control.
 *
 * Example:
 * ```
 * val spec = ToggleableDefaults.styleSpec {
 *     if (selected) contentColor = Color.Green
 * }
 * Toggleable(state = state, onClick = onCycle, style = spec) {
 *     // Render On / Off / Indeterminate marks
 * }
 * ```
 *
 * @since 0.8.0
 */
@ExperimentalWildApi
@Composable
fun Toggleable(
    state: ToggleableState,
    onClick: () -> Unit,
    style: StyleSpec,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    ToggleableImpl(
        state = state,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        style = style,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
private fun ToggleableImpl(
    state: ToggleableState,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    style: Style,
    interactionSource: MutableInteractionSource?,
    content: @Composable BoxScope.() -> Unit,
) {
    Container(
        onClick = onClick,
        modifier = modifier.semantics { toggleableState = state },
        enabled = enabled,
        selected = state != ToggleableState.Off,
        style = style,
        interactionSource = interactionSource,
        content = content,
    )
}

@OptIn(ExperimentalWildApi::class)
@Composable
private fun ToggleableImpl(
    state: ToggleableState,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    style: StyleSpec,
    interactionSource: MutableInteractionSource?,
    content: @Composable BoxScope.() -> Unit,
) {
    Container(
        onClick = onClick,
        modifier = modifier.semantics { toggleableState = state },
        enabled = enabled,
        selected = state != ToggleableState.Off,
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
 *     modifier = Modifier
 *         .size(20.dp)
 *         .semantics { role = Role.RadioButton },
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
    style: Style = SelectableDefaults.style(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Container(
        onClick = onClick,
        modifier =
            modifier.semantics {
                this.selected = selected
            },
        enabled = enabled,
        selected = selected,
        style = style,
        interactionSource = interactionSource,
        content = content,
    )
}

/**
 * [Selectable] with an experimental [StyleSpec] for chrome and content-color propagation.
 *
 * @param selected Whether this item is currently selected.
 * @param onClick Callback invoked when the item is clicked.
 * @param style Required [StyleSpec] distinguishing this overload from the value [Style] overload.
 * Prefer [SelectableDefaults.styleSpec] when building the Spec.
 * @param modifier Modifier to apply to the selectable.
 * @param enabled Whether the control is enabled.
 * @param interactionSource Optional interaction source; when null, Container owns one.
 * @param content Visual content of the control.
 *
 * Example:
 * ```
 * val spec = SelectableDefaults.styleSpec {
 *     if (selected) scale = 1.1f
 * }
 * Selectable(selected = isSelected, onClick = onSelect, style = spec) {
 *     // Draw selection indicator
 * }
 * ```
 *
 * @since 0.8.0
 */
@ExperimentalWildApi
@Composable
fun Selectable(
    selected: Boolean,
    onClick: () -> Unit,
    style: StyleSpec,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Container(
        onClick = onClick,
        modifier =
            modifier.semantics {
                this.selected = selected
            },
        enabled = enabled,
        selected = selected,
        style = style,
        interactionSource = interactionSource,
        content = content,
    )
}

/**
 * Contains the default values used by [Toggleable].
 *
 * @since 0.6.0
 */
object ToggleableDefaults {
    /**
     * Creates a default [Style] for toggleable controls.
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

    /**
     * Creates a default experimental [StyleSpec] for toggleable controls.
     *
     * When every argument is left at its default (including [block]), returns a cached instance
     * whose [StyleSpec.base] is [style].
     *
     * @param colors The colors for the control in different states.
     * @param borders The borders for the control in different states.
     * @param scale The scale for the control in different states.
     * @param shapes The shapes for the control in different states.
     * @param alpha The alpha for the control in different states.
     * @param block First ordered override applied to [ComponentStyleScope].
     * @since 0.8.0
     */
    @ExperimentalWildApi
    fun styleSpec(
        colors: Colors = StyleDefaults.colors(),
        borders: Borders = StyleDefaults.borders(),
        scale: Scale = StyleDefaults.scale(),
        shapes: Shapes = StyleDefaults.shapes(),
        alpha: Alpha = StyleDefaults.alpha(),
        block: ComponentStyleScope.() -> Unit = DefaultStyleSpecOverride,
    ): StyleSpec {
        val base = style(colors = colors, borders = borders, scale = scale, shapes = shapes, alpha = alpha)
        return if (base === style() && block === DefaultStyleSpecOverride) {
            DefaultStyleSpec
        } else {
            newStyleSpec(base, block)
        }
    }

    @OptIn(ExperimentalWildApi::class)
    private val DefaultStyleSpecOverride: ComponentStyleScope.() -> Unit = {}

    @OptIn(ExperimentalWildApi::class)
    private val DefaultStyleSpec: StyleSpec = newStyleSpec(style(), DefaultStyleSpecOverride)
}

/**
 * Contains the default values used by [Selectable].
 *
 * Separate from [ToggleableDefaults] to allow independent evolution of defaults
 * for single-selection controls (radio buttons, tabs) vs toggle controls (switches, checkboxes).
 *
 * @since 0.6.0
 */
object SelectableDefaults {
    /**
     * Creates a default [Style] for selectable controls.
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

    /**
     * Creates a default experimental [StyleSpec] for selectable controls.
     *
     * When every argument is left at its default (including [block]), returns a cached instance
     * whose [StyleSpec.base] is [style].
     *
     * @param colors The colors for the control in different states.
     * @param borders The borders for the control in different states.
     * @param scale The scale for the control in different states.
     * @param shapes The shapes for the control in different states.
     * @param alpha The alpha for the control in different states.
     * @param block First ordered override applied to [ComponentStyleScope].
     * @since 0.8.0
     */
    @ExperimentalWildApi
    fun styleSpec(
        colors: Colors = StyleDefaults.colors(),
        borders: Borders = StyleDefaults.borders(),
        scale: Scale = StyleDefaults.scale(),
        shapes: Shapes = StyleDefaults.shapes(),
        alpha: Alpha = StyleDefaults.alpha(),
        block: ComponentStyleScope.() -> Unit = DefaultStyleSpecOverride,
    ): StyleSpec {
        val base = style(colors = colors, borders = borders, scale = scale, shapes = shapes, alpha = alpha)
        return if (base === style() && block === DefaultStyleSpecOverride) {
            DefaultStyleSpec
        } else {
            newStyleSpec(base, block)
        }
    }

    @OptIn(ExperimentalWildApi::class)
    private val DefaultStyleSpecOverride: ComponentStyleScope.() -> Unit = {}

    @OptIn(ExperimentalWildApi::class)
    private val DefaultStyleSpec: StyleSpec = newStyleSpec(style(), DefaultStyleSpecOverride)
}
