package io.daio.wild.foundation

import androidx.compose.runtime.Immutable

@Immutable
data class Alpha(
    val alpha: Float = 1f,
    val focusedAlpha: Float = alpha,
    val pressedAlpha: Float = alpha,
    val disabledAlpha: Float = .6f,
    val focusedDisabledAlpha: Float = disabledAlpha,
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
