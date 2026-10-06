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

    private val oneOverride: ComponentStyleScope.() -> Unit = { color = Color.Green }
    private val override2: ComponentStyleScope.() -> Unit = { scale = 1.1f }
    private val override3: ComponentStyleScope.() -> Unit = { alpha = 0.9f }
    private val override4: ComponentStyleScope.() -> Unit = { color = Color.Cyan }
    private val override5: ComponentStyleScope.() -> Unit = { scale = 1.2f }
    private val override6: ComponentStyleScope.() -> Unit = { alpha = 0.8f }
    private val override7: ComponentStyleScope.() -> Unit = { color = Color.Magenta }
    private val override8: ComponentStyleScope.() -> Unit = { scale = 1.3f }
    private val override9: ComponentStyleScope.() -> Unit = { alpha = 0.7f }
    private val override10: ComponentStyleScope.() -> Unit = { color = Color.Yellow }
    private val override11: ComponentStyleScope.() -> Unit = { scale = 1.4f }
    private val override12: ComponentStyleScope.() -> Unit = { alpha = 0.6f }
    private val override13: ComponentStyleScope.() -> Unit = { color = Color.Gray }
    private val override14: ComponentStyleScope.() -> Unit = { scale = 1.5f }
    private val override15: ComponentStyleScope.() -> Unit = { alpha = 0.5f }
    private val override16: ComponentStyleScope.() -> Unit = { color = Color.Black }

    private val styleSpec1: StyleSpec = styleSpec(style, oneOverride)
    private val styleSpec4: StyleSpec =
        styleSpec(style, oneOverride)
            .then(override2)
            .then(override3)
            .then(override4)
    private val styleSpec16: StyleSpec =
        styleSpec(style, oneOverride)
            .then(override2)
            .then(override3)
            .then(override4)
            .then(override5)
            .then(override6)
            .then(override7)
            .then(override8)
            .then(override9)
            .then(override10)
            .then(override11)
            .then(override12)
            .then(override13)
            .then(override14)
            .then(override15)
            .then(override16)

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
