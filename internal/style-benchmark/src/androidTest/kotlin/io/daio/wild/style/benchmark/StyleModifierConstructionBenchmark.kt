// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.benchmark

import androidx.benchmark.BlackHole
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.ComponentStyleScope
import io.daio.wild.style.StyleDefaults
import io.daio.wild.style.StyleScope
import io.daio.wild.style.StyleSpec
import io.daio.wild.style.interactionStyle
import io.daio.wild.style.styleSpec
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Construction-only microbenchmarks for value vs hoisted-lambda vs [StyleSpec] modifiers.
 * These measure definition/modifier construction, never attached node resolution.
 *
 * StyleSpec cases hoist stable callbacks outside [measureRepeated] so timed work does not
 * allocate fresh override lambdas.
 */
@OptIn(ExperimentalWildApi::class)
@RunWith(AndroidJUnit4::class)
class StyleModifierConstructionBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val interactionSource = MutableInteractionSource()
    private val style =
        StyleDefaults.style(
            colors = StyleDefaults.colors(backgroundColor = Color.Red, focusedBackgroundColor = Color.Blue),
        )
    private val styleBlock: StyleScope.() -> Unit = {
        color = if (focused) Color.Blue else Color.Red
    }

    private val colorGreen: ComponentStyleScope.() -> Unit = { color = Color.Green }
    private val scale110: ComponentStyleScope.() -> Unit = { scale = 1.1f }
    private val alpha90: ComponentStyleScope.() -> Unit = { alpha = 0.9f }
    private val colorCyan: ComponentStyleScope.() -> Unit = { color = Color.Cyan }
    private val scale120: ComponentStyleScope.() -> Unit = { scale = 1.2f }
    private val alpha80: ComponentStyleScope.() -> Unit = { alpha = 0.8f }
    private val colorMagenta: ComponentStyleScope.() -> Unit = { color = Color.Magenta }
    private val scale130: ComponentStyleScope.() -> Unit = { scale = 1.3f }
    private val alpha70: ComponentStyleScope.() -> Unit = { alpha = 0.7f }
    private val colorYellow: ComponentStyleScope.() -> Unit = { color = Color.Yellow }
    private val scale140: ComponentStyleScope.() -> Unit = { scale = 1.4f }
    private val alpha60: ComponentStyleScope.() -> Unit = { alpha = 0.6f }
    private val colorGray: ComponentStyleScope.() -> Unit = { color = Color.Gray }
    private val scale150: ComponentStyleScope.() -> Unit = { scale = 1.5f }
    private val alpha50: ComponentStyleScope.() -> Unit = { alpha = 0.5f }
    private val colorBlack: ComponentStyleScope.() -> Unit = { color = Color.Black }

    private val styleSpec1: StyleSpec = styleSpec(style, colorGreen)
    private val styleSpec4: StyleSpec =
        styleSpec(style, colorGreen)
            .then(scale110)
            .then(alpha90)
            .then(colorCyan)
    private val styleSpec16: StyleSpec =
        styleSpec(style, colorGreen)
            .then(scale110)
            .then(alpha90)
            .then(colorCyan)
            .then(scale120)
            .then(alpha80)
            .then(colorMagenta)
            .then(scale130)
            .then(alpha70)
            .then(colorYellow)
            .then(scale140)
            .then(alpha60)
            .then(colorGray)
            .then(scale150)
            .then(alpha50)
            .then(colorBlack)

    @Test
    fun valueInteractionStyle_construction() =
        benchmarkRule.measureRepeated {
            BlackHole.consume(
                Modifier.interactionStyle(
                    interactionSource = interactionSource,
                    style = style,
                ),
            )
        }

    @Test
    fun hoistedLambdaInteractionStyle_construction() =
        benchmarkRule.measureRepeated {
            BlackHole.consume(
                Modifier.interactionStyle(
                    interactionSource = interactionSource,
                    block = styleBlock,
                ),
            )
        }

    @Test
    fun styleSpec1Override_construction() =
        benchmarkRule.measureRepeated {
            BlackHole.consume(
                Modifier.interactionStyle(
                    interactionSource = interactionSource,
                    style = styleSpec1,
                ),
            )
        }

    @Test
    fun styleSpec4Override_construction() =
        benchmarkRule.measureRepeated {
            BlackHole.consume(
                Modifier.interactionStyle(
                    interactionSource = interactionSource,
                    style = styleSpec4,
                ),
            )
        }

    @Test
    fun styleSpec16Override_construction() =
        benchmarkRule.measureRepeated {
            BlackHole.consume(
                Modifier.interactionStyle(
                    interactionSource = interactionSource,
                    style = styleSpec16,
                ),
            )
        }
}
