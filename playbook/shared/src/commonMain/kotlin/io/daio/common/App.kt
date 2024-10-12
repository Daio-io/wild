package io.daio.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App(modifier: Modifier = Modifier) {
    LazyRow(
        modifier =
            modifier.fillMaxSize()
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(100) {
//            BasicContainer(
//                style =
//                    Style(
//                        colors =
//                            StyleDefaults.colors(
//                                color = Color.Red,
//                                pressedBackgroundColor = Color.Blue,
//                                focusedBackgroundColor = Color.Black,
//                            ),
//                    ),
//                modifier =
//                    Modifier.clickable {
//                        println("clicked")
//                    },
//            ) {
//                BasicText("Click Me")
//            }
        }
    }
}
