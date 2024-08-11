package io.daio.wild.tv.foundation

import androidx.annotation.FloatRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Immutable
data class Scale(
    @FloatRange(from = 0.0) val scale: Float = 1f,
    @FloatRange(from = 0.0) val focusedScale: Float = scale,
    @FloatRange(from = 0.0) val pressedScale: Float = scale,
    @FloatRange(from = 0.0) val disabledScale: Float = scale,
    @FloatRange(from = 0.0) val focusedDisabledScale: Float = focusedScale,
) {
    fun scaleFor(
        enabled: Boolean,
        focused: Boolean,
        pressed: Boolean,
    ): Float {
        return when {
            pressed && enabled -> pressedScale
            focused && enabled -> focusedScale
            !enabled && focused -> focusedDisabledScale
            !enabled -> disabledScale
            else -> scale
        }
    }
}

// Will be fixed
@Suppress("ModifierComposable")
@Composable
fun Modifier.tvScale(
    scale: Float,
    interactionSource: MutableInteractionSource,
    animationSpecProvider: (Interaction) -> AnimationSpec<Float> = { defaultScaleAnimationSpec(it) },
): Modifier {
    val interaction by
        interactionSource.interactions.collectAsState(initial = FocusInteraction.Focus())
    val provider by rememberUpdatedState(animationSpecProvider)

    val animatedScale by
        animateFloatAsState(
            targetValue = scale,
            animationSpec = provider(interaction),
            label = "tv-scale",
        )

    return this.graphicsLayer(scaleX = animatedScale, scaleY = animatedScale)
}

private fun defaultScaleAnimationSpec(interaction: Interaction): TweenSpec<Float> =
    tween(
        durationMillis =
            when (interaction) {
                is FocusInteraction.Focus -> 300
                is FocusInteraction.Unfocus -> 500
                is PressInteraction.Press -> 120
                is PressInteraction.Release -> 300
                is PressInteraction.Cancel -> 300
                else -> 300
            },
        easing = CubicBezierEasing(0f, 0f, 0.2f, 1f),
    )
