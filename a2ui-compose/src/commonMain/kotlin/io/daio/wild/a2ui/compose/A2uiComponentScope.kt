// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

import androidx.compose.runtime.Composable
import io.daio.wild.a2ui.A2uiMessageProcessor
import io.daio.wild.a2ui.A2uiSurfaceModel
import io.daio.wild.a2ui.A2uiUserAction
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/** Provides component data, bindings, and actions to an A2UI catalog component.
 *
 * Example: `bindString(props.raw("label"))` reads a literal or dynamic label,
 * while `dispatchAction(action)` sends a user action to the host.
 *
 * @since 0.1.0
 */
interface A2uiComponentScope {
    /** The surface currently being rendered. @since 0.1.0 */
    val surface: A2uiSurfaceModel

    /** Dispatches [action] to the host and protocol processor. @param action action to dispatch. @since 0.1.0 */
    fun dispatchAction(action: A2uiUserAction)

    /** Observes a component by [id]. @param id component identifier. @since 0.1.0 */
    @Composable fun observeComponentState(id: String): A2uiComponentState

    /** Resolves a literal or dynamic string. @param el raw property value. @since 0.1.0 */
    fun bindString(el: JsonElement?): String?

    /** Resolves a dynamic boolean at [path]. @param path data-model path. @since 0.1.0 */
    fun bindBoolean(path: String): Boolean

    /** Creates an updater for [path]. @param path data-model path. @since 0.1.0 */
    fun bindUpdater(path: String): (JsonElement) -> Unit

    /** Resolves a child list, reporting unsupported template objects.
     * @param el raw child-list property value.
     * @since 0.1.0
     */
    fun resolveChildList(el: JsonElement?): A2uiChildList
}

/** Result of resolving an A2UI child-list property. @since 0.1.0 */
sealed interface A2uiChildList {
    /** Resolved child component IDs. @param ids child IDs. @since 0.1.0 */
    data class Ids(val ids: List<String>) : A2uiChildList

    /** Indicates that template child lists are not supported. @since 0.1.0 */
    data object TemplatesUnsupported : A2uiChildList

    /** Indicates that the property was not a valid child list. @since 0.1.0 */
    data object Invalid : A2uiChildList
}

internal class DefaultA2uiComponentScope(
    override val surface: A2uiSurfaceModel,
    private val processor: A2uiMessageProcessor,
    private val onAction: (A2uiUserAction) -> Unit,
) : A2uiComponentScope {
    override fun dispatchAction(action: A2uiUserAction) {
        onAction(action)
        processor.dispatchAction(action)
    }

    @Composable
    override fun observeComponentState(id: String): A2uiComponentState =
        surface.components[id]?.let { A2uiComponentState.Success(id, it) } ?: A2uiComponentState.Loading

    override fun bindString(el: JsonElement?): String? =
        when (el) {
            is JsonPrimitive -> el.contentOrNull
            is JsonObject ->
                el["path"]?.jsonPrimitive?.contentOrNull?.let {
                    processor.resolvePath(surface.surfaceId, it)?.jsonPrimitive?.contentOrNull
                }
            else -> null
        }

    override fun bindBoolean(path: String): Boolean = processor.resolvePath(surface.surfaceId, path)?.jsonPrimitive?.booleanOrNull ?: false

    override fun bindUpdater(path: String): (JsonElement) -> Unit =
        { value ->
            processor.setPath(surface.surfaceId, path, value)
        }

    override fun resolveChildList(el: JsonElement?): A2uiChildList =
        when (el) {
            is kotlinx.serialization.json.JsonArray ->
                A2uiChildList.Ids(el.mapNotNull { it.jsonPrimitive.contentOrNull })
            is JsonObject ->
                (el["explicitList"] as? kotlinx.serialization.json.JsonArray)?.let {
                    A2uiChildList.Ids(it.mapNotNull { item -> item.jsonPrimitive.contentOrNull })
                } ?: A2uiChildList.TemplatesUnsupported
            else -> A2uiChildList.Invalid
        }
}
