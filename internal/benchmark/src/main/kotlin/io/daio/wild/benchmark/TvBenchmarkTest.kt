// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.benchmark

import android.view.KeyEvent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MemoryUsageMetric
import androidx.benchmark.macro.Metric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val DEFAULT_ITERATIONS = 20
private const val APP_PACKAGE = "io.daio.wild.playbook.tv"
private const val MODE_EXTRA = "MODE"
private const val ITEMS_EXTRA = "ITEMS"
private const val RECOMPOSITION_DRIVER_EXTRA = "RECOMPOSITION_DRIVER"
private const val GRID_MODE = "grid"
private const val FIRST_FOCUS_TARGET = "benchmark-item-0-0"
private const val TERMINAL_FOCUS_TARGET = "benchmark-item-5-20"
private const val RECOMPOSITION_MARKER_PREFIX = "benchmark-recomposition-"
private const val FOCUS_TARGET_TIMEOUT_MS = 5_000L
private const val INPUT_PACE_MS = 80L
private val BENCHMARK_COMPILATION_MODE = CompilationMode.Partial()

private enum class StyleVariant(val extraValue: String) {
    CurrentTraversal("current_traversal"),
    ExplicitSourceFastPath("explicit_source_fast_path"),
    NullSourceCompatibility("null_source_compatibility"),
    CandidateComposite("candidate_composite"),
    MaterialSurface("material_surface"),
}

private val DETERMINISTIC_FOCUS_SEQUENCE =
    listOf(
        KeyEvent.KEYCODE_DPAD_RIGHT to 15,
        KeyEvent.KEYCODE_DPAD_DOWN to 10,
        KeyEvent.KEYCODE_DPAD_RIGHT to 15,
        KeyEvent.KEYCODE_DPAD_LEFT to 10,
        KeyEvent.KEYCODE_DPAD_UP to 5,
    ).flatMap { (keyCode, count) -> List(count) { keyCode } }

@RunWith(AndroidJUnit4::class)
class TvBenchmarkTest {
    @get:Rule
    val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollGridWithCurrentTraversal() {
        benchmarkRule.measureStyleVariant(StyleVariant.CurrentTraversal)
    }

    @Test
    fun scrollGridWithCandidateComposite() {
        benchmarkRule.measureStyleVariant(StyleVariant.CandidateComposite)
    }

    @Test
    fun scrollGridWithMaterialSurface() {
        benchmarkRule.measureStyleVariant(StyleVariant.MaterialSurface)
    }

    @Test
    fun recomposeUnchangedGridWithExplicitSourceFastPath() {
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.ExplicitSourceFastPath,
            enableRecompositionDriver = true,
        )
    }

    @Test
    fun recomposeUnchangedGridWithNullSourceCompatibility() {
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.NullSourceCompatibility,
            enableRecompositionDriver = true,
        )
    }
}

private fun MacrobenchmarkRule.measureStyleVariant(
    variant: StyleVariant,
    enableRecompositionDriver: Boolean = false,
) {
    measureRepeated(
        packageName = APP_PACKAGE,
        metrics = styleMetrics(),
        compilationMode = BENCHMARK_COMPILATION_MODE,
        startupMode = StartupMode.WARM,
        iterations = DEFAULT_ITERATIONS,
        setupBlock = {
            startActivityAndWait {
                it.putExtra(MODE_EXTRA, GRID_MODE)
                it.putExtra(ITEMS_EXTRA, variant.extraValue)
                it.putExtra(RECOMPOSITION_DRIVER_EXTRA, enableRecompositionDriver)
            }
            device.waitForIdle()
            check(device.wait(Until.hasObject(By.desc(FIRST_FOCUS_TARGET)), FOCUS_TARGET_TIMEOUT_MS)) {
                "Timed out waiting for first benchmark focus target $FIRST_FOCUS_TARGET"
            }
            if (enableRecompositionDriver) {
                check(device.wait(Until.hasObject(By.desc(recompositionMarker(0))), FOCUS_TARGET_TIMEOUT_MS)) {
                    "Timed out waiting for initial recomposition marker ${recompositionMarker(0)}"
                }
            }
        },
    ) {
        device.runDeterministicFocusSequence(enableRecompositionDriver)
    }
}

@OptIn(ExperimentalMetricApi::class)
private fun styleMetrics(): List<Metric> =
    listOf(
        FrameTimingMetric(),
        MemoryUsageMetric(MemoryUsageMetric.Mode.Max),
    )

private fun UiDevice.runDeterministicFocusSequence(enableRecompositionDriver: Boolean) {
    var recompositionGeneration = 0

    pressKeyCode(KeyEvent.KEYCODE_DPAD_RIGHT)
    waitForIdle()
    if (enableRecompositionDriver) {
        requestUnchangedRecomposition(++recompositionGeneration)
    }

    DETERMINISTIC_FOCUS_SEQUENCE.forEach { keyCode ->
        pressKeyCode(keyCode)
        waitForIdle(INPUT_PACE_MS)
        if (enableRecompositionDriver) {
            requestUnchangedRecomposition(++recompositionGeneration)
        }
    }

    check(wait(Until.hasObject(By.desc(TERMINAL_FOCUS_TARGET)), FOCUS_TARGET_TIMEOUT_MS)) {
        "Timed out waiting for terminal benchmark focus target $TERMINAL_FOCUS_TARGET"
    }
}

private fun UiDevice.requestUnchangedRecomposition(generation: Int) {
    check(pressKeyCode(KeyEvent.KEYCODE_R)) { "Failed to inject recomposition key" }
    waitForIdle(INPUT_PACE_MS)

    val marker = recompositionMarker(generation)
    check(wait(Until.hasObject(By.desc(marker)), FOCUS_TARGET_TIMEOUT_MS)) {
        "Timed out waiting for recomposition marker $marker"
    }
}

private fun recompositionMarker(generation: Int): String = "$RECOMPOSITION_MARKER_PREFIX$generation"
