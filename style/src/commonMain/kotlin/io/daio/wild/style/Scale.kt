// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

@Immutable
data class Scale(
    val scale: Float = 1f,
    val focusedScale: Float = scale,
    val hoveredScale: Float = focusedScale,
    val pressedScale: Float = focusedScale,
    val selectedScale: Float = scale,
    val disabledScale: Float = scale,
    val focusedSelectedScale: Float = focusedScale,
    val pressedSelectedScale: Float = pressedScale,
    val hoveredSelectedScale: Float = hoveredScale,
    val focusedDisabledScale: Float = disabledScale,
    val pressedDisabledScale: Float = disabledScale,
    val hoveredDisabledScale: Float = disabledScale,
) {
    @Stable
    fun scaleFor(
        enabled: Boolean,
        focused: Boolean,
        hovered: Boolean,
        pressed: Boolean,
        selected: Boolean,
    ): Float {
        return when {
            pressed && selected && enabled -> pressedSelectedScale
            hovered && selected && enabled -> hoveredSelectedScale
            focused && selected && enabled -> focusedSelectedScale
            pressed && enabled -> pressedScale
            hovered && enabled -> hoveredScale
            focused && enabled -> focusedScale
            selected && enabled -> selectedScale
            !enabled && pressed -> pressedDisabledScale
            !enabled && hovered -> hoveredDisabledScale
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
 *
 * @since 0.2.0
 */
@Composable
fun animateInteractionScaleAsState(
    targetScale: Float,
    pressed: Boolean,
    focused: Boolean,
    hovered: Boolean,
    label: String = "interaction-scale",
    visibilityThreshold: Float = 0.01f,
    finishedListener: ((Float) -> Unit)? = null,
    animationSpecProvider: (pressed: Boolean, focused: Boolean, hovered: Boolean) -> AnimationSpec<Float> = { press, focus, hover ->
        defaultScaleAnimationSpec(pressed = press, focused = focus, hovered = hover)
    },
): State<Float> {
    val provider by rememberUpdatedState(animationSpecProvider)

    return animateFloatAsState(
        targetValue = targetScale,
        visibilityThreshold = visibilityThreshold,
        finishedListener = finishedListener,
        animationSpec = provider(pressed, focused, hovered),
        label = label,
    )
}

/**
 * Creates a [TweenSpec] configured with the default duration, delay and easing curve for the state
 * [pressed], [focused] && [hovered].
 *
 * @param pressed whether the element is currently pressed.
 * @param focused whether the element is currently focused.
 * @param hovered whether the element is currently hovered.
 */
@Stable
internal fun defaultScaleAnimationSpec(
    pressed: Boolean,
    focused: Boolean,
    hovered: Boolean,
): TweenSpec<Float> =
    tween(
        durationMillis =
            when {
                (focused || hovered) && !pressed -> 300
                pressed -> 120
                else -> 300
            },
        easing = CubicBezierEasing(0f, 0f, 0.2f, 1f),
    )

/**
 * Contains default [Scale] presets for common interaction patterns.
 *
 * @since 0.5.0
 */
object ScaleDefaults {
    /**
     * No scaling effect. All states return scale 1.0.
     */
    val None: Scale =
        Scale(
            scale = 1f,
            focusedScale = 1f,
            hoveredScale = 1f,
            pressedScale = 1f,
            selectedScale = 1f,
            disabledScale = 1f,
            focusedSelectedScale = 1f,
            pressedSelectedScale = 1f,
            hoveredSelectedScale = 1f,
            focusedDisabledScale = 1f,
            pressedDisabledScale = 1f,
            hoveredDisabledScale = 1f,
        )

    /**
     * Press-in effect where the element shrinks slightly when pressed.
     * Pressed scale: 0.95
     */
    val Pressed: Scale = Scale(pressedScale = 0.95f)
}
