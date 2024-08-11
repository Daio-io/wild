package io.daio.android.tv

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.tv.button.Button
import io.daio.wild.tv.button.ButtonDefaults
import io.daio.wild.tv.container.ContainerDefaults

@Composable
fun TvLayout(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        repeat(20) {
            item {
                Button(
                    colors = ContainerDefaults.colors(Color.Black, focusedColor = Color.Red),
                    scale = ButtonDefaults.scale(focusedScale = 1.5f),
                    modifier = Modifier.size(100.dp),
                    onClick = {
                    },
                ) {
                    BasicText(text = "Click Me", color = { Color.White })
                }
            }
        }
    }
}
