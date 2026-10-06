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
 * Content color is published to composition by interactive component Spec/value overloads via
 * an equality-gated bridge. Standalone Spec chrome modifiers do not publish content locals.
 *
 * Example:
 * ```
 * val spec = styleSpec(StyleDefaults.style()) {
 *     contentColor = Color.White
 *     if (focused) {
 *         scale = 1.1f
 *         border = Border(width = 2.dp, color = Color.Cyan)
 *     }
 * }
 * Modifier.interactionStyle(interactionSource, style = spec)
 * ```
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
@OptIn(ExperimentalWildApi::class)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DefaultComponentStyleScope

        if (alpha != other.alpha) return false
        if (scale != other.scale) return false
        if (_focused != other._focused) return false
        if (_hovered != other._hovered) return false
        if (_pressed != other._pressed) return false
        if (_selected != other._selected) return false
        if (_enabled != other._enabled) return false
        if (color != other.color) return false
        if (shape != other.shape) return false
        if (border != other.border) return false
        if (scaleAnimationSpec != other.scaleAnimationSpec) return false
        if (contentColor != other.contentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = alpha.hashCode()
        result = 31 * result + scale.hashCode()
        result = 31 * result + _focused.hashCode()
        result = 31 * result + _hovered.hashCode()
        result = 31 * result + _pressed.hashCode()
        result = 31 * result + _selected.hashCode()
        result = 31 * result + _enabled.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + shape.hashCode()
        result = 31 * result + border.hashCode()
        result = 31 * result + (scaleAnimationSpec?.hashCode() ?: 0)
        result = 31 * result + contentColor.hashCode()
        return result
    }
}
