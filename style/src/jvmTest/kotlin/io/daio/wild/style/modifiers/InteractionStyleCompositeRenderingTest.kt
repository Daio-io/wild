// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Smoke pixel checks for the candidate draw path's manual clip / group-alpha around
 * `drawContent()` (the novel part vs the traversal chain's layout-layer approach).
 */
@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class InteractionStyleCompositeRenderingTest {
    @Test
    fun rectangleRoundedAndAlphaModesRenderExpectedPixels() =
        runComposeUiTest {
            setContent {
                Box(Modifier.size(160.dp)) {
                    TestSurface(
                        tag = "opaque-rectangle",
                        shape = RectangleShape,
                        alpha = 1f,
                    )
                    TestSurface(
                        tag = "opaque-circle",
                        shape = CircleShape,
                        alpha = 1f,
                        modifier = Modifier.align(Alignment.TopEnd),
                    )
                    TestSurface(
                        tag = "alpha-rectangle",
                        shape = RectangleShape,
                        alpha = 0.5f,
                        modifier = Modifier.align(Alignment.BottomStart),
                    )
                    TestSurface(
                        tag = "alpha-circle",
                        shape = CircleShape,
                        alpha = 0.5f,
                        modifier = Modifier.align(Alignment.BottomEnd),
                    )
                }
            }
            waitForIdle()

            val opaqueRectangle = onNodeWithTag("opaque-rectangle").captureToImage()
            opaqueRectangle.centerColor().assertCloseTo(Color.Red)
            opaqueRectangle.cornerColor().assertCloseTo(Color.Red)

            val opaqueCircle = onNodeWithTag("opaque-circle").captureToImage()
            opaqueCircle.centerColor().assertCloseTo(Color.Red)
            opaqueCircle.cornerColor().assertCloseTo(Color.Blue)

            val alphaRectangle = onNodeWithTag("alpha-rectangle").captureToImage()
            alphaRectangle.centerColor().assertCloseTo(RedOverBlue)
            alphaRectangle.cornerColor().assertCloseTo(RedOverBlue)

            val alphaCircle = onNodeWithTag("alpha-circle").captureToImage()
            alphaCircle.centerColor().assertCloseTo(RedOverBlue)
            alphaCircle.cornerColor().assertCloseTo(Color.Blue)
        }

    @Test
    fun groupAlphaPreservesOverlappingChildPixels() =
        runComposeUiTest {
            setContent {
                Box(
                    Modifier
                        .size(SurfaceSize)
                        .background(Color.Blue),
                ) {
                    Canvas(
                        Modifier
                            .fillMaxSize()
                            .interactionStyleComposite(interactionSource = null) { alpha = 0.5f }
                            .testTag("overlap"),
                    ) {
                        drawRect(Color.Red, size = Size(size.width * 0.75f, size.height))
                        drawRect(
                            Color.Red,
                            topLeft = Offset(size.width * 0.25f, 0f),
                            size = Size(size.width * 0.75f, size.height),
                        )
                    }
                }
            }
            waitForIdle()

            val image = onNodeWithTag("overlap").captureToImage()
            val pixels = image.toPixelMap()
            val singleDraw = pixels[image.width / 8, image.height / 2]
            val overlappingDraws = pixels[image.width / 2, image.height / 2]

            singleDraw.assertCloseTo(RedOverBlue)
            overlappingDraws.assertCloseTo(singleDraw)
        }

    @Composable
    private fun TestSurface(
        tag: String,
        shape: Shape,
        alpha: Float,
        modifier: Modifier = Modifier,
    ) {
        Box(
            modifier =
                modifier
                    .size(SurfaceSize)
                    .background(Color.Blue)
                    .testTag(tag),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .interactionStyleComposite(interactionSource = null) {
                        this.shape = shape
                        this.alpha = alpha
                    }
                    .background(Color.Red),
            )
        }
    }

    private fun ImageBitmap.centerColor(): Color = toPixelMap()[width / 2, height / 2]

    private fun ImageBitmap.cornerColor(): Color = toPixelMap()[1, 1]

    private fun Color.assertCloseTo(expected: Color) {
        assertTrue(isCloseTo(expected), "Expected $expected but was $this")
    }

    private fun Color.isCloseTo(
        expected: Color,
        tolerance: Float = 0.04f,
    ): Boolean =
        abs(red - expected.red) <= tolerance &&
            abs(green - expected.green) <= tolerance &&
            abs(blue - expected.blue) <= tolerance &&
            abs(alpha - expected.alpha) <= tolerance

    private companion object {
        val SurfaceSize = 40.dp
        val RedOverBlue = Color(red = 0.5f, green = 0f, blue = 0.5f)
    }
}
