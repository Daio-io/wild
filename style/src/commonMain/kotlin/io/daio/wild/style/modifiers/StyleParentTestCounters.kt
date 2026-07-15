// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

/**
 * Test-only instrumentation for style parent resolve/update counts.
 * Disabled by default so production builds do not mutate counters.
 */
internal object StyleParentTestCounters {
    var enabled: Boolean = false
    var resolveCount: Int = 0
    var updateCount: Int = 0

    fun reset() {
        enabled = true
        resolveCount = 0
        updateCount = 0
    }

    fun onUpdate() {
        if (enabled) updateCount++
    }

    fun onResolve() {
        if (enabled) resolveCount++
    }
}
