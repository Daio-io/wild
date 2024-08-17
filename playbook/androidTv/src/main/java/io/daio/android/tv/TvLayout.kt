package io.daio.android.tv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.Border
import io.daio.wild.tv.button.Button
import io.daio.wild.tv.button.ButtonDefaults
import io.daio.wild.tv.container.ContainerDefaults

@Composable
fun TvLayout(modifier: Modifier = Modifier) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        repeat(20) {
            item {
                Button(
                    colors = ContainerDefaults.colors(Color.Black, focusedColor = Color.Red),
                    scale = ButtonDefaults.scale(focusedScale = 1.5f),
                    borders =
                        ButtonDefaults.borders(
                            focusedBorder =
                                Border(
                                    color = Color.Blue,
                                    width = 2.dp,
                                ),
                        ),
                    modifier = Modifier.width(200.dp),
                    onClick = {
                    },
                ) {
                    BasicText(text = "Click Me", color = { Color.White })
                }
            }
        }
    }
}
