// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.daio.wild.a2ui.A2uiMessageProcessor
import io.daio.wild.a2ui.A2uiSurfaceModel
import io.daio.wild.a2ui.A2uiUserAction
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/** Renders the root component on an A2UI surface.
 * @param surface surface model to render.
 * @param catalog component catalog matching the surface.
 * @param processor message processor backing the surface.
 * @param modifier modifier applied to the root component.
 * @param onAction callback for user actions.
 * @param loading content shown while data is unavailable.
 * @param error content shown for render errors.
 * @since 0.1.0
 */
@Composable
fun A2uiSurface(
    surface: A2uiSurfaceModel,
    catalog: A2uiCatalog,
    processor: A2uiMessageProcessor,
    modifier: Modifier = Modifier,
    onAction: (A2uiUserAction) -> Unit = {},
    loading: @Composable () -> Unit = {},
    error: @Composable (String) -> Unit = { BasicText(it) },
) {
    if (surface.error != null) {
        error(surface.error!!)
        return
    }
    if (surface.catalogId != catalog.catalogId) {
        error("catalog mismatch")
        return
    }
    val scope = remember(surface, processor, onAction) { DefaultA2uiComponentScope(surface, processor, onAction) }
    CompositionLocalProvider(LocalA2uiCatalog provides catalog, LocalA2uiScope provides scope) {
        A2uiComponent(scope.observeComponentState("root"), modifier, loading, error)
    }
}

/** Renders a component state from the current catalog and scope.
 * @param state component state to render.
 * @param modifier modifier applied to the component.
 * @param loading content shown while data is unavailable.
 * @param error content shown for render errors.
 * @since 0.1.0
 */
@Composable
fun A2uiComponent(
    state: A2uiComponentState,
    modifier: Modifier = Modifier,
    loading: @Composable () -> Unit = {},
    error: @Composable (String) -> Unit = {},
) {
    val catalog = LocalA2uiCatalog.current
    val scope = LocalA2uiScope.current
    when (state) {
        A2uiComponentState.Loading -> loading()
        is A2uiComponentState.Error -> error(state.message)
        is A2uiComponentState.Success -> {
            val component = state.json["component"]
            val componentData =
                when (component) {
                    is JsonObject ->
                        component.entries.singleOrNull()?.let { entry ->
                            (entry.value as? JsonObject)?.let { entry.key to it }
                        }
                    else -> component?.let { it.jsonPrimitive.contentOrNull to state.json }
                } ?: return error("missing component type")
            val (type, componentJson) = componentData
            val impl = catalog.components[type] ?: return error("Unknown component: $type")
            val props = A2uiComponentProperties.from(componentJson, state.id)
            if (!with(impl) { with(scope) { isReady(props) } }) {
                loading()
                return
            }
            with(impl) { with(scope) { Content(props, modifier) } }
        }
    }
}
