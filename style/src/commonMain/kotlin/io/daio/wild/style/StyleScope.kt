// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import io.daio.wild.foundation.InteractionState

@DslMarker
annotation class StyleScopeDslMarker

/**
 * A scope used to define the style properties of the element.
 */
@StyleScopeDslMarker
interface StyleScope : InteractionState {
    /**
     * Sets the background color of the element.
     */
    var color: Color

    /**
     * Sets the alpha value of the element.
     * Range 0.0f to 1.0f.
     */
    var alpha: Float

    /**
     * Sets the animated scale of the element.
     */
    var scale: Float

    /**
     * Sets the shape for the element.
     */
    var shape: Shape

    /**
     * Sets the border of the element.
     */
    var border: Border
}
