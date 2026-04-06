// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun Chevron(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
) {
    val rotation by animateFloatAsState(if (expanded) 90f else 0f, label = "chevronRotation")
    Canvas(modifier = modifier.size(10.dp).rotate(rotation)) {
        val path =
            Path().apply {
                moveTo(this@Canvas.size.width * 0.25f, this@Canvas.size.height * 0.15f)
                lineTo(this@Canvas.size.width * 0.75f, this@Canvas.size.height * 0.5f)
                lineTo(this@Canvas.size.width * 0.25f, this@Canvas.size.height * 0.85f)
            }
        drawPath(
            path,
            color = color,
            style = Stroke(width = 2f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}
