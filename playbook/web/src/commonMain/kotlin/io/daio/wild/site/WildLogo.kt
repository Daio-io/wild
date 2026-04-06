// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

private val LeafDarkGreen = Color(0xFF3B5A41)
private val LeafMediumGreen = Color(0xFF627A65)
private val LeafLightGreen = Color(0xFF94A996)
private val LeafMutedGreen = Color(0xFF79937D)

@Composable
fun WildLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(28.dp, 18.dp)) {
        val sx = size.width / 100f
        val sy = size.height / 65f
        drawLeaves(sx, sy)
    }
}

private fun DrawScope.drawLeaves(
    sx: Float,
    sy: Float,
) {
    // Back-left leaf (darkest green)
    val path1 =
        Path().apply {
            moveTo(26.20f * sx, 9.09f * sy)
            lineTo(33.38f * sx, 22.76f * sy)
            lineTo(44.56f * sx, 44.03f * sy)
            cubicTo(47.92f * sx, 50.40f * sy, 45.81f * sx, 58.20f * sy, 39.92f * sx, 62.06f * sy)
            cubicTo(39.54f * sx, 62.32f * sy, 39.14f * sx, 62.56f * sy, 38.74f * sx, 62.77f * sy)
            cubicTo(37.91f * sx, 63.21f * sy, 37.05f * sx, 63.56f * sy, 36.16f * sx, 63.82f * sy)
            cubicTo(35.30f * sx, 64.07f * sy, 34.43f * sx, 64.23f * sy, 33.53f * sx, 64.32f * sy)
            cubicTo(33.30f * sx, 64.34f * sy, 33.06f * sx, 64.35f * sy, 32.82f * sx, 64.37f * sy)
            cubicTo(32.10f * sx, 64.39f * sy, 31.38f * sx, 64.37f * sy, 30.66f * sx, 64.28f * sy)
            cubicTo(30.42f * sx, 64.25f * sy, 30.19f * sx, 64.22f * sy, 29.95f * sx, 64.18f * sy)
            cubicTo(29.07f * sx, 64.03f * sy, 28.20f * sx, 63.79f * sy, 27.37f * sx, 63.48f * sy)
            cubicTo(24.30f * sx, 62.34f * sy, 21.62f * sx, 60.08f * sy, 19.98f * sx, 56.96f * sy)
            lineTo(1.60f * sx, 22.00f * sy)
            cubicTo(-1.97f * sx, 15.21f * sy, 0.64f * sx, 6.82f * sy, 7.42f * sx, 3.24f * sy)
            cubicTo(7.86f * sx, 3.02f * sy, 8.30f * sx, 2.82f * sy, 8.73f * sx, 2.66f * sy)
            cubicTo(10.58f * sx, 1.90f * sy, 12.52f * sx, 1.59f * sy, 14.43f * sx, 1.66f * sy)
            cubicTo(19.22f * sx, 1.86f * sy, 23.78f * sx, 4.53f * sy, 26.18f * sx, 9.09f * sy)
            close()
        }
    drawPath(path1, LeafDarkGreen)

    // Middle leaf
    val path2 =
        Path().apply {
            moveTo(29.16f * sx, 31.12f * sy)
            lineTo(37.71f * sx, 18.30f * sy)
            lineTo(40.11f * sx, 15.03f * sy)
            cubicTo(43.07f * sx, 10.97f * sy, 47.83f * sx, 8.99f * sy, 52.52f * sx, 9.38f * sy)
            cubicTo(54.80f * sx, 9.57f * sy, 57.04f * sx, 10.32f * sy, 59.06f * sx, 11.69f * sy)
            cubicTo(59.22f * sx, 11.79f * sy, 59.37f * sx, 11.89f * sy, 59.52f * sx, 12.00f * sy)
            cubicTo(62.66f * sx, 14.31f * sy, 64.56f * sx, 17.68f * sy, 65.07f * sx, 21.26f * sy)
            cubicTo(65.23f * sx, 22.40f * sy, 65.26f * sx, 23.57f * sy, 65.13f * sx, 24.72f * sy)
            cubicTo(64.89f * sx, 27.05f * sy, 64.03f * sx, 29.36f * sy, 62.53f * sx, 31.41f * sy)
            lineTo(44.41f * sx, 57.14f * sy)
        }
    drawPath(path2, LeafMediumGreen)

    // Right leaf (lightest)
    val path3 =
        Path().apply {
            moveTo(98.53f * sx, 19.82f * sy)
            lineTo(91.73f * sx, 33.34f * sy)
            lineTo(81.15f * sx, 54.34f * sy)
            cubicTo(77.99f * sx, 60.64f * sy, 70.63f * sx, 63.38f * sy, 64.25f * sx, 60.87f * sy)
            cubicTo(63.83f * sx, 60.72f * sy, 63.42f * sx, 60.52f * sy, 63.02f * sx, 60.32f * sy)
            cubicTo(62.19f * sx, 59.90f * sy, 61.45f * sx, 59.41f * sy, 60.74f * sx, 58.85f * sy)
            cubicTo(60.05f * sx, 58.30f * sy, 59.43f * sx, 57.69f * sy, 58.87f * sx, 57.02f * sy)
            cubicTo(58.72f * sx, 56.84f * sy, 58.57f * sx, 56.67f * sy, 58.43f * sx, 56.47f * sy)
            cubicTo(58.00f * sx, 55.91f * sy, 57.62f * sx, 55.31f * sy, 57.29f * sx, 54.69f * sy)
            cubicTo(57.18f * sx, 54.49f * sy, 57.08f * sx, 54.28f * sy, 56.98f * sx, 54.07f * sy)
            cubicTo(56.60f * sx, 53.28f * sy, 56.30f * sx, 52.45f * sy, 56.08f * sx, 51.60f * sy)
            cubicTo(55.27f * sx, 48.50f * sy, 55.54f * sx, 45.07f * sy, 57.10f * sx, 41.98f * sy)
            lineTo(74.48f * sx, 7.45f * sy)
            cubicTo(77.85f * sx, 0.75f * sy, 85.97f * sx, -1.93f * sy, 92.63f * sx, 1.48f * sy)
            cubicTo(93.05f * sx, 1.70f * sy, 93.45f * sx, 1.92f * sy, 93.82f * sx, 2.19f * sy)
            cubicTo(95.47f * sx, 3.23f * sy, 96.80f * sx, 4.60f * sy, 97.81f * sx, 6.17f * sy)
            cubicTo(100.35f * sx, 10.12f * sy, 100.80f * sx, 15.29f * sy, 98.54f * sx, 19.80f * sy)
            close()
        }
    drawPath(path3, LeafLightGreen)

    // Front-right leaf (muted)
    val path4 =
        Path().apply {
            moveTo(62.34f * sx, 12.89f * sy)
            lineTo(69.29f * sx, 24.82f * sy)
            lineTo(80.11f * sx, 43.38f * sy)
            cubicTo(83.36f * sx, 48.95f * sy, 81.79f * sx, 56.37f * sy, 76.67f * sx, 60.54f * sy)
            cubicTo(76.33f * sx, 60.83f * sy, 75.98f * sx, 61.09f * sy, 75.63f * sx, 61.33f * sy)
            cubicTo(74.90f * sx, 61.81f * sy, 74.15f * sx, 62.22f * sy, 73.38f * sx, 62.56f * sy)
            cubicTo(72.62f * sx, 62.87f * sy, 71.85f * sx, 63.12f * sy, 71.05f * sx, 63.28f * sy)
            cubicTo(70.84f * sx, 63.33f * sy, 70.64f * sx, 63.37f * sy, 70.42f * sx, 63.40f * sy)
            cubicTo(69.77f * sx, 63.50f * sy, 69.12f * sx, 63.54f * sy, 68.47f * sx, 63.53f * sy)
            cubicTo(68.26f * sx, 63.53f * sy, 68.05f * sx, 63.53f * sy, 67.84f * sx, 63.51f * sy)
            cubicTo(67.05f * sx, 63.46f * sy, 66.27f * sx, 63.32f * sy, 65.51f * sx, 63.12f * sy)
            cubicTo(62.72f * sx, 62.36f * sy, 60.25f * sx, 60.56f * sy, 58.66f * sx, 57.83f * sy)
            lineTo(40.88f * sx, 27.32f * sy)
            cubicTo(37.43f * sx, 21.41f * sy, 39.44f * sx, 13.36f * sy, 45.35f * sx, 9.37f * sy)
            cubicTo(45.73f * sx, 9.12f * sy, 46.11f * sx, 8.89f * sy, 46.50f * sx, 8.70f * sy)
            cubicTo(48.12f * sx, 7.82f * sy, 49.84f * sx, 7.33f * sy, 51.54f * sx, 7.21f * sy)
            cubicTo(55.84f * sx, 6.91f * sy, 60.01f * sx, 8.92f * sy, 62.34f * sx, 12.90f * sy)
            close()
        }
    drawPath(path4, LeafMutedGreen)
}
