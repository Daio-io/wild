// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.radio

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Provides group semantics for caller-laid-out radio buttons.
 *
 * [RadioGroup] does not own the selected value, rewrite child callbacks, choose a Row or
 * Column layout, or implement directional navigation. Callers enforce single selection and
 * choose the layout themselves, for example:
 *
 * ```
 * var selected by remember { mutableStateOf("small") }
 * RadioGroup {
 *     Column {
 *         listOf("small", "medium").forEach { value ->
 *             RadioButton(
 *                 selected = selected == value,
 *                 onClick = { selected = value },
 *                 indicator = { isSelected ->
 *                     // Draw an indicator from the caller-owned value.
 *                 },
 *             )
 *         }
 *     }
 * }
 * ```
 *
 * Callers are responsible for enforcing valid single-selection state. Empty groups and groups
 * with multiple selected descendants are permitted by this primitive.
 *
 * @param modifier Modifier to apply to the group.
 * @param content Caller-laid-out radio buttons and other content.
 *
 * @since 0.8.0
 */
@Composable
fun RadioGroup(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.selectableGroup(),
        content = content,
    )
}
