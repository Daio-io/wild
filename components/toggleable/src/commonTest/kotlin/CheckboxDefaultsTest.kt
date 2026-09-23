// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.style.Border
import io.daio.wild.style.StyleDefaults
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@OptIn(ExperimentalTestApi::class)
class CheckboxDefaultsTest {
    @Test
    fun defaultStyleMatchesStyleDefaultsNone() {
        assertSame(StyleDefaults.None, CheckboxDefaults.style())
    }

    @Test
    fun indicatorSizeIs20dp() {
        assertEquals(20.0, CheckboxDefaults.indicatorSize.value.toDouble())
    }

    @Test
    fun checkedIndicatorComposesAtDefaultSize() =
        runComposeUiTest {
            setContent {
                CheckboxDefaults.Indicator(
                    checked = true,
                    modifier = Modifier.testTag("indicator"),
                )
            }

            onNodeWithTag("indicator").assertIsDisplayed()
        }

    @Test
    fun indeterminateIndicatorComposesAtDefaultSize() =
        runComposeUiTest {
            setContent {
                CheckboxDefaults.Indicator(
                    state = ToggleableState.Indeterminate,
                    modifier = Modifier.testTag("indicator"),
                )
            }

            onNodeWithTag("indicator").assertIsDisplayed()
        }

    @Test
    fun customParametersAreForwarded() {
        val border = Border(width = 2.dp, color = Color.Blue)
        val style =
            CheckboxDefaults.style(
                colors = StyleDefaults.colors(backgroundColor = Color.Red),
                borders = StyleDefaults.borders(border = border),
                scale = StyleDefaults.scale(focusedScale = 1.2f),
                shapes = StyleDefaults.shapes(shape = RectangleShape),
                alpha = StyleDefaults.alpha(disabledAlpha = 0.3f),
            )

        assertEquals(Color.Red, style.colors.backgroundColor)
        assertEquals(border, style.borders.border)
        assertEquals(1.2f, style.scale.focusedScale)
        assertEquals(RectangleShape, style.shapes.shape)
        assertEquals(0.3f, style.alpha.disabledAlpha)
    }
}
