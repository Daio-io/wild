// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.benchmark

import androidx.benchmark.BlackHole
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.compose.ui.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.daio.wild.components.button.ButtonDefaults
import io.daio.wild.style.StyleDefaults
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StyleDefaultsBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val customColors = StyleDefaults.colors(backgroundColor = Color.Red)

    @Test
    fun defaultColors() =
        benchmarkRule.measureRepeated {
            BlackHole.consume(StyleDefaults.colors())
        }

    @Test
    fun defaultBorders() =
        benchmarkRule.measureRepeated {
            BlackHole.consume(StyleDefaults.borders())
        }

    @Test
    fun defaultScale() =
        benchmarkRule.measureRepeated {
            BlackHole.consume(StyleDefaults.scale())
        }

    @Test
    fun defaultShapes() =
        benchmarkRule.measureRepeated {
            BlackHole.consume(StyleDefaults.shapes())
        }

    @Test
    fun defaultAlpha() =
        benchmarkRule.measureRepeated {
            BlackHole.consume(StyleDefaults.alpha())
        }

    @Test
    fun defaultStyle() =
        benchmarkRule.measureRepeated {
            BlackHole.consume(StyleDefaults.style())
        }

    @Test
    fun defaultButtonStyle() =
        benchmarkRule.measureRepeated {
            BlackHole.consume(ButtonDefaults.style())
        }

    @Test
    fun partiallyCustomizedStyle() =
        benchmarkRule.measureRepeated {
            BlackHole.consume(StyleDefaults.style(colors = customColors))
        }
}
