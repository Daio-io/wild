// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.android.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.common.CustomDesignSystemApp
import io.daio.wild.components.button.Button
import io.daio.wild.container.Container
import io.daio.wild.content.LocalContentColor
import io.daio.wild.style.Border
import io.daio.wild.style.StyleDefaults

enum class Screen {
    Main,
    CustomDs,
    Material3,
    MaterialTv,
}

@Composable
fun TvLayout(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableStateOf(Screen.Main) }

    BackHandler(currentScreen != Screen.Main) {
        currentScreen = Screen.Main
    }

    Container(color = Color.Black) {
        when (currentScreen) {
            Screen.Main ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            16.dp,
                            alignment = Alignment.CenterVertically,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    NavigationButton(
                        title = "Custom Design System Example",
                        onClick = {
                            currentScreen = Screen.CustomDs
                        },
                    )
                    NavigationButton(
                        title = "Material 3 on Tv Example",
                        onClick = {
                            currentScreen = Screen.Material3
                        },
                    )
                    NavigationButton(
                        title = "Material Tv Example",
                        onClick = {
                            currentScreen = Screen.MaterialTv
                        },
                    )
                }

            Screen.CustomDs -> CustomDesignSystemApp(modifier)
            Screen.Material3 -> TODO()
            Screen.MaterialTv -> TODO()
        }
    }
}

@Composable
private fun NavigationButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        style =
            StyleDefaults.style(
                colors =
                    StyleDefaults.colors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White,
                        focusedBackgroundColor = Color.Red,
                        focusedContentColor = Color.Black,
                        pressedBackgroundColor = Color.Black.copy(alpha = .6f),
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
        modifier = modifier.width(200.dp),
        onClick = onClick,
    ) {
        val color = LocalContentColor.current
        BasicText(text = title, color = { color })
    }
}
