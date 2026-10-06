// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import io.daio.wild.foundation.ExperimentalWildApi

/**
 * Style scope for [StyleSpec] callbacks. Extends [StyleScope] with a content color output that is
 * resolved from the base [Style] tables and may be overridden by ordered blocks.
 *
 * Content color is retained for a later content-local bridge; chrome Spec modifiers do not publish
 * content composition locals yet.
 *
 * @since 0.8.0
 */
@ExperimentalWildApi
@StyleScopeDslMarker
interface ComponentStyleScope : StyleScope {
    /**
     * Content color for the current interaction and component flags.
     */
    var contentColor: Color
}

/**
 * Default mutable [ComponentStyleScope] used while evaluating [StyleSpec] blocks.
 */
@ExperimentalWildApi
internal class DefaultComponentStyleScope : ComponentStyleScope {
    override var color: Color = Color.Unspecified
    override var alpha: Float = 1f
    override var scale: Float = 1f
    override var shape: Shape = RectangleShape
    override var border: Border = BorderDefaults.None
    override var scaleAnimationSpec: AnimationSpec<Float>? = null
    override var contentColor: Color = Color.Unspecified

    override val focused: Boolean
        get() = _focused

    override val hovered: Boolean
        get() = _hovered

    override val pressed: Boolean
        get() = _pressed

    override val selected: Boolean
        get() = _selected

    override val enabled: Boolean
        get() = _enabled

    private var _focused: Boolean = false
    private var _hovered: Boolean = false
    private var _pressed: Boolean = false
    private var _selected: Boolean = false
    private var _enabled: Boolean = true

    fun updateState(
        enabled: Boolean,
        focused: Boolean,
        selected: Boolean,
        pressed: Boolean,
        hovered: Boolean,
    ) {
        _focused = focused
        _hovered = hovered
        _pressed = pressed
        _selected = selected
        _enabled = enabled
    }
}
