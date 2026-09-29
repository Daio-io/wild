// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** A component implementation registered in an [A2uiCatalog].
 *
 * Example: `object : A2uiComponent { override val name = "Text" }`.
 *
 * @since 0.1.0
 */
interface A2uiComponent {
    /** Component type name used by the protocol. @since 0.1.0 */
    val name: String

    /** Component properties accepted by this implementation. @since 0.1.0 */
    val properties: List<A2uiProperty<*>>

    /** Reports whether [props] can currently be rendered.
     * @param props component properties.
     * @since 0.1.0
     */
    @Composable
    fun A2uiComponentScope.isReady(props: A2uiComponentProperties): Boolean = true

    /** Renders [props].
     * @param props component properties.
     * @param modifier modifier applied to the component.
     * @since 0.1.0
     */
    @Composable
    fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    )
}
