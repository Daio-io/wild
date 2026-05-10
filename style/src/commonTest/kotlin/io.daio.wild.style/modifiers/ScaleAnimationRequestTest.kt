// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ScaleAnimationRequestTest {
    @Test
    fun matchingDefaultRequestsAreEqual() {
        val first = request(scale = 1.1f, zIndex = 0.5f, pressed = false)
        val second = request(scale = 1.1f, zIndex = 0.5f, pressed = false)

        assertEquals(first, second)
    }

    @Test
    fun changedScaleCreatesDifferentRequest() {
        val first = request(scale = 1.1f, zIndex = 0.5f, pressed = false)
        val second = request(scale = 1.2f, zIndex = 0.5f, pressed = false)

        assertNotEquals(first, second)
    }

    @Test
    fun changedZIndexCreatesDifferentRequest() {
        val first = request(scale = 1.1f, zIndex = 0.5f, pressed = false)
        val second = request(scale = 1.1f, zIndex = 0f, pressed = false)

        assertNotEquals(first, second)
    }

    @Test
    fun defaultPressedAndNonPressedRequestsAreDifferent() {
        val first = request(scale = 1.1f, zIndex = 0.5f, pressed = false)
        val second = request(scale = 1.1f, zIndex = 0.5f, pressed = true)

        assertNotEquals(first, second)
    }

    @Test
    fun defaultFocusedAndHoveredRequestsWithSameTargetAreEqual() {
        val focused =
            request(
                scale = 1.1f,
                zIndex = 0.5f,
                pressed = false,
            )
        val hovered =
            request(
                scale = 1.1f,
                zIndex = 0.5f,
                pressed = false,
            )

        assertEquals(focused, hovered)
    }

    @Test
    fun matchingCustomSpecRequestsAreEqual() {
        val spec = tween<Float>(durationMillis = 100)
        val first = request(scale = 1.1f, zIndex = 0.5f, pressed = false, animationSpec = spec)
        val second = request(scale = 1.1f, zIndex = 0.5f, pressed = false, animationSpec = spec)

        assertEquals(first, second)
    }

    @Test
    fun differentCustomSpecRequestsAreDifferent() {
        val first =
            request(
                scale = 1.1f,
                zIndex = 0.5f,
                pressed = false,
                animationSpec = tween(durationMillis = 100),
            )
        val second =
            request(
                scale = 1.1f,
                zIndex = 0.5f,
                pressed = false,
                animationSpec = tween(durationMillis = 200),
            )

        assertNotEquals(first, second)
    }

    @Test
    fun customAndDefaultRequestsAreDifferent() {
        val custom =
            request(
                scale = 1.1f,
                zIndex = 0.5f,
                pressed = false,
                animationSpec = tween(durationMillis = 300),
            )
        val default = request(scale = 1.1f, zIndex = 0.5f, pressed = false)

        assertNotEquals(custom, default)
    }

    private fun request(
        scale: Float,
        zIndex: Float,
        pressed: Boolean,
        animationSpec: AnimationSpec<Float>? = null,
    ): ScaleAnimationRequest =
        scaleAnimationRequest(
            scale = scale,
            zIndex = zIndex,
            animationSpec = animationSpec,
            pressed = pressed,
        )
}
