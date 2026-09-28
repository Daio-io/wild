// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.slider

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SliderStateTest {
    @Test
    fun stateCoercesValueAndExposesNormalizedFraction() {
        val state = SliderState(value = 25f, valueRange = 10f..20f, steps = 0)

        assertEquals(20f, state.value)
        assertEquals(1f, state.fraction)
    }

    @Test
    fun stateSnapsProposedValuesToDiscreteSteps() {
        val state = SliderState(value = 0f, valueRange = 0f..100f, steps = 3)

        assertEquals(25f, state.snap(37f))
        assertEquals(100f, state.snap(88f))
    }

    @Test
    fun stateRejectsInvalidRangesAndSteps() {
        assertFailsWith<IllegalArgumentException> {
            SliderState(value = 0f, valueRange = 1f..1f, steps = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            SliderState(value = 0f, valueRange = 0f..1f, steps = -1)
        }
        assertFailsWith<IllegalArgumentException> {
            SliderState(value = 0f, valueRange = Float.NaN..1f, steps = 0)
        }
    }

    @Test
    fun rangeStateNeverCrossesThumbs() {
        val state = RangeSliderState(value = 0.25f..0.75f, valueRange = 0f..1f, steps = 0)

        assertEquals(0.75f, state.coerceStart(0.9f))
        assertEquals(0.25f, state.coerceEnd(0.1f))
    }

    @Test
    fun rtlFractionIsReversed() {
        assertTrue(kotlin.math.abs(fractionToValue(0.8f, 0f..1f, rtl = true) - 0.2f) < 0.0001f)
        assertTrue(kotlin.math.abs(valueToFraction(0.2f, 0f..1f, rtl = true) - 0.8f) < 0.0001f)
    }
}
