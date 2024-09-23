package io.daio.wild.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

internal const val DEFAULT_DISABLED_ALPHA = .6f

@Immutable
data class Alpha(
    val alpha: Float = 1f,
    val focusedAlpha: Float = alpha,
    val pressedAlpha: Float = alpha,
    val selectedAlpha: Float = alpha,
    val disabledAlpha: Float = .6f,
    val focusedDisabledAlpha: Float = disabledAlpha,
) {
    @Stable
    fun alphaFor(
        enabled: Boolean,
        focused: Boolean,
        pressed: Boolean,
        selected: Boolean,
    ): Float {
        return when {
            pressed && enabled -> pressedAlpha
            focused && enabled -> focusedAlpha
            selected && enabled -> selectedAlpha
            !enabled && focused -> focusedDisabledAlpha
            !enabled -> disabledAlpha
            else -> alpha
        }
    }
}
