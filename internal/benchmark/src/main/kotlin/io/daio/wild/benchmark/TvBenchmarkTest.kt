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
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Confirmation / release profile remains the default. Pass
 * `-Pandroid.testInstrumentationRunnerArguments.benchmarkProfile=local_short` for a shorter,
 * still-like-for-like local device pass.
 */
private const val APP_PACKAGE = "io.daio.wild.playbook.tv"
private const val MODE_EXTRA = "MODE"
private const val ITEMS_EXTRA = "ITEMS"
private const val RECOMPOSITION_DRIVER_EXTRA = "RECOMPOSITION_DRIVER"
private const val GRID_MODE = "grid"
private const val FOCUS_FLIP_MODE = "focus_flip"
private const val FIRST_FOCUS_TARGET = "benchmark-item-0-0"
private const val SCROLL_TERMINAL_FOCUS_TARGET = "benchmark-item-5-20"
private const val FOCUS_FLIP_TERMINAL_TARGET = "benchmark-item-0-1"
private const val RECOMPOSITION_MARKER_PREFIX = "benchmark-recomposition-"
private const val FOCUS_TARGET_TIMEOUT_MS = 10_000L
private const val FOCUS_POLL_INTERVAL_MS = 50L
private const val BENCHMARK_PROFILE_ARGUMENT = "benchmarkProfile"
private const val LOCAL_SHORT_PROFILE = "local_short"

/**
 * Fixed delay between DPAD keys. Prefer this over [UiDevice.waitForIdle] alone so bursty focus
 * moves stay paced across devices; compare runs only when they share the same pace.
 */
private const val INPUT_FRAME_PACE_MS = 50L
private val BENCHMARK_COMPILATION_MODE = CompilationMode.Partial()

private data class BenchmarkProfile(
    val iterations: Int,
    val scrollFocusSequence: List<Int>,
    val scrollTerminalTarget: String,
)

private enum class StyleVariant(val extraValue: String) {
    WildClickable("wild_clickable"),
    ExplicitSourceFastPath("explicit_source_fast_path"),
    NullSourceCompatibility("null_source_compatibility"),
    WildContainer("wild_container"),
    MaterialSurface("material_surface"),
}

/**
 * Full scroll/focus path for confirmation runs. Nested [LazyRow]s reset column on DOWN, so the
 * sequence re-scrolls horizontally after each vertical move. The playbook grid centers focused
 * items via [androidx.compose.foundation.gestures.BringIntoViewSpec].
 *
 * For local exploration, pass
 * `-Pandroid.testInstrumentationRunnerArguments.benchmarkProfile=local_short` (or use
 * `./scripts/run-tv-style-benchmarks.sh --profile local_short`) instead of editing this path.
 */
private fun confirmationScrollGridFocusSequence(): List<Int> =
    buildList {
        repeat(20) { add(KeyEvent.KEYCODE_DPAD_RIGHT) }
        repeat(5) {
            add(KeyEvent.KEYCODE_DPAD_DOWN)
            repeat(20) { add(KeyEvent.KEYCODE_DPAD_RIGHT) }
        }
        repeat(5) { add(KeyEvent.KEYCODE_DPAD_LEFT) }
        repeat(5) { add(KeyEvent.KEYCODE_DPAD_RIGHT) }
    }

private fun activeBenchmarkProfile(): BenchmarkProfile =
    when (InstrumentationRegistry.getArguments().getString(BENCHMARK_PROFILE_ARGUMENT)?.lowercase()) {
        LOCAL_SHORT_PROFILE ->
            BenchmarkProfile(
                iterations = 5,
                scrollFocusSequence =
                    buildList {
                        repeat(10) { add(KeyEvent.KEYCODE_DPAD_RIGHT) }
                        repeat(2) {
                            add(KeyEvent.KEYCODE_DPAD_DOWN)
                            repeat(10) { add(KeyEvent.KEYCODE_DPAD_RIGHT) }
                        }
                    },
                scrollTerminalTarget = "benchmark-item-2-10",
            )
        else ->
            BenchmarkProfile(
                iterations = 20,
                scrollFocusSequence = confirmationScrollGridFocusSequence(),
                scrollTerminalTarget = SCROLL_TERMINAL_FOCUS_TARGET,
            )
    }

/** Alternates focus between two stationary items, then ends on the second item. */
private val FOCUS_FLIP_SEQUENCE =
    buildList {
        repeat(15) {
            add(KeyEvent.KEYCODE_DPAD_RIGHT)
            add(KeyEvent.KEYCODE_DPAD_LEFT)
        }
        add(KeyEvent.KEYCODE_DPAD_RIGHT)
    }

