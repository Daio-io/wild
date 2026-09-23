// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.radio

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher

/**
 * Matches nodes with the given selected state.
 */
fun hasSelectedState(selected: Boolean): SemanticsMatcher = SemanticsMatcher.expectValue(SemanticsProperties.Selected, selected)

/**
 * Matches nodes with the given [Role].
 */
fun hasRole(role: Role): SemanticsMatcher = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

/**
 * Matches nodes that expose selectable-group semantics.
 */
fun isSelectableGroup(): SemanticsMatcher = SemanticsMatcher.expectValue(SemanticsProperties.SelectableGroup, Unit)
