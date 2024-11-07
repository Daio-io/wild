package io.daio.wild.style

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

internal const val DEFAULT_DISABLED_ALPHA = .6f

@Immutable
data class Alpha(
    val alpha: Float = 1f,
    val focusedAlpha: Float = alpha,
    val hoveredAlpha: Float = alpha,
    val pressedAlpha: Float = alpha,
    val selectedAlpha: Float = alpha,
    val disabledAlpha: Float = .6f,
    val focusedDisabledAlpha: Float = disabledAlpha,
    val hoveredDisabledAlpha: Float = disabledAlpha,
) {
    @Stable
    fun alphaFor(
        enabled: Boolean,
        focused: Boolean,
        hovered: Boolean,
        pressed: Boolean,
        selected: Boolean,
    ): Float {
        return when {
            pressed && enabled -> pressedAlpha
            focused && enabled -> focusedAlpha
            hovered && enabled -> hoveredAlpha
            selected && enabled -> selectedAlpha
            !enabled && focused -> focusedDisabledAlpha
            !enabled && hovered -> hoveredDisabledAlpha
            !enabled -> disabledAlpha
            else -> alpha
        }
    }
}
