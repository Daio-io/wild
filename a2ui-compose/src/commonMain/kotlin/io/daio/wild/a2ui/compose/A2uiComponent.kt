// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** A catalog component implementation. */
interface A2uiComponent {
    val name: String
    val properties: List<A2uiProperty<*>>

    @Composable
    fun A2uiComponentScope.isReady(props: A2uiComponentProperties): Boolean = true

    @Composable
    fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    )
}
