package io.daio.android.tv

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.tv.container.Container
import io.daio.wild.tv.container.ContainerDefaults

@Composable
fun TvLayout(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        repeat(20) {
            item {
                Container(
                    colors = ContainerDefaults.colors(Color.Black, focusedColor = Color.Red),
                    onClick = {
                        Log.d("TAGGY", "Clicked")
                    },
                    onLongClick = {},
                    modifier = Modifier.size(100.dp),
                ) {
                    BasicText(text = "Click Me", color = { Color.White })
                }
            }
        }
    }
}
