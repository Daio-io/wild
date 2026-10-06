// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.content

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Consumer-local coverage for the equality-gated content-color bridge: when the published color
 * changes, Wild, Material, Material3, and the platform alternate locals all update together.
 */
@OptIn(ExperimentalTestApi::class)
class ContentColorBridgeConsumerTest {
    @Test
    fun wildMaterialMaterial3Alternate_updateWhenColorChanges() =
        runComposeUiTest {
            var published by mutableStateOf(Color.Red)
            var wild = Color.Unspecified
            var material = Color.Unspecified
            var material3 = Color.Unspecified
            var alternate = Color.Unspecified

            setContent {
                ProvidesContentColor(published) {
                    wild = LocalContentColor.current
                    material = materialContentColorLocal.current
                    material3 = material3ContentColorLocal.current
                    alternate = LocalAlternatePlatformColor.current
                }
            }

            runOnIdle {
                assertEquals(Color.Red, wild)
                assertEquals(Color.Red, material)
                assertEquals(Color.Red, material3)
                assertEquals(Color.Red, alternate)
            }

            runOnIdle { published = Color.Blue }
            waitForIdle()

            runOnIdle {
                assertEquals(Color.Blue, wild)
                assertEquals(Color.Blue, material)
                assertEquals(Color.Blue, material3)
                assertEquals(Color.Blue, alternate)
            }
        }

    @Test
    fun equalPublishedColor_doesNotRequireDistinctLocalIdentity() =
        runComposeUiTest {
            var published by mutableStateOf(Color.Green)
            var observations = 0
            var observed = Color.Unspecified

            setContent {
                ProvidesContentColor(published) {
                    observations++
                    observed = LocalContentColor.current
                }
            }

            waitForIdle()
            val afterInitial = observations

            runOnIdle { published = Color.Green }
            waitForIdle()

            // Same color value: Compose may still recompose the provider caller, but the
            // observed local must remain Green (bridge contract for consumers).
            runOnIdle {
                assertEquals(Color.Green, observed)
                assertEquals(afterInitial, observations)
            }
        }
}