@RunWith(AndroidJUnit4::class)
class TvBenchmarkTest {
    @get:Rule
    val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollGridWithWildClickable() {
        val profile = activeBenchmarkProfile()
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.WildClickable,
            iterations = profile.iterations,
            focusSequence = profile.scrollFocusSequence,
            terminalFocusTarget = profile.scrollTerminalTarget,
        )
    }

    @Test
    fun scrollGridWithWildContainer() {
        val profile = activeBenchmarkProfile()
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.WildContainer,
            iterations = profile.iterations,
            focusSequence = profile.scrollFocusSequence,
            terminalFocusTarget = profile.scrollTerminalTarget,
        )
    }

    @Test
    fun focusFlipWithWildClickable() {
        val profile = activeBenchmarkProfile()
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.WildClickable,
            mode = FOCUS_FLIP_MODE,
            iterations = profile.iterations,
            focusSequence = FOCUS_FLIP_SEQUENCE,
            terminalFocusTarget = FOCUS_FLIP_TERMINAL_TARGET,
        )
    }

    @Test
    fun focusFlipWithWildContainer() {
        val profile = activeBenchmarkProfile()
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.WildContainer,
            mode = FOCUS_FLIP_MODE,
            iterations = profile.iterations,
            focusSequence = FOCUS_FLIP_SEQUENCE,
            terminalFocusTarget = FOCUS_FLIP_TERMINAL_TARGET,
        )
    }

    @Test
    fun scrollGridWithMaterialSurface() {
        val profile = activeBenchmarkProfile()
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.MaterialSurface,
            iterations = profile.iterations,
            focusSequence = profile.scrollFocusSequence,
            terminalFocusTarget = profile.scrollTerminalTarget,
        )
    }

    @Test
    fun recomposeUnchangedGridWithExplicitSourceFastPath() {
        val profile = activeBenchmarkProfile()
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.ExplicitSourceFastPath,
            iterations = profile.iterations,
            focusSequence = profile.scrollFocusSequence,
            terminalFocusTarget = profile.scrollTerminalTarget,
            enableRecompositionDriver = true,
        )
    }

    @Test
    fun recomposeUnchangedGridWithNullSourceCompatibility() {
        val profile = activeBenchmarkProfile()
        benchmarkRule.measureStyleVariant(
            variant = StyleVariant.NullSourceCompatibility,
            iterations = profile.iterations,
            focusSequence = profile.scrollFocusSequence,
            terminalFocusTarget = profile.scrollTerminalTarget,
            enableRecompositionDriver = true,
        )
    }
}

private fun MacrobenchmarkRule.measureStyleVariant(
    variant: StyleVariant,
    mode: String = GRID_MODE,
    iterations: Int,
    focusSequence: List<Int>,
    terminalFocusTarget: String,
    enableRecompositionDriver: Boolean = false,
) {
    measureRepeated(
        packageName = APP_PACKAGE,
        metrics = styleMetrics(),
        compilationMode = BENCHMARK_COMPILATION_MODE,
        startupMode = StartupMode.WARM,
        iterations = iterations,
        setupBlock = {
            startActivityAndWait {
                it.putExtra(MODE_EXTRA, mode)
                it.putExtra(ITEMS_EXTRA, variant.extraValue)
                it.putExtra(RECOMPOSITION_DRIVER_EXTRA, enableRecompositionDriver)
            }
            device.waitForIdle()
            check(device.waitUntilFocusedMarker(FIRST_FOCUS_TARGET, FOCUS_TARGET_TIMEOUT_MS)) {
                "Timed out waiting for first focused benchmark marker $FIRST_FOCUS_TARGET"
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
        check(pressKeyCode(keyCode)) { "Failed to inject DPAD key $keyCode" }
        SystemClock.sleep(INPUT_FRAME_PACE_MS)
        if (enableRecompositionDriver) {
            requestUnchangedRecomposition(++recompositionGeneration)
        }
    }

    check(waitUntilFocusedMarker(terminalFocusTarget, FOCUS_TARGET_TIMEOUT_MS)) {
        "Timed out waiting for terminal focused benchmark marker $terminalFocusTarget"
    }
}

/**
 * Waits until [marker] is present and either focused itself or has a focused ancestor.
 *
 * On Fire TV / Compose, [Modifier.clickable] focus often lands on a parent node while
 * contentDescription stays on a child Text. Matching `By.desc(marker).focused(true)` alone
 * therefore times out even when focus traversal succeeded.
 */
private fun UiDevice.waitUntilFocusedMarker(
    marker: String,
    timeoutMs: Long,
): Boolean {
    val deadline = SystemClock.uptimeMillis() + timeoutMs
    val markerFocused = By.desc(marker).focused(true)
    val markerUnderFocusedAncestor = By.desc(marker).hasAncestor(By.focused(true))
    while (SystemClock.uptimeMillis() < deadline) {
        if (hasObject(markerFocused) || hasObject(markerUnderFocusedAncestor)) {
            return true
        }
        SystemClock.sleep(FOCUS_POLL_INTERVAL_MS)
    }
    return hasObject(markerFocused) || hasObject(markerUnderFocusedAncestor)
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
