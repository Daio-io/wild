// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import com.dropbox.differ.Color
import com.dropbox.differ.Image
import com.dropbox.differ.SimpleImageComparator
import com.github.takahirom.roborazzi.ThresholdValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScreenshotCompareOptionsTest {
    @Test
    fun defaultDifferDistanceRejectsFocusedButtonAntialiasDelta() {
        val result =
            SimpleImageComparator(maxDistance = 0.007f)
                .compare(focusedGoldenPixel(), focusedCiPixel())
        assertEquals(1, result.pixelDifferences)
        assertFalse(ThresholdValidator(0f).invoke(result))
    }

    @Test
    fun screenshotCompareOptionsAcceptsFocusedButtonAntialiasDelta() {
        val options = screenshotCompareOptions()
        val result = options.imageComparator.compare(focusedGoldenPixel(), focusedCiPixel())
        assertEquals(0, result.pixelDifferences)
        assertTrue(options.resultValidator.invoke(result))
    }

    @Test
    fun screenshotCompareOptionsRejectsRealColorRegression() {
        val options = screenshotCompareOptions()
        val enabled = solidImage(Color(36 / 255f, 52 / 255f, 71 / 255f, 1f))
        val focused = solidImage(Color(21 / 255f, 101 / 255f, 192 / 255f, 1f))
        val result = options.imageComparator.compare(enabled, focused)
        assertTrue(result.pixelDifferences > 0)
        assertFalse(options.resultValidator.invoke(result))
    }
}

/**
 * Minimal images reproducing the CI failure: one text-edge AA pixel differs by RGB ±2 on the
 * focused button desktop golden (`ButtonScreenshotTest.focused`).
 */
private fun focusedGoldenPixel(): Image =
    solidImage(
        fill = Color(21 / 255f, 101 / 255f, 192 / 255f, 1f),
        override = (234 to 39) to Color(25 / 255f, 104 / 255f, 193 / 255f, 1f),
    )

private fun focusedCiPixel(): Image =
    solidImage(
        fill = Color(21 / 255f, 101 / 255f, 192 / 255f, 1f),
        override = (234 to 39) to Color(23 / 255f, 102 / 255f, 192 / 255f, 1f),
    )

private fun solidImage(
    fill: Color,
    override: Pair<Pair<Int, Int>, Color>? = null,
): Image =
    object : Image {
        override val width: Int = 480
        override val height: Int = 80

        override fun getPixel(
            x: Int,
            y: Int,
        ): Color = if (override != null && x to y == override.first) override.second else fill
    }
