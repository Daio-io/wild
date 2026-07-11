// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.android.tv

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Surface
import io.daio.wild.container.Container
import io.daio.wild.content.LocalContentColor
import io.daio.wild.style.Border
import io.daio.wild.style.Style
import io.daio.wild.style.StyleDefaults
import io.daio.wild.style.clickable
import androidx.tv.material3.LocalContentColor as TvLocalContentColor

private const val GRID_ROWS = 20
private const val GRID_COLUMNS = 100
private const val LIST_ITEMS = 100

private enum class StyleVariant(val extraValue: String) {
    CurrentTraversal("current_traversal"),
    CandidateComposite("candidate_composite"),
    MaterialSurface("material_surface"),
    Container("container"),
}

private data class BenchmarkItemConfig(
    val itemSize: Dp = 100.dp,
    val focusedScale: Float = 1.2f,
    val shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    val backgroundColor: Color = Color.Black,
    val contentColor: Color = Color.White,
    val focusedBackgroundColor: Color = Color.Red,
    val focusedContentColor: Color = Color.Black,
    val pressedBackgroundColor: Color = Color.Black.copy(alpha = .6f),
    val disabledBackgroundColor: Color = Color.Black.copy(alpha = .3f),
    val borderColor: Color = Color.Red,
    val borderWidth: Dp = 2.dp,
    val borderInset: Dp = 2.dp,
)

private val BenchmarkItemConfig.style: Style
    get() =
        StyleDefaults.style(
            colors =
                StyleDefaults.colors(
                    backgroundColor = backgroundColor,
                    contentColor = contentColor,
                    focusedBackgroundColor = focusedBackgroundColor,
                    focusedContentColor = focusedContentColor,
                    pressedBackgroundColor = pressedBackgroundColor,
                    disabledBackgroundColor = disabledBackgroundColor,
                ),
            borders =
                StyleDefaults.borders(
                    border =
                        Border(
                            width = borderWidth,
                            inset = borderInset,
                            color = borderColor,
                        ),
                ),
            scale = StyleDefaults.scale(focusedScale = focusedScale),
            shapes = StyleDefaults.shapes(shape),
        )

@Composable
fun TvLayout(
    modifier: Modifier = Modifier,
    mode: String = "list",
    itemsType: String = StyleVariant.Container.extraValue,
) {
    val variant = StyleVariant.entries.firstOrNull { it.extraValue == itemsType } ?: StyleVariant.Container

    when (mode) {
        "list" -> OptionsList(variant, modifier)
        "grid" -> OptionsGrid(variant, modifier)
    }
}

@Composable
private fun OptionsGrid(
    variant: StyleVariant,
    modifier: Modifier = Modifier,
) {
    val config = remember { BenchmarkItemConfig() }
    val style = remember(config) { config.style }

    LazyColumn(
        modifier =
            modifier
                .background(Color.Black)
                .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(GRID_ROWS, key = { it }) { row ->
            LazyRow {
                items(GRID_COLUMNS, key = { it }) { column ->
                    BenchmarkItem(
                        variant = variant,
                        title = variant.extraValue,
                        style = style,
                        config = config,
                        marker = "benchmark-item-$row-$column",
                        onClick = { },
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionsList(
    variant: StyleVariant,
    modifier: Modifier = Modifier,
) {
    val config = remember { BenchmarkItemConfig() }
    val style = remember(config) { config.style }

    LazyColumn(
        modifier =
            modifier
                .background(Color.Black)
                .fillMaxSize(),
        verticalArrangement =
            Arrangement.spacedBy(
                16.dp,
                alignment = Alignment.CenterVertically,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(LIST_ITEMS, key = { it }) { index ->
            BenchmarkItem(
                variant = variant,
                title = variant.extraValue,
                style = style,
                config = config,
                marker = "benchmark-item-0-$index",
                onClick = { },
            )
        }
    }
}

@Composable
private fun BenchmarkItem(
    variant: StyleVariant,
    title: String,
    style: Style,
    config: BenchmarkItemConfig,
    marker: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemModifier = modifier.benchmarkItemMarker(marker).size(config.itemSize)

    when (variant) {
        StyleVariant.CurrentTraversal -> CurrentTraversalItem(title, onClick, style, itemModifier)
        StyleVariant.CandidateComposite -> CandidateCompositeItem(title, onClick, style, itemModifier)
        StyleVariant.MaterialSurface -> MaterialSurfaceItem(title, onClick, config, itemModifier)
        StyleVariant.Container -> CandidateCompositeItem(title, onClick, style, itemModifier)
    }
}

@Composable
private fun CurrentTraversalItem(
    title: String,
    onClick: () -> Unit,
    style: Style,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier =
            modifier.clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                style = style,
            ),
    ) {
        BenchmarkItemText(title)
    }
}

@Composable
private fun CandidateCompositeItem(
    title: String,
    onClick: () -> Unit,
    style: Style,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Container(
        style = style,
        modifier = modifier,
        interactionSource = interactionSource,
        onClick = onClick,
    ) {
        BenchmarkItemText(title)
    }
}

@Composable
private fun MaterialSurfaceItem(
    title: String,
    onClick: () -> Unit,
    config: BenchmarkItemConfig,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier,
        interactionSource = interactionSource,
        onClick = onClick,
        colors =
            ClickableSurfaceDefaults.colors(
                containerColor = config.backgroundColor,
                contentColor = config.contentColor,
                focusedContainerColor = config.focusedBackgroundColor,
                focusedContentColor = config.focusedContentColor,
                pressedContainerColor = config.pressedBackgroundColor,
                disabledContainerColor = config.disabledBackgroundColor,
            ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = config.focusedScale),
        border =
            ClickableSurfaceDefaults.border(
                border =
                    androidx.tv.material3.Border(
                        border = BorderStroke(width = config.borderWidth, config.borderColor),
                        inset = config.borderInset,
                    ),
            ),
        shape = ClickableSurfaceDefaults.shape(config.shape),
    ) {
        MaterialSurfaceItemText(title)
    }
}

@Composable
private fun BenchmarkItemText(title: String) {
    val color = LocalContentColor.current
    BasicText(text = title, color = { color })
}

@Composable
private fun MaterialSurfaceItemText(title: String) {
    val color = TvLocalContentColor.current
    BasicText(text = title, color = { color })
}

private fun Modifier.benchmarkItemMarker(marker: String): Modifier = semantics { contentDescription = marker }
