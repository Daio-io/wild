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
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Surface
import io.daio.wild.container.Container
import io.daio.wild.content.LocalContentColor
import io.daio.wild.style.Border
import io.daio.wild.style.StyleDefaults
import io.daio.wild.style.clickable
import io.daio.wild.style.experimentalClickable

@Composable
fun TvLayout(
    modifier: Modifier = Modifier,
    mode: String = "list",
    itemsType: String = "container",
) {
    when (mode) {
        "list" -> OptionsList(itemsType, modifier)
        "grid" -> OptionsGrid(itemsType, modifier)
    }
}

@Composable
private fun OptionsGrid(
    itemsType: String,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .background(Color.Black)
                .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        repeat(20) {
            item(key = it) {
                LazyRow {
                    repeat(100) { i ->
                        item(key = i) {
                            when (itemsType) {
                                "container" -> {
                                    ContainerItem(
                                        title = itemsType,
                                        onClick = { },
                                    )
                                }

                                "surface" -> {
                                    SurfaceItem(
                                        title = itemsType,
                                        onClick = { },
                                    )
                                }

                                "modifier" -> {
                                    ModifierItem(
                                        title = itemsType,
                                        onClick = { },
                                    )
                                }

                                "experimental_modifier" -> {
                                    ExperimentalModifierItem(
                                        title = itemsType,
                                        onClick = { },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionsList(
    itemsType: String,
    modifier: Modifier = Modifier,
) {
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
        repeat(100) {
            item(key = it) {
                when (itemsType) {
                    "container" -> {
                        ContainerItem(
                            title = itemsType,
                            onClick = { },
                        )
                    }

                    "surface" -> {
                        SurfaceItem(
                            title = itemsType,
                            onClick = { },
                        )
                    }

                    "modifier" -> {
                        ModifierItem(
                            title = itemsType,
                            onClick = { },
                        )
                    }

                    "experimental_modifier" -> {
                        ExperimentalModifierItem(
                            title = itemsType,
                            onClick = { },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContainerItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Container(
        style =
            StyleDefaults.style(
                colors =
                    StyleDefaults.colors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White,
                        focusedBackgroundColor = Color.Red,
                        focusedContentColor = Color.Black,
                        pressedBackgroundColor = Color.Black.copy(alpha = .6f),
                        disabledBackgroundColor = Color.Black.copy(alpha = .3f),
                    ),
                borders =
                    StyleDefaults.borders(
                        border =
                            Border(
                                width = 2.dp,
                                inset = 2.dp,
                                color = Color.Red,
                            ),
                    ),
                scale = StyleDefaults.scale(focusedScale = 1.2f),
                shapes = StyleDefaults.shapes(RoundedCornerShape(8.dp)),
            ),
        modifier =
            modifier
                .size(100.dp),
        onClick = onClick,
    ) {
        val color = LocalContentColor.current
        BasicText(text = title, color = { color })
    }
}

@Composable
private fun SurfaceItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier =
            modifier
                .size(100.dp),
        interactionSource = interactionSource,
        onClick = onClick,
        colors =
            ClickableSurfaceDefaults.colors(
                containerColor = Color.Black,
                contentColor = Color.White,
                focusedContainerColor = Color.Red,
                focusedContentColor = Color.Black,
                pressedContainerColor = Color.Black.copy(alpha = .6f),
                disabledContainerColor = Color.Black.copy(alpha = .3f),
            ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.2f),
        border =
            ClickableSurfaceDefaults.border(
                border =
                    androidx.tv.material3.Border(
                        border = BorderStroke(width = 2.dp, Color.Red),
                        inset = 2.dp,
                    ),
            ),
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(8.dp)),
    ) {
        val color = LocalContentColor.current
        BasicText(text = title, color = { color })
    }
}

@Composable
private fun ModifierItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier =
            modifier
                .size(100.dp)
                .clickable(
                    onClick = onClick,
                    interactionSource = interactionSource,
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = Color.Black,
                                    contentColor = Color.White,
                                    focusedBackgroundColor = Color.Red,
                                    focusedContentColor = Color.Black,
                                    pressedBackgroundColor = Color.Black.copy(alpha = .6f),
                                    disabledBackgroundColor = Color.Black.copy(alpha = .3f),
                                ),
                            borders =
                                StyleDefaults.borders(
                                    border =
                                        Border(
                                            width = 2.dp,
                                            inset = 2.dp,
                                            color = Color.Red,
                                        ),
                                ),
                            scale = StyleDefaults.scale(focusedScale = 1.2f),
                            shapes = StyleDefaults.shapes(RoundedCornerShape(8.dp)),
                        ),
                ),
    ) {
        val color = LocalContentColor.current
        BasicText(text = title, color = { color })
    }
}

@Composable
private fun ExperimentalModifierItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier =
            modifier
                .size(100.dp)
                .experimentalClickable(
                    onClick = onClick,
                    interactionSource = interactionSource,
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = Color.Black,
                                    contentColor = Color.White,
                                    focusedBackgroundColor = Color.Red,
                                    focusedContentColor = Color.Black,
                                    pressedBackgroundColor = Color.Black.copy(alpha = .6f),
                                    disabledBackgroundColor = Color.Black.copy(alpha = .3f),
                                ),
                            borders =
                                StyleDefaults.borders(
                                    border =
                                        Border(
                                            width = 2.dp,
                                            inset = 2.dp,
                                            color = Color.Red,
                                        ),
                                ),
                            scale = StyleDefaults.scale(focusedScale = 1.2f),
                            shapes = StyleDefaults.shapes(RoundedCornerShape(8.dp)),
                        ),
                ),
    ) {
        val color = LocalContentColor.current
        BasicText(text = title, color = { color })
    }
}
