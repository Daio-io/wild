package io.daio.android.tv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor
import io.daio.wild.style.StyleDefaults
import io.daio.wild.tv.button.Button

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
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = Color.Black,
                                    contentColor = Color.White,
                                    focusedBackgroundColor = Color.Red,
                                    focusedContentColor = Color.Black,
                                ),
                            scale = StyleDefaults.scale(focusedScale = 1.2f),
                            shapes = StyleDefaults.shapes(RoundedCornerShape(12.dp)),
                        ),
                    modifier = Modifier.width(200.dp),
                    onClick = {
                        println("Clicked!")
                    },
                ) {
                    val color = LocalContentColor.current
                    BasicText(text = "Click Me", color = { color })
                }
            }
        }
    }
}
