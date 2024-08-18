package io.daio.wild.foundation

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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

@Immutable
data class Scale(
    val scale: Float = 1f,
    val focusedScale: Float = scale,
    val pressedScale: Float = scale,
    val disabledScale: Float = scale,
    val focusedDisabledScale: Float = focusedScale,
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

/**
 * Fire-and-forget animation function for [Float]. Using [animateFloatAsState] on the provided
 * [targetScale] updating based on the current [Interaction] provided on the [interactionSource].
 *
 * [animateInteractionScaleAsState] returns a [State] object.
 * The value of the state object will continuously be updated by the animation until the animation
 * finishes.
 *
 * @param targetScale Target value of the animation.
 * @param animationSpecProvider The animation to provide based on the current [Interaction] that
 * will be used to change the value through time.
 * @param visibilityThreshold An optional threshold for deciding when the animation value is
 *                            considered close enough to the targetScale.
 * @param label An optional label to differentiate from other animations in Android Studio.
 * @param finishedListener An optional end listener to get notified when the animation is finished.
 * @return A [State] object, the value of which is updated by animation.
 */
@Composable
fun animateInteractionScaleAsState(
    targetScale: Float,
    interactionSource: MutableInteractionSource,
    label: String = "interaction-scale",
    visibilityThreshold: Float = 0.01f,
    finishedListener: ((Float) -> Unit)? = null,
    animationSpecProvider: (Interaction) -> AnimationSpec<Float> = { defaultScaleAnimationSpec(it) },
): State<Float> {
    val interaction
        by interactionSource.interactions.collectAsState(initial = FocusInteraction.Focus())

    val provider by rememberUpdatedState(animationSpecProvider)

    return animateFloatAsState(
        targetValue = targetScale,
        visibilityThreshold = visibilityThreshold,
        finishedListener = finishedListener,
        animationSpec = provider(interaction),
        label = label,
    )
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
