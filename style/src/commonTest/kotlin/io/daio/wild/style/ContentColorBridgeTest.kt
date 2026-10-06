// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class ContentColorBridgeTest {
    @Test
    fun replacingPublisher_republishesCurrentResolvedColor() =
        runComposeUiTest {
            var generation by mutableIntStateOf(0)
            val first = RecordingPublisher()
            val second = RecordingPublisher()
            val style =
                StyleDefaults.style(
                    colors =
                        StyleDefaults.colors(
                            backgroundColor = Color.Black,
                            contentColor = Color.Red,
                        ),
                )

            setContent {
                val publisher = if (generation == 0) first else second
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null, style = style)
                        .contentColorBridge(publisher),
                )
            }

            waitForIdle()
            runOnIdle {
                assertEquals(Color.Red, first.published.last())
                assertEquals(emptyList(), second.published)
            }

            runOnIdle { generation = 1 }
            waitForIdle()

            // Replacement sink must receive the already-resolved color without waiting for a
            // later interaction or style change (ContentColorBridgeElement.update path).
            runOnIdle { assertEquals(Color.Red, second.published.last()) }
        }

    private class RecordingPublisher : ContentColorPublisher {
        val published = mutableListOf<Color>()

        override fun publishResolvedColor(color: Color) {
            published += color
        }
    }
}
