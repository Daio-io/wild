// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.Border
import io.daio.wild.style.StyleDefaults
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Verifies that [StyleResolver.Value] resolves every [StyleScope] property to the same value
 * that calling the corresponding [io.daio.wild.style.Style] helper function directly would
 * produce, for a representative set of interaction/component state combinations.
 */
@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class StyleValueResolutionTest {
    private val style =
        StyleDefaults.style(
            colors =
                StyleDefaults.colors(
                    backgroundColor = Color.Black,
                    focusedBackgroundColor = Color.Blue,
                    hoveredBackgroundColor = Color.Cyan,
                    pressedBackgroundColor = Color.Red,
                    disabledBackgroundColor = Color.Gray,
                    selectedBackgroundColor = Color.Yellow,
                    focusedSelectedBackgroundColor = Color.Magenta,
                ),
            scale =
                StyleDefaults.scale(
                    scale = 1f,
                    focusedScale = 1.1f,
                    hoveredScale = 1.2f,
                    pressedScale = 1.3f,
                    selectedScale = 1.4f,
                    disabledScale = 0.9f,
                    focusedSelectedScale = 1.5f,
                    animationSpec = spring(),
                ),
            alpha =
                StyleDefaults.alpha(
                    alpha = 1f,
                    focusedAlpha = 0.9f,
                    hoveredAlpha = 0.8f,
                    pressedAlpha = 0.7f,
                    selectedAlpha = 0.6f,
                    disabledAlpha = 0.5f,
                    focusedSelectedAlpha = 0.4f,
                ),
            shapes =
                StyleDefaults.shapes(
                    shape = RectangleShape,
                    focusedShape = CircleShape,
                ),
            borders =
                StyleDefaults.borders(
                    border = Border(width = 1.dp, color = Color.Black),
                    focusedBorder = Border(width = 2.dp, color = Color.Blue),
                ),
        )

    @Test
    fun defaultStateResolvesFromStyleHelpers() = assertResolvedStyle()

    @Test
    fun focusedStateResolvesFromStyleHelpers() = assertResolvedStyle(focused = true)

    @Test
    fun disabledStateResolvesFromStyleHelpers() = assertResolvedStyle(enabled = false)

    @Test
    fun selectedStateResolvesFromStyleHelpers() = assertResolvedStyle(selected = true)

    @Test
    fun pressedStateResolvesFromStyleHelpers() = assertResolvedStyle(pressed = true)

    @Test
    fun hoveredStateResolvesFromStyleHelpers() = assertResolvedStyle(hovered = true)

    @Test
    fun focusedAndSelectedStateResolvesFromStyleHelpers() = assertResolvedStyle(focused = true, selected = true)

    private fun assertResolvedStyle(
        enabled: Boolean = true,
        selected: Boolean = false,
        focused: Boolean = false,
        hovered: Boolean = false,
        pressed: Boolean = false,
    ) = runComposeUiTest {
        val recorder = StyleRecorder()
        val source = MutableInteractionSource()

        setContent {
            Box(
                Modifier
                    .size(1.dp)
                    .interactionSourceNode(
                        interactionSource = source,
                        childTraversalKey = StyleParentTraversalKey,
                    )
                    .then(
                        StyleScopeParentElement(
                            enabled = enabled,
                            selected = selected,
                            resolver = StyleResolver.Value(style),
                        ),
                    )
                    .then(ScaleLayoutElement())
                    .then(BorderElement())
                    .then(BackgroundElement())
                    .then(ShapeLayoutElement())
                    .recordStyle(recorder),
            )
        }
        waitForIdle()

        if (focused || hovered || pressed) {
            runOnIdle {
                if (focused) assertTrue(source.tryEmit(FocusInteraction.Focus()))
                if (hovered) assertTrue(source.tryEmit(HoverInteraction.Enter()))
                if (pressed) assertTrue(source.tryEmit(PressInteraction.Press(Offset.Zero)))
            }
            waitForIdle()
        }

        assertEquals(
            style.colors.colorFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            ),
            recorder.last.color,
        )
        assertEquals(
            style.scale.scaleFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            ),
            recorder.last.scale,
        )
        assertEquals(
            style.alpha.alphaFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            ),
            recorder.last.alpha,
        )
        assertEquals(
            style.shapes.shapeFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            ),
            recorder.last.shape,
        )
        assertEquals(
            style.borders.borderFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            ),
            recorder.last.border,
        )
        assertEquals(style.scale.animationSpec, recorder.last.scaleAnimationSpec)
    }
}
