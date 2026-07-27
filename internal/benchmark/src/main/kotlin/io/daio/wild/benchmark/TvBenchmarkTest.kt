// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.benchmark

import android.os.SystemClock
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

/**
 * Confirmation / release profile. For faster local iteration, temporarily lower
 * [DEFAULT_ITERATIONS], switch [BENCHMARK_COMPILATION_MODE] to [CompilationMode.None], and/or
 * shorten [SCROLL_GRID_FOCUS_SEQUENCE]. Restore this profile before release claims.
 */
private const val DEFAULT_ITERATIONS = 20
private const val APP_PACKAGE = "io.daio.wild.playbook.tv"
private const val MODE_EXTRA = "MODE"
private const val ITEMS_EXTRA = "ITEMS"
private const val RECOMPOSITION_DRIVER_EXTRA = "RECOMPOSITION_DRIVER"
private const val GRID_MODE = "grid"
private const val FIRST_FOCUS_TARGET = "benchmark-item-0-0"
private const val SCROLL_TERMINAL_FOCUS_TARGET = "benchmark-item-5-20"
private const val FOCUS_FLIP_TERMINAL_TARGET = "benchmark-item-0-15"
private const val RECOMPOSITION_MARKER_PREFIX = "benchmark-recomposition-"
private const val FOCUS_TARGET_TIMEOUT_MS = 10_000L

/**
 * Fixed delay between DPAD keys. Prefer this over [UiDevice.waitForIdle] alone so bursty focus
 * moves stay paced across devices; compare runs only when they share the same pace.
 */
private const val INPUT_FRAME_PACE_MS = 50L
private val BENCHMARK_COMPILATION_MODE = CompilationMode.Partial()

private enum class StyleVariant(val extraValue: String) {
    CurrentTraversal("current_traversal"),
    ExplicitSourceFastPath("explicit_source_fast_path"),
    NullSourceCompatibility("null_source_compatibility"),
    CandidateComposite("candidate_composite"),
    MaterialSurface("material_surface"),
}

/**
 * Full scroll/focus path for confirmation runs. Nested [LazyRow]s reset column on DOWN, so the
 * sequence re-scrolls horizontally after each vertical move. The playbook grid centers focused
 * items via [androidx.compose.foundation.gestures.BringIntoViewSpec].
 *
 * For local optimization loops, shorten this path (for example end at `benchmark-item-2-10`) and
 * temporarily lower [DEFAULT_ITERATIONS] / use [CompilationMode.None].
 */
private val SCROLL_GRID_FOCUS_SEQUENCE =
    buildList {
        repeat(20) { add(KeyEvent.KEYCODE_DPAD_RIGHT) }
        repeat(5) {
            add(KeyEvent.KEYCODE_DPAD_DOWN)
            repeat(20) { add(KeyEvent.KEYCODE_DPAD_RIGHT) }
        }
        repeat(5) { add(KeyEvent.KEYCODE_DPAD_LEFT) }
        repeat(5) { add(KeyEvent.KEYCODE_DPAD_RIGHT) }
    }

/** Horizontal focus flips — isolates style update cost vs deep grid scroll. */
private val FOCUS_FLIP_SEQUENCE =
    buildList {
        repeat(15) { add(KeyEvent.KEYCODE_DPAD_RIGHT) }
        repeat(15) { add(KeyEvent.KEYCODE_DPAD_LEFT) }
        repeat(15) { add(KeyEvent.KEYCODE_DPAD_RIGHT) }
    }

@RunWith(AndroidJUnit4::class)
class TvBenchmarkTest {
    @get:Rule
    val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollGridWithCurrentTraversal() {
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.CurrentTraversal,
            focusSequence = SCROLL_GRID_FOCUS_SEQUENCE,
            terminalFocusTarget = SCROLL_TERMINAL_FOCUS_TARGET,
        )
    }

    @Test
    fun scrollGridWithCandidateComposite() {
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.CandidateComposite,
            focusSequence = SCROLL_GRID_FOCUS_SEQUENCE,
            terminalFocusTarget = SCROLL_TERMINAL_FOCUS_TARGET,
        )
    }

    @Test
    fun focusFlipWithCurrentTraversal() {
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.CurrentTraversal,
            focusSequence = FOCUS_FLIP_SEQUENCE,
            terminalFocusTarget = FOCUS_FLIP_TERMINAL_TARGET,
        )
    }

    @Test
    fun focusFlipWithCandidateComposite() {
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.CandidateComposite,
            focusSequence = FOCUS_FLIP_SEQUENCE,
            terminalFocusTarget = FOCUS_FLIP_TERMINAL_TARGET,
        )
    }

    @Test
    fun scrollGridWithMaterialSurface() {
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.MaterialSurface,
            focusSequence = SCROLL_GRID_FOCUS_SEQUENCE,
            terminalFocusTarget = SCROLL_TERMINAL_FOCUS_TARGET,
        )
    }

    @Test
    fun recomposeUnchangedGridWithExplicitSourceFastPath() {
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.ExplicitSourceFastPath,
            focusSequence = SCROLL_GRID_FOCUS_SEQUENCE,
            terminalFocusTarget = SCROLL_TERMINAL_FOCUS_TARGET,
            enableRecompositionDriver = true,
        )
    }

    @Test
    fun recomposeUnchangedGridWithNullSourceCompatibility() {
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.NullSourceCompatibility,
            focusSequence = SCROLL_GRID_FOCUS_SEQUENCE,
            terminalFocusTarget = SCROLL_TERMINAL_FOCUS_TARGET,
            enableRecompositionDriver = true,
        )
    }
}

private fun MacrobenchmarkRule.measureStyleVariant(
    variant: StyleVariant,
    focusSequence: List<Int>,
    terminalFocusTarget: String,
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
        device.runDeterministicFocusSequence(
            focusSequence = focusSequence,
            terminalFocusTarget = terminalFocusTarget,
            enableRecompositionDriver = enableRecompositionDriver,
        )
    }
}

@OptIn(ExperimentalMetricApi::class)
private fun styleMetrics(): List<Metric> =
    listOf(
        FrameTimingMetric(),
        MemoryUsageMetric(MemoryUsageMetric.Mode.Max),
    )

private fun UiDevice.runDeterministicFocusSequence(
    focusSequence: List<Int>,
    terminalFocusTarget: String,
    enableRecompositionDriver: Boolean,
) {
    var recompositionGeneration = 0

    focusSequence.forEach { keyCode ->
        pressKeyCode(keyCode)
        SystemClock.sleep(INPUT_FRAME_PACE_MS)
        if (enableRecompositionDriver) {
            requestUnchangedRecomposition(++recompositionGeneration)
        }
    }

    check(wait(Until.hasObject(By.desc(terminalFocusTarget)), FOCUS_TARGET_TIMEOUT_MS)) {
        "Timed out waiting for terminal benchmark focus target $terminalFocusTarget"
    }
}

private fun UiDevice.requestUnchangedRecomposition(generation: Int) {
    check(pressKeyCode(KeyEvent.KEYCODE_R)) { "Failed to inject recomposition key" }
    SystemClock.sleep(INPUT_FRAME_PACE_MS)

    val marker = recompositionMarker(generation)
    check(wait(Until.hasObject(By.desc(marker)), FOCUS_TARGET_TIMEOUT_MS)) {
        "Timed out waiting for recomposition marker $marker"
    }
}

private fun recompositionMarker(generation: Int): String = "$RECOMPOSITION_MARKER_PREFIX$generation"
