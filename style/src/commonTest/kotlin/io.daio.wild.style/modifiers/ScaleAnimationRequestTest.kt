// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ScaleAnimationRequestTest {
    @Test
    fun matchingDefaultRequestsAreEqual() {
        val first = request(scale = 1.1f, zIndex = 0.5f)
        val second = request(scale = 1.1f, zIndex = 0.5f)

        assertEquals(first, second)
    }

    @Test
    fun changedScaleCreatesDifferentRequest() {
        val first = request(scale = 1.1f, zIndex = 0.5f)
        val second = request(scale = 1.2f, zIndex = 0.5f)

        assertNotEquals(first, second)
    }

    @Test
    fun changedZIndexCreatesDifferentRequest() {
        val first = request(scale = 1.1f, zIndex = 0.5f)
        val second = request(scale = 1.1f, zIndex = 0f)

        assertNotEquals(first, second)
    }

    @Test
    fun defaultPressedAndNonPressedRequestsAreDifferent() {
        val first = request(scale = 1.1f, zIndex = 0.5f)
        val second = request(scale = 1.1f, zIndex = 0.5f, pressed = true)

        assertNotEquals(first, second)
    }

    @Test
    fun defaultFocusedAndHoveredRequestsWithSameTargetAreEqual() {
        val focused =
            request(
                scale = 1.1f,
                zIndex = 0.5f,
                focused = true,
            )
        val hovered =
            request(
                scale = 1.1f,
                zIndex = 0.5f,
                hovered = true,
            )

        assertEquals(focused, hovered)
    }

    @Test
    fun matchingCustomSpecRequestsAreEqual() {
        val spec = tween<Float>(durationMillis = 100)
        val first = request(scale = 1.1f, zIndex = 0.5f, animationSpec = spec)
        val second = request(scale = 1.1f, zIndex = 0.5f, animationSpec = spec)

        assertEquals(first, second)
    }

    @Test
    fun differentCustomSpecRequestsAreDifferent() {
        val first =
            request(
                scale = 1.1f,
                zIndex = 0.5f,
                animationSpec = tween(durationMillis = 100),
            )
        val second =
            request(
                scale = 1.1f,
                zIndex = 0.5f,
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
                animationSpec = tween(durationMillis = 300),
            )
        val default = request(scale = 1.1f, zIndex = 0.5f)

        assertNotEquals(custom, default)
    }

    @Test
    fun coalescerAnimatesFirstRequest() {
        val coalescer = ScaleAnimationRequestCoalescer()

        assertTrue(coalescer.shouldAnimate(request(scale = 1.1f, zIndex = 0.5f)))
    }

    @Test
    fun coalescerSkipsMatchingRequest() {
        val coalescer = ScaleAnimationRequestCoalescer()
        val request = request(scale = 1.1f, zIndex = 0.5f)

        coalescer.shouldAnimate(request)

        assertFalse(coalescer.shouldAnimate(request))
    }

    @Test
    fun coalescerAnimatesChangedRequest() {
        val coalescer = ScaleAnimationRequestCoalescer()

        coalescer.shouldAnimate(request(scale = 1.1f, zIndex = 0.5f))

        assertTrue(coalescer.shouldAnimate(request(scale = 1.2f, zIndex = 0.5f)))
    }

    @Test
    fun coalescerAnimatesMatchingRequestAfterReset() {
        val coalescer = ScaleAnimationRequestCoalescer()
        val request = request(scale = 1.1f, zIndex = 0.5f)

        coalescer.shouldAnimate(request)
        coalescer.reset()

        assertTrue(coalescer.shouldAnimate(request))
    }

    private fun request(
        scale: Float,
        zIndex: Float,
        focused: Boolean = false,
        pressed: Boolean = false,
        hovered: Boolean = false,
        animationSpec: AnimationSpec<Float>? = null,
    ): ScaleAnimationRequest =
        scaleAnimationRequest(
            scale = scale,
            zIndex = zIndex,
            animationSpec = animationSpec,
            focused = focused,
            pressed = pressed,
            hovered = hovered,
        )
}
