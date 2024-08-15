package io.daio.wild.foundation

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable

@Immutable
data class Alpha(
    @FloatRange(from = 0.0, to = 1.0) val alpha: Float = 1f,
    @FloatRange(from = 0.0, to = 1.0) val focusedAlpha: Float = alpha,
    @FloatRange(from = 0.0, to = 1.0) val pressedAlpha: Float = alpha,
    @FloatRange(from = 0.0, to = 1.0) val disabledAlpha: Float = .6f,
    @FloatRange(from = 0.0, to = 1.0) val focusedDisabledAlpha: Float = disabledAlpha,
) {
    fun alphaFor(
        enabled: Boolean,
        focused: Boolean,
        pressed: Boolean,
    ): Float {
        return when {
            pressed && enabled -> pressedAlpha
            focused && enabled -> focusedAlpha
            !enabled && focused -> focusedDisabledAlpha
            !enabled -> disabledAlpha
            else -> alpha
        }
    }
}
