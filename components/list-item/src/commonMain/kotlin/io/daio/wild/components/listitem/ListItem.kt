// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.listitem

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.daio.wild.container.Container
import io.daio.wild.style.Alpha
import io.daio.wild.style.Borders
import io.daio.wild.style.Colors
import io.daio.wild.style.Scale
import io.daio.wild.style.Shapes
import io.daio.wild.style.Style
import io.daio.wild.style.StyleDefaults

/**
 * A list item with content only — no leading or trailing slots.
 *
 * @param onClick Callback invoked when the list item is clicked.
 * @param modifier Modifier to apply to the list item.
 * @param enabled Whether the list item is enabled.
 * @param selected Whether the list item is currently selected.
 * @param style The [Style] for interaction states.
 * @param contentPadding Padding applied inside the list item.
 * @param verticalAlignment Vertical alignment of the row content.
 * @param horizontalArrangement Horizontal arrangement of the row content.
 * @param interactionSource Optional [MutableInteractionSource] for observing [Interaction]s.
 * @param content Main content of the list item.
 *
 * @since 0.6.0
 */
@Composable
fun ListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    style: Style = ListItemDefaults.style(),
    contentPadding: PaddingValues = ListItemDefaults.contentPadding,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalArrangement: Arrangement.Horizontal =
        Arrangement.spacedBy(ListItemDefaults.contentSpacing),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    Container(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = ListItemDefaults.defaultMinHeight),
        enabled = enabled,
        selected = selected,
        style = style,
        interactionSource = interactionSource,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
            verticalAlignment = verticalAlignment,
            horizontalArrangement = horizontalArrangement,
        ) {
            content()
        }
    }
}

/**
 * A list item with leading, content, and trailing slots.
 *
 * @param onClick Callback invoked when the list item is clicked.
 * @param leadingContent Content displayed at the start.
 * @param trailingContent Content displayed at the end.
 * @param modifier Modifier to apply to the list item.
 * @param enabled Whether the list item is enabled.
 * @param selected Whether the list item is currently selected.
 * @param style The [Style] for interaction states.
 * @param contentPadding Padding applied inside the list item.
 * @param verticalAlignment Vertical alignment of the row content.
 * @param horizontalArrangement Horizontal arrangement of the row content.
 * @param interactionSource Optional [MutableInteractionSource] for observing [Interaction]s.
 * @param content Main content of the list item.
 *
 * @since 0.6.0
 */
@Composable
fun ListItem(
    onClick: () -> Unit,
    leadingContent: (@Composable () -> Unit)?,
    trailingContent: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    style: Style = ListItemDefaults.style(),
    contentPadding: PaddingValues = ListItemDefaults.contentPadding,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalArrangement: Arrangement.Horizontal =
        Arrangement.spacedBy(ListItemDefaults.contentSpacing),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    Container(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = ListItemDefaults.defaultMinHeight),
        enabled = enabled,
        selected = selected,
        style = style,
        interactionSource = interactionSource,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
            verticalAlignment = verticalAlignment,
            horizontalArrangement = horizontalArrangement,
        ) {
            if (leadingContent != null) {
                leadingContent()
            }
            Box(modifier = Modifier.weight(1f)) {
                content()
            }
            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}

/**
 * Contains the default values used by [ListItem].
 *
 * @since 0.6.0
 */
object ListItemDefaults {
    /**
     * The default minimum height of the list item.
     */
    val defaultMinHeight: Dp = 48.dp

    /**
     * The default shape for list items.
     */
    val shape: Shape = RectangleShape

    /**
     * The default content padding.
     */
    val contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

    /**
     * The default spacing between leading, content, and trailing slots.
     */
    val contentSpacing: Dp = 16.dp

    /**
     * Creates a default [Style] for list items.
     */
    fun style(
        colors: Colors = StyleDefaults.colors(),
        borders: Borders = StyleDefaults.borders(),
        scale: Scale = StyleDefaults.scale(),
        shapes: Shapes = StyleDefaults.shapes(),
        alpha: Alpha = StyleDefaults.alpha(),
    ): Style =
        StyleDefaults.style(
            colors = colors,
            borders = borders,
            scale = scale,
            shapes = shapes,
            alpha = alpha,
        )
}
