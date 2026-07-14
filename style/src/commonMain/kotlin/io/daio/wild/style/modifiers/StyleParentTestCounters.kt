// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

/**
 * Test-only instrumentation for [StyleScopeParentNode] resolve/update calls.
 *
 * Not part of the public API. Lives in `commonMain` (rather than test sources) purely so
 * [StyleScopeParentNode] can increment it directly; production callers must not read or depend
 * on these counts.
 */
internal object StyleParentTestCounters {
    var resolveCount: Int = 0
    var updateCount: Int = 0

    fun reset() {
        resolveCount = 0
        updateCount = 0
    }
}
