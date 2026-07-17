// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.DefaultStyleScope
import io.daio.wild.style.StyleScope
import io.daio.wild.style.defaultScaleAnimationSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class ScaleAnimationRequestTest {
    @Test
    fun defaultRestingSpecIsCachedWithExpectedDurationAndEasing() {
        val resting = defaultScaleAnimationSpec(pressed = false, focused = false, hovered = false)
        val focused = defaultScaleAnimationSpec(pressed = false, focused = true, hovered = false)
        val hovered = defaultScaleAnimationSpec(pressed = false, focused = false, hovered = true)

        assertSame(resting, focused)
        assertSame(resting, hovered)
        assertEquals(300, resting.durationMillis)
        assertEquals(CubicBezierEasing(0f, 0f, 0.2f, 1f), resting.easing)
    }

    @Test
    fun defaultPressedSpecIsCachedWithExpectedDurationAndEasing() {
        val pressed = defaultScaleAnimationSpec(pressed = true, focused = false, hovered = false)
        val pressedAndFocused =
            defaultScaleAnimationSpec(pressed = true, focused = true, hovered = false)
        val pressedAndHovered =
            defaultScaleAnimationSpec(pressed = true, focused = false, hovered = true)

        assertSame(pressed, pressedAndFocused)
        assertSame(pressed, pressedAndHovered)
        assertEquals(120, pressed.durationMillis)
        assertEquals(CubicBezierEasing(0f, 0f, 0.2f, 1f), pressed.easing)
    }

    @Test
    fun coalescerAnimatesFirstRequest() {
        val coalescer = ScaleAnimationRequestCoalescer()

        assertTrue(coalescer.shouldAnimate(scale = 1.1f))
    }

    @Test
    fun coalescerSkipsMatchingRequest() {
        val coalescer = ScaleAnimationRequestCoalescer()

        coalescer.shouldAnimate(scale = 1.1f)

        assertFalse(coalescer.shouldAnimate(scale = 1.1f))
    }

    @Test
    fun coalescerAnimatesChangedScale() {
        val coalescer = ScaleAnimationRequestCoalescer()

        coalescer.shouldAnimate(scale = 1.1f)

        assertTrue(coalescer.shouldAnimate(scale = 1.2f))
    }

    @Test
    fun coalescerAnimatesWhenPressedDefaultChanges() {
        val coalescer = ScaleAnimationRequestCoalescer()

        coalescer.shouldAnimate(scale = 1.1f)

        assertTrue(coalescer.shouldAnimate(scale = 1.1f, pressed = true))
    }

    @Test
    fun coalescerSkipsFocusedAndHoveredDefaultRequestsWithSameTarget() {
        val coalescer = ScaleAnimationRequestCoalescer()

        coalescer.shouldAnimate(
            scale = 1.1f,
            focused = true,
        )

        assertFalse(
            coalescer.shouldAnimate(
                scale = 1.1f,
                hovered = true,
            ),
        )
    }

    @Test
    fun coalescerSkipsEqualCustomSpecRequests() {
        val coalescer = ScaleAnimationRequestCoalescer()

        coalescer.shouldAnimate(
            scale = 1.1f,
            animationSpec = tween(durationMillis = 100),
        )

        assertFalse(
            coalescer.shouldAnimate(
                scale = 1.1f,
                animationSpec = tween(durationMillis = 100),
            ),
        )
    }

    @Test
    fun coalescerAnimatesWhenChangingBetweenDefaultAndCustomSpecs() {
        val coalescer = ScaleAnimationRequestCoalescer()

        coalescer.shouldAnimate(scale = 1.1f)

        assertTrue(
            coalescer.shouldAnimate(
                scale = 1.1f,
                animationSpec = tween(durationMillis = 100),
            ),
        )
    }

    @Test
    fun coalescerAnimatesWhenChangingBetweenCustomAndDefaultSpecs() {
        val coalescer = ScaleAnimationRequestCoalescer()

        coalescer.shouldAnimate(
            scale = 1.1f,
            animationSpec = tween(durationMillis = 100),
        )

        assertTrue(coalescer.shouldAnimate(scale = 1.1f))
    }

    @Test
    fun coalescerAnimatesMatchingRequestAfterReset() {
        val coalescer = ScaleAnimationRequestCoalescer()

        coalescer.shouldAnimate(scale = 1.1f)
        coalescer.reset()

        assertTrue(coalescer.shouldAnimate(scale = 1.1f))
    }

    @Test
    fun zIndexChangeInvalidatesPlacementWithoutStartingScaleAnimationJob() =
        runComposeUiTest {
            val recorder = PlacementRecorder()
            lateinit var node: ScaleLayoutModifier

            setContent {
                Box(
                    modifier =
                        Modifier
                            .size(8.dp)
                            .scaleLayoutForTest { node = it }
                            .onPlaced { recorder.count++ },
                )
            }
            waitForIdle()

            runOnIdle {
                node.updateStyle(testStyleScope(scale = 1f))
            }
            waitForIdle()
            assertFalse(node.isScaleAnimationRunningForTest)
            val placementsAfterBaseline = recorder.count

            runOnIdle {
                node.updateStyle(
                    testStyleScope(
                        scale = 1f,
                        focused = true,
                    ),
                )
            }
            waitForIdle()

            assertEquals(placementsAfterBaseline + 1, recorder.count)
            assertFalse(node.isScaleAnimationRunningForTest)
        }

    @Test
    fun resetClearsStateAndCancelsScaleAnimationJob() =
        runComposeUiTest {
            lateinit var node: ScaleLayoutModifier
            val slowSpec =
                tween<Float>(
                    durationMillis = 10_000,
                    easing = LinearEasing,
                )

            setContent {
                Box(
                    modifier =
                        Modifier
                            .size(8.dp)
                            .scaleLayoutForTest { node = it },
                )
            }
            waitForIdle()

            runOnIdle {
                node.updateStyle(
                    testStyleScope(
                        scale = 1.2f,
                        animationSpec = slowSpec,
                    ),
                )
            }
            waitUntil { node.isScaleAnimationRunningForTest && node.animatedScaleForTest > 1f }

            runOnIdle {
                node.onReset()
            }
            waitForIdle()

            assertFalse(node.isScaleAnimationRunningForTest)
            assertEquals(1f, node.scale)
            assertEquals(0f, node.zIndex)
            assertEquals(1f, node.animatedScaleForTest)

            runOnIdle {
                node.updateStyle(
                    testStyleScope(
                        scale = 1.2f,
                        animationSpec = slowSpec,
                    ),
                )
            }

            assertTrue(node.isScaleAnimationRunningForTest)
        }
}

private class PlacementRecorder {
    var count: Int = 0
}

private fun Modifier.scaleLayoutForTest(onNode: (ScaleLayoutModifier) -> Unit): Modifier = this then TestScaleLayoutElement(onNode)

private data class TestScaleLayoutElement(
    private val onNode: (ScaleLayoutModifier) -> Unit,
) : androidx.compose.ui.node.ModifierNodeElement<ScaleLayoutModifier>() {
    override fun create(): ScaleLayoutModifier = ScaleLayoutModifier(zIndex = 0f, scale = 1f).also(onNode)

    override fun update(node: ScaleLayoutModifier) {
        onNode(node)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "TestScaleLayoutElement"
    }
}

private fun testStyleScope(
    scale: Float,
    focused: Boolean = false,
    hovered: Boolean = false,
    pressed: Boolean = false,
    animationSpec: androidx.compose.animation.core.AnimationSpec<Float>? = null,
): StyleScope =
    DefaultStyleScope().apply {
        this.scale = scale
        this.scaleAnimationSpec = animationSpec
        updateState(
            enabled = true,
            focused = focused,
            selected = false,
            pressed = pressed,
            hovered = hovered,
        )
    }
