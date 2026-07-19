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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
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
private const val SOURCE_PATH_BENCHMARK_TITLE = "styled_clickable_source_path"

internal enum class BenchmarkItemImplementation {
    StyledClickable,
    CandidateComposite,
    MaterialSurface,
}

internal enum class BenchmarkInteractionSourceStrategy {
    Explicit,
    NullCompatibility,
}

internal enum class StyleVariant(
    val extraValue: String,
    val implementation: BenchmarkItemImplementation,
    val interactionSourceStrategy: BenchmarkInteractionSourceStrategy? = null,
    val benchmarkTitle: String = extraValue,
) {
    CurrentTraversal(
        "current_traversal",
        BenchmarkItemImplementation.StyledClickable,
        BenchmarkInteractionSourceStrategy.Explicit,
    ),
    ExplicitSourceFastPath(
        "explicit_source_fast_path",
        BenchmarkItemImplementation.StyledClickable,
        BenchmarkInteractionSourceStrategy.Explicit,
        SOURCE_PATH_BENCHMARK_TITLE,
    ),
    NullSourceCompatibility(
        "null_source_compatibility",
        BenchmarkItemImplementation.StyledClickable,
        BenchmarkInteractionSourceStrategy.NullCompatibility,
        SOURCE_PATH_BENCHMARK_TITLE,
    ),
    CandidateComposite("candidate_composite", BenchmarkItemImplementation.CandidateComposite),
    MaterialSurface("material_surface", BenchmarkItemImplementation.MaterialSurface),
    Container("container", BenchmarkItemImplementation.CandidateComposite),
}

internal fun benchmarkStyleVariant(extraValue: String): StyleVariant =
    StyleVariant.entries.firstOrNull { it.extraValue == extraValue } ?: StyleVariant.Container

@Stable
internal class BenchmarkRecompositionDriver(
    internal val runtimeObserver: BenchmarkItemRuntimeObserver? = null,
) {
    private val generationState = mutableIntStateOf(0)
    private val appliedGenerationState = mutableIntStateOf(0)

    val generation: Int
        get() = generationState.intValue

    val marker: String
        get() = "benchmark-recomposition-${appliedGenerationState.intValue}"

    fun advance(): Int = ++generationState.intValue

    fun acknowledgeApplied(generation: Int) {
        if (generation > appliedGenerationState.intValue) {
            appliedGenerationState.intValue = generation
        }
    }
}

internal data class BenchmarkItemAppliedComposition(
    val generation: Int,
    val receiverModifier: Modifier,
    val onClick: () -> Unit,
    val style: Style,
    val interactionSourceStrategy: BenchmarkInteractionSourceStrategy,
    val interactionSource: MutableInteractionSource?,
    val clickableModifier: Modifier,
    val markerBefore: String,
    val markerAfter: String,
)

@Stable
internal class BenchmarkItemRuntimeObserver {
    private val mutableAppliedCompositions = mutableListOf<BenchmarkItemAppliedComposition>()

    val appliedCompositions: List<BenchmarkItemAppliedComposition>
        get() = mutableAppliedCompositions

    internal fun recordApplied(composition: BenchmarkItemAppliedComposition) {
        mutableAppliedCompositions += composition
    }
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
    enableRecompositionDriver: Boolean = false,
) {
    val variant = benchmarkStyleVariant(itemsType)

    if (enableRecompositionDriver) {
        RecompositionDrivenLayout(variant, mode, modifier)
    } else {
        BenchmarkLayout(variant, mode, modifier)
    }
}

@Composable
private fun RecompositionDrivenLayout(
    variant: StyleVariant,
    mode: String,
    modifier: Modifier,
) {
    val driver = remember { BenchmarkRecompositionDriver() }
    val drivenModifier =
        modifier
            .onPreviewKeyEvent { event ->
                if (event.key != Key.R) {
                    false
                } else {
                    if (event.type == KeyEventType.KeyUp) {
                        driver.advance()
                    }
                    true
                }
            }.semantics { contentDescription = driver.marker }

    BenchmarkLayout(variant, mode, drivenModifier, driver)
}

