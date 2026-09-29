// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.serialization.json.JsonObject

/** State of a component being rendered. @since 0.1.0 */
sealed interface A2uiComponentState {
    /** Component data is not available yet. @since 0.1.0 */
    data object Loading : A2uiComponentState

    /** Rendering failed. @param message failure description. @since 0.1.0 */
    data class Error(val message: String) : A2uiComponentState

    /** Component data is available. @param id component ID. @param json component payload. @since 0.1.0 */
    data class Success(val id: String, val json: JsonObject) : A2uiComponentState
}

/** Provides the catalog to descendant components. @since 0.1.0 */
val LocalA2uiCatalog = staticCompositionLocalOf<A2uiCatalog> { error("LocalA2uiCatalog") }
/** Provides the component scope to descendant components. @since 0.1.0 */
val LocalA2uiScope = staticCompositionLocalOf<A2uiComponentScope> { error("LocalA2uiScope") }
