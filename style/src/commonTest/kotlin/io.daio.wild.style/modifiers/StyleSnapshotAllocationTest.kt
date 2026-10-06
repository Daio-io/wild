// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.StyleScope
import io.daio.wild.style.interactionStyle
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class StyleSnapshotAllocationTest {
    @AfterTest
    fun clearAllocationHook() {
        styleScopeSnapshotAllocationHook = null
    }

    @Test
    fun equalOutputResolve_skipsDispatchAndSnapshotAlloc() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            val work = WorkCounters()
            val styleKeyState = mutableStateOf(0)
            val stableBlock: StyleScope.() -> Unit = {
                color =
                    when (styleKeyState.value) {
                        0,
                        1,
                        -> Color.Red
                        else -> Color.Blue
                    }
            }

            styleScopeSnapshotAllocationHook = { work.snapshotAllocations++ }
            try {
                setContent {
                    Box(
                        Modifier
                            .size(1.dp)
                            .interactionStyle(interactionSource = null, block = stableBlock)
                            .recordStyle(recorder),
                    )
                }
                waitForIdle()
                assertEquals(Color.Red, recorder.last.color)
                val chromeBaseline = recorder.snapshots.size
                val allocationsAfterAttach = work.snapshotAllocations
                assertTrue(allocationsAfterAttach > 0)

                // Equal effective style: resolve again without changing outputs.
                runOnIdle { styleKeyState.value = 1 }
                waitForIdle()

                assertEquals(chromeBaseline, recorder.snapshots.size)
                assertEquals(Color.Red, recorder.last.color)
                assertEquals(
                    allocationsAfterAttach,
                    work.snapshotAllocations,
                    "equal-output resolve must not allocate StyleScopeSnapshot",
                )
            } finally {
                styleScopeSnapshotAllocationHook = null
            }
        }
}
