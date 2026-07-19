// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.android.tv

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class TvBenchmarkScenarioTest {
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
}
