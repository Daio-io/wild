// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.container

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.ContentColorPublisher

/**
 * Equality-gated bridge from the style parent's resolved content color to composition.
 *
 * Publications occur only when the resolved color changes. Composition reads [contentColor]
 * and must not read the live style resolver.
 */
@ExperimentalWildApi
internal class ComponentStyleBinding(
    initial: Color,
) : ContentColorPublisher {
    private val state = mutableStateOf(initial)

    val contentColor: State<Color>
        get() = state

    override fun publishResolvedColor(color: Color) {
        if (state.value != color) {
            state.value = color
        }
    }
}
