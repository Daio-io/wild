// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.serialization.json.JsonObject

sealed interface A2uiComponentState {
    data object Loading : A2uiComponentState

    data class Error(val message: String) : A2uiComponentState

    data class Success(val id: String, val json: JsonObject) : A2uiComponentState
}

val LocalA2uiCatalog = staticCompositionLocalOf<A2uiCatalog> { error("LocalA2uiCatalog") }
val LocalA2uiScope = staticCompositionLocalOf<A2uiComponentScope> { error("LocalA2uiScope") }
