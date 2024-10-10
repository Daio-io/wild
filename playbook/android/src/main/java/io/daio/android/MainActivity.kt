package io.daio.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.style.Border
import io.daio.wild.tv.container.Container
import io.daio.wild.tv.container.ContainerDefaults

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                repeat(20) {
                    item {
                        Container(
                            style =
                                ContainerDefaults.style(
                                    colors =
                                        ContainerDefaults.colors(
                                            Color.Black,
                                            focusedColor = Color.Red,
                                        ),
                                    scale = ContainerDefaults.scale(focusedScale = 1.5f),
                                    borders =
                                        ContainerDefaults.borders(
                                            focusedBorder =
                                                Border(
                                                    color = Color.Blue,
                                                    width = 2.dp,
                                                ),
                                        ),
                                ),
                            modifier = Modifier.width(200.dp),
                            onClick = {
                                println("Clicked!")
                            },
                        ) {
                            BasicText(text = "Click Me", color = { Color.White })
                        }
                    }
                }
            }
        }
    }
}
