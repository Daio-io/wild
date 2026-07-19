// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.android.tv

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TvBenchmarkScenarioTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun explicitAndNullSourceVariantsRouteThroughDistinctSourceStrategies() {
        val fastPath = benchmarkStyleVariant("explicit_source_fast_path")
        val compatibilityPath = benchmarkStyleVariant("null_source_compatibility")

        assertEquals(BenchmarkItemImplementation.StyledClickable, fastPath.implementation)
        assertEquals(BenchmarkItemImplementation.StyledClickable, compatibilityPath.implementation)
        assertEquals(fastPath.benchmarkTitle, compatibilityPath.benchmarkTitle)
        assertEquals(BenchmarkInteractionSourceStrategy.Explicit, fastPath.interactionSourceStrategy)
        assertEquals(
            BenchmarkInteractionSourceStrategy.NullCompatibility,
            compatibilityPath.interactionSourceStrategy,
        )
        assertNotEquals(fastPath.interactionSourceStrategy, compatibilityPath.interactionSourceStrategy)
    }

    @Test
    fun existingBenchmarkVariantsRemainAvailable() {
        assertEquals(
            BenchmarkItemImplementation.StyledClickable,
            benchmarkStyleVariant("current_traversal").implementation,
        )
        assertEquals(
            BenchmarkInteractionSourceStrategy.Explicit,
            benchmarkStyleVariant("current_traversal").interactionSourceStrategy,
        )
        assertEquals(
            BenchmarkItemImplementation.CandidateComposite,
            benchmarkStyleVariant("candidate_composite").implementation,
        )
        assertEquals(
            BenchmarkItemImplementation.MaterialSurface,
            benchmarkStyleVariant("material_surface").implementation,
        )
    }

    @Test
    fun recompositionDriverPublishesMarkerOnlyAfterApplication() {
        val driver = BenchmarkRecompositionDriver()

        assertEquals(0, driver.generation)
        assertEquals("benchmark-recomposition-0", driver.marker)

        assertEquals(1, driver.advance())
        assertEquals(1, driver.generation)
        assertEquals("benchmark-recomposition-0", driver.marker)

        driver.acknowledgeApplied(1)

        assertEquals("benchmark-recomposition-1", driver.marker)
    }

    @Test
    fun realSourcePathItemsRecomposeWithStableInputsAndDistinctModifierRoutes() {
        val explicitObserver = BenchmarkItemRuntimeObserver()
        val compatibilityObserver = BenchmarkItemRuntimeObserver()
        val explicitDriver = BenchmarkRecompositionDriver(explicitObserver)
        val compatibilityDriver = BenchmarkRecompositionDriver(compatibilityObserver)
        val explicitMarkersDuringComposition = mutableListOf<Pair<Int, String>>()
        val compatibilityMarkersDuringComposition = mutableListOf<Pair<Int, String>>()

        composeRule.setContent {
            val explicitGeneration = explicitDriver.generation
            val compatibilityGeneration = compatibilityDriver.generation
            val explicitMarkerDuringComposition = explicitDriver.marker
            val compatibilityMarkerDuringComposition = compatibilityDriver.marker
            Box {
                BenchmarkSourcePathItem(
                    variant = benchmarkStyleVariant("explicit_source_fast_path"),
                    recompositionDriver = explicitDriver,
                )
                BenchmarkSourcePathItem(
                    variant = benchmarkStyleVariant("null_source_compatibility"),
                    recompositionDriver = compatibilityDriver,
                )
            }
            SideEffect {
                explicitMarkersDuringComposition +=
                    explicitGeneration to explicitMarkerDuringComposition
                compatibilityMarkersDuringComposition +=
                    compatibilityGeneration to compatibilityMarkerDuringComposition
            }
        }

        composeRule.runOnIdle {
            assertSourcePathInitialApplication(
                observer = explicitObserver,
                expectsComposedModifier = false,
                expectsExplicitSource = true,
            )
            assertSourcePathInitialApplication(
                observer = compatibilityObserver,
                expectsComposedModifier = true,
                expectsExplicitSource = false,
            )

            explicitDriver.advance()
            compatibilityDriver.advance()
        }

        composeRule.runOnIdle {
            assertSourcePathRecomposition(
                observer = explicitObserver,
                driver = explicitDriver,
                markersDuringComposition = explicitMarkersDuringComposition,
            )
            assertSourcePathRecomposition(
                observer = compatibilityObserver,
                driver = compatibilityDriver,
                markersDuringComposition = compatibilityMarkersDuringComposition,
            )
        }
    }
}

private fun assertSourcePathInitialApplication(
    observer: BenchmarkItemRuntimeObserver,
    expectsComposedModifier: Boolean,
    expectsExplicitSource: Boolean,
) {
    assertEquals(1, observer.appliedCompositions.size)
    assertEquals(
        expectsComposedModifier,
        observer.appliedCompositions.single().clickableModifier.hasComposedElement(),
    )
    if (expectsExplicitSource) {
        assertTrue(observer.appliedCompositions.single().interactionSource != null)
    } else {
        assertNull(observer.appliedCompositions.single().interactionSource)
    }
}

private fun assertSourcePathRecomposition(
    observer: BenchmarkItemRuntimeObserver,
    driver: BenchmarkRecompositionDriver,
    markersDuringComposition: List<Pair<Int, String>>,
) {
    assertEquals(listOf(0, 1), observer.appliedCompositions.map { it.generation })

    val initial = observer.appliedCompositions.first()
    val recomposed = observer.appliedCompositions.last()
    assertSame(initial.receiverModifier, recomposed.receiverModifier)
    assertSame(initial.onClick, recomposed.onClick)
    assertSame(initial.style, recomposed.style)
    assertSame(initial.interactionSource, recomposed.interactionSource)
    assertEquals(initial.interactionSourceStrategy, recomposed.interactionSourceStrategy)
    assertTrue(1 to "benchmark-recomposition-0" in markersDuringComposition)
    assertEquals("benchmark-recomposition-0", recomposed.markerBefore)
    assertEquals("benchmark-recomposition-1", recomposed.markerAfter)
    assertEquals("benchmark-recomposition-1", driver.marker)
}

private fun Modifier.hasComposedElement(): Boolean =
    foldIn(false) { found, element ->
        found || element::class.simpleName?.contains("ComposedModifier") == true
    }
