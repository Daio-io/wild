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
import io.daio.wild.style.StyleDefaults
import io.daio.wild.style.StyleScope
import io.daio.wild.style.interactionStyle
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Construction-only microbenchmarks for value vs hoisted-lambda [interactionStyle] modifiers.
 * These measure definition/modifier construction, never attached node resolution.
 */
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
}
