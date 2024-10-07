package io.daio.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.BasicContainer
import io.daio.wild.foundation.ContainerDefaults
import io.daio.wild.foundation.Style

@Composable
fun App(modifier: Modifier = Modifier) {
    LazyRow(
        modifier =
            modifier.fillMaxSize()
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(100) {
            BasicContainer(
                style =
                    Style(
                        colors =
                            ContainerDefaults.colors(
                                color = Color.Red,
                                pressedColor = Color.Blue,
                                focusedColor = Color.Black,
                            ),
                    ),
                modifier =
                    Modifier.clickable {
                        println("clicked")
                    },
            ) {
                BasicText("Click Me")
            }
        }
    }
}
