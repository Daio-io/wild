// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.benchmark

import android.view.KeyEvent
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val DEFAULT_ITERATIONS = 20
private const val APP_PACKAGE = "io.daio.wild.playbook.tv"

@RunWith(AndroidJUnit4::class)
class TvBenchmarkTest {
    @get:Rule
    val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    /**
     * Using a Modifier that sets up and adjusts styles using a Modifier.composed solution.
     */
    @Test
    fun scrollGridWithWildComposedModifier() {
        benchmarkRule.measureRepeated(
            packageName = APP_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            startupMode = StartupMode.WARM,
            iterations = DEFAULT_ITERATIONS,
            setupBlock = {
                startActivityAndWait {
                    it.putExtra("MODE", "grid")
                    it.putExtra("ITEMS", "modifier")
                }
            },
        ) {
            device.scrollWithRemote()
        }
    }

    /**
     * Using a Modifier that sets up and adjusts styles using custom Node implementations.
     */
    @Test
    fun scrollGridWithWildExperimentalModifier() {
        benchmarkRule.measureRepeated(
            packageName = APP_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            startupMode = StartupMode.WARM,
            iterations = DEFAULT_ITERATIONS,
            setupBlock = {
                startActivityAndWait {
                    it.putExtra("MODE", "grid")
                    it.putExtra("ITEMS", "experimental_modifier")
                }
            },
        ) {
            device.scrollWithRemote()
        }
    }

    /**
     * Uses Tv Material Surface.
     */
    @Test
    fun scrollGridWithMaterialSurface() {
        benchmarkRule.measureRepeated(
            packageName = APP_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            startupMode = StartupMode.WARM,
            iterations = DEFAULT_ITERATIONS,
            setupBlock = {
                startActivityAndWait {
                    it.putExtra("MODE", "grid")
                    it.putExtra("ITEMS", "surface")
                }
            },
        ) {
            device.scrollWithRemote()
        }
    }

    /**
     * Uses Wild Container to setup styles.
     */
    @Test
    fun scrollGridWildContainer() {
        benchmarkRule.measureRepeated(
            packageName = APP_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            startupMode = StartupMode.WARM,
            iterations = DEFAULT_ITERATIONS,
            setupBlock = {
                startActivityAndWait {
                    it.putExtra("MODE", "grid")
                    it.putExtra("ITEMS", "container")
                }
            },
        ) {
            device.scrollWithRemote()
        }
    }
}

fun UiDevice.scrollWithRemote() {
    runBlocking {
        repeat(15) {
            pressKeyCode(KeyEvent.KEYCODE_DPAD_RIGHT)
        }
        repeat(10) {
            pressKeyCode(KeyEvent.KEYCODE_DPAD_DOWN)
        }
        repeat(15) {
            pressKeyCode(KeyEvent.KEYCODE_DPAD_RIGHT)
        }
        repeat(10) {
            pressKeyCode(KeyEvent.KEYCODE_DPAD_LEFT)
        }
        repeat(5) {
            pressKeyCode(KeyEvent.KEYCODE_DPAD_UP)
        }
    }
}