@Composable
private fun BenchmarkLayout(
    variant: StyleVariant,
    mode: String,
    modifier: Modifier,
    recompositionDriver: BenchmarkRecompositionDriver? = null,
) {
    when (mode) {
        "list" -> OptionsList(variant, modifier, recompositionDriver)
        "grid" -> OptionsGrid(variant, modifier, recompositionDriver)
    }
}

@Composable
internal fun BenchmarkSourcePathItem(
    variant: StyleVariant,
    recompositionDriver: BenchmarkRecompositionDriver,
    modifier: Modifier = Modifier,
) {
    require(variant.implementation == BenchmarkItemImplementation.StyledClickable)
    val config = remember { BenchmarkItemConfig() }
    val style = remember(config) { config.style }
    val onClick = remember { {} }

    BenchmarkItem(
        variant = variant,
        title = variant.benchmarkTitle,
        style = style,
        config = config,
        marker = "benchmark-source-path-probe",
        recompositionDriver = recompositionDriver,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun OptionsGrid(
    variant: StyleVariant,
    modifier: Modifier = Modifier,
    recompositionDriver: BenchmarkRecompositionDriver? = null,
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
                        title = variant.benchmarkTitle,
                        style = style,
                        config = config,
                        marker = "benchmark-item-$row-$column",
                        recompositionDriver = recompositionDriver,
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
    recompositionDriver: BenchmarkRecompositionDriver? = null,
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
                title = variant.benchmarkTitle,
                style = style,
                config = config,
                marker = "benchmark-item-0-$index",
                recompositionDriver = recompositionDriver,
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
    recompositionDriver: BenchmarkRecompositionDriver?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemModifier = modifier.benchmarkItemMarker(marker).size(config.itemSize)

    when (variant.implementation) {
        BenchmarkItemImplementation.StyledClickable ->
            StyledClickableItem(
                title = title,
                onClick = onClick,
                style = style,
                interactionSourceStrategy = checkNotNull(variant.interactionSourceStrategy),
                recompositionDriver = recompositionDriver,
                modifier = itemModifier,
            )
        BenchmarkItemImplementation.CandidateComposite ->
            CandidateCompositeItem(title, onClick, style, itemModifier)
        BenchmarkItemImplementation.MaterialSurface ->
            MaterialSurfaceItem(title, onClick, config, itemModifier)
    }
}

@Composable
private fun StyledClickableItem(
    title: String,
    onClick: () -> Unit,
    style: Style,
    interactionSourceStrategy: BenchmarkInteractionSourceStrategy,
    recompositionDriver: BenchmarkRecompositionDriver?,
    modifier: Modifier = Modifier,
) {
    // The stable driver parameter does not change. Reading its snapshot state invalidates this
    // scope directly while every clickable argument remains unchanged.
    val recompositionGeneration = recompositionDriver?.generation

    val interactionSource =
        when (interactionSourceStrategy) {
            BenchmarkInteractionSourceStrategy.Explicit -> remember { MutableInteractionSource() }
            BenchmarkInteractionSourceStrategy.NullCompatibility -> null
        }
    val clickableModifier =
        modifier.clickable(
            onClick = onClick,
            interactionSource = interactionSource,
            style = style,
        )
    val runtimeObserver = recompositionDriver?.runtimeObserver

    if (recompositionDriver != null && recompositionGeneration != null) {
        if (runtimeObserver == null) {
            SideEffect { recompositionDriver.acknowledgeApplied(recompositionGeneration) }
        } else {
            // Observer-only values stay out of the measured null-observer path. The detailed record
            // is published after this successful test-probe composition has been applied.
            SideEffect {
                val markerBefore = recompositionDriver.marker
                recompositionDriver.acknowledgeApplied(recompositionGeneration)
                runtimeObserver.recordApplied(
                    BenchmarkItemAppliedComposition(
                        generation = recompositionGeneration,
                        receiverModifier = modifier,
                        onClick = onClick,
                        style = style,
                        interactionSourceStrategy = interactionSourceStrategy,
                        interactionSource = interactionSource,
                        clickableModifier = clickableModifier,
                        markerBefore = markerBefore,
                        markerAfter = recompositionDriver.marker,
                    ),
                )
            }
        }
    }

    Box(
        modifier = clickableModifier,
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
