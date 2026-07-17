// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.animation.core.snap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.Border
import io.daio.wild.style.interactionStyle
import kotlinx.coroutines.runBlocking
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class StyleLayerRenderingTest {
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
                            .interactionStyle(null) { alpha = 0.5f }
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

    @Test
    fun transitionsBetweenNoLayerAndLayerModesPreserveOutput() =
        runComposeUiTest {
            var shape by mutableStateOf<Shape>(RectangleShape)
            var alpha by mutableStateOf(1f)

            setContent {
                TestSurface(tag = "transition", shape = shape, alpha = alpha)
            }
            waitForIdle()

            onNodeWithTag("transition").captureToImage().run {
                centerColor().assertCloseTo(Color.Red)
                cornerColor().assertCloseTo(Color.Red)
            }

            runOnIdle {
                shape = CircleShape
                alpha = 0.5f
            }
            waitForIdle()

            onNodeWithTag("transition").captureToImage().run {
                centerColor().assertCloseTo(RedOverBlue)
                cornerColor().assertCloseTo(Color.Blue)
            }

            runOnIdle {
                shape = RectangleShape
                alpha = 1f
            }
            waitForIdle()

            onNodeWithTag("transition").captureToImage().run {
                centerColor().assertCloseTo(Color.Red)
                cornerColor().assertCloseTo(Color.Red)
            }
        }

    @Test
    fun focusAndPressScaleRenderAtDeterministicSizes() =
        runComposeUiTest {
            val interactionSource = MutableInteractionSource()

            setContent {
                Box(
                    modifier =
                        Modifier
                            .size(SurfaceSize)
                            .background(Color.Blue)
                            .testTag("scale-root"),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        Modifier
                            .size(24.dp)
                            .interactionStyle(interactionSource) {
                                color = Color.Red
                                scale =
                                    when {
                                        pressed -> 0.5f
                                        focused -> 0.75f
                                        else -> 1f
                                    }
                                scaleAnimationSpec = snap()
                            },
                    )
                }
            }
            waitForIdle()

            val defaultWidth = onNodeWithTag("scale-root").captureToImage().redWidth()

            lateinit var focus: FocusInteraction.Focus
            runBlocking {
                focus = FocusInteraction.Focus()
                interactionSource.emit(focus)
            }
            waitForIdle()
            val focusedWidth = onNodeWithTag("scale-root").captureToImage().redWidth()

            lateinit var press: PressInteraction.Press
            runBlocking {
                press = PressInteraction.Press(Offset.Zero)
                interactionSource.emit(press)
            }
            waitForIdle()
            val pressedWidth = onNodeWithTag("scale-root").captureToImage().redWidth()

            assertTrue(defaultWidth > focusedWidth)
            assertTrue(focusedWidth > pressedWidth)

            runBlocking {
                interactionSource.emit(PressInteraction.Release(press))
                interactionSource.emit(FocusInteraction.Unfocus(focus))
            }
        }

    @Test
    fun borderInsetsRemainOutsideTheClippedSurfaceWhenRequested() =
        runComposeUiTest {
            setContent {
                Box(Modifier.size(80.dp)) {
                    BorderSurface(
                        tag = "outside-border",
                        inset = 3.dp,
                    )
                    BorderSurface(
                        tag = "inside-border",
                        inset = -3.dp,
                        modifier = Modifier.align(Alignment.TopEnd),
                    )
                }
            }
            waitForIdle()

            val outsideBorder = onNodeWithTag("outside-border").captureToImage()
            val insideBorder = onNodeWithTag("inside-border").captureToImage()

            assertTrue(outsideBorder.countYellowOutsideChildBounds() > 0)
            assertEquals(0, insideBorder.countYellowOutsideChildBounds())
        }

    @Test
    fun nestedStyledComponentsKeepIndependentClipAndColor() =
        runComposeUiTest {
            setContent {
                Box(
                    modifier =
                        Modifier
                            .size(SurfaceSize)
                            .background(Color.Blue)
                            .testTag("nested"),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(32.dp)
                                .interactionStyle(null) {
                                    color = Color.Red
                                    shape = CircleShape
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .interactionStyle(null) {
                                    color = Color.Green
                                    shape = RectangleShape
                                },
                        )
                    }
                }
            }
            waitForIdle()

            val image = onNodeWithTag("nested").captureToImage()
            val pixels = image.toPixelMap()
            pixels[image.width / 2, image.height / 2].assertCloseTo(Color.Green)
            pixels[image.width / 2, image.height / 8].assertCloseTo(Color.Red)
            image.cornerColor().assertCloseTo(Color.Blue)
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
                    .interactionStyle(null) {
                        this.shape = shape
                        this.alpha = alpha
                    }
                    .background(Color.Red),
            )
        }
    }

    @Composable
    private fun BorderSurface(
        tag: String,
        inset: Dp,
        modifier: Modifier = Modifier,
    ) {
        Box(
            modifier =
                modifier
                    .size(SurfaceSize)
                    .background(Color.Blue)
                    .testTag(tag),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .size(24.dp)
                    .interactionStyle(null) {
                        color = Color.Red
                        shape = CircleShape
                        border = Border(width = 2.dp, color = Color.Yellow, inset = inset)
                    },
            )
        }
    }

    private fun ImageBitmap.centerColor(): Color = toPixelMap()[width / 2, height / 2]

    private fun ImageBitmap.cornerColor(): Color = toPixelMap()[1, 1]

    private fun ImageBitmap.redWidth(): Int {
        val pixels = toPixelMap()
        val y = height / 2
        return (0 until width).count { x -> pixels[x, y].isCloseTo(Color.Red) }
    }

    private fun ImageBitmap.countYellowOutsideChildBounds(): Int {
        val pixels = toPixelMap()
        val childStartX = (width * 0.2f).toInt()
        val childEndX = (width * 0.8f).toInt()
        val childStartY = (height * 0.2f).toInt()
        val childEndY = (height * 0.8f).toInt()
        var count = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val outsideChild =
                    x < childStartX || x >= childEndX || y < childStartY || y >= childEndY
                if (outsideChild && pixels[x, y].isCloseTo(Color.Yellow)) count++
            }
        }
        return count
    }

    private fun Color.assertCloseTo(expected: Color) {
        assertTrue(
            isCloseTo(expected),
            "Expected $expected but was $this",
        )
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
