// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.checkbox

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher

fun hasToggleableState(state: ToggleableState): SemanticsMatcher = SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, state)

fun hasRole(role: Role): SemanticsMatcher = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)
