// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import io.daio.wild.components.progress.CircularProgressIndicator
import io.daio.wild.components.progress.LinearProgressIndicator
import io.daio.wild.components.text.Text
import io.daio.wild.site.components.ComponentPage
import io.daio.wild.site.components.ComponentPageData
import io.daio.wild.site.components.Demo
import io.daio.wild.site.components.Platform
import io.daio.wild.site.components.Prop
import io.daio.wild.site.theme.SiteTheme

@Composable
fun ProgressPage(
    modifier: Modifier = Modifier,
    data: ComponentPageData = ProgressPageDefaults.data,
) {
    ComponentPage(
        modifier = modifier,
        data = data,
    )
}

object ProgressPageDefaults {
    @Composable
    private fun LinearDrawing(progress: Float) {
        val colors = SiteTheme.colors
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .drawBehind {
                        drawRoundRect(
                            color = colors.border,
                            size = size,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2),
                        )
                        drawRoundRect(
                            color = colors.accent,
                            size = Size(size.width * progress, size.height),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2),
                        )
                    },
        )
    }

    @Composable
    private fun CircularDrawing(progress: Float) {
        val colors = SiteTheme.colors
        Canvas(modifier = Modifier.size(64.dp)) {
            val strokeWidth = 8.dp.toPx()
            drawArc(
                color = colors.border,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth),
            )
            drawArc(
                color = colors.accent,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
    }

    @Composable
    private fun rememberIndeterminatePhase(): Float {
        val transition = rememberInfiniteTransition(label = "progress-indicator")
        val phase by
            transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(1200, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart,
                    ),
                label = "progress-phase",
            )
        return phase
    }

    @Composable
    private fun LinearIndeterminateDrawing(phase: Float) {
        val colors = SiteTheme.colors
        Canvas(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(10.dp),
        ) {
            drawRoundRect(
                color = colors.border,
                size = size,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2),
            )
            val segmentWidth = size.width * 0.35f
            val segmentStart = (size.width + segmentWidth) * phase - segmentWidth
            drawRoundRect(
                color = colors.accent,
                topLeft = Offset(segmentStart, 0f),
                size = Size(segmentWidth, size.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2),
            )
        }
    }

    @Composable
    private fun CircularIndeterminateDrawing(phase: Float) {
        val colors = SiteTheme.colors
        Canvas(modifier = Modifier.size(64.dp)) {
            drawArc(
                color = colors.border,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx()),
            )
            drawArc(
                color = colors.accent,
                startAngle = phase * 360f,
                sweepAngle = 110f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
            )
        }
    }

    val data =
        ComponentPageData(
            name = "Progress",
            description =
                "Semantic linear and circular progress primitives that leave dimensions, " +
                    "drawing, and animation to the caller.",
            module = "io.daio.wild.components:progress",
            demos =
                listOf(
                    Demo("Determinate linear", "The slot receives normalized progress for custom drawing.") {
                        LinearProgressIndicator(progress = { 0.65f }) { progress ->
                            LinearDrawing(progress)
                        }
                    },
                    Demo("Indeterminate linear", "The caller supplies the animation and artwork.") {
                        val phase = rememberIndeterminatePhase()
                        LinearProgressIndicator {
                            LinearIndeterminateDrawing(phase)
                        }
                    },
                    Demo("Determinate circular", "A circular indicator with caller-owned geometry.") {
                        CircularProgressIndicator(progress = { 0.7f }) { progress ->
                            CircularDrawing(progress)
                        }
                    },
                    Demo("Indeterminate circular", "A caller-animated circular indicator.") {
                        val phase = rememberIndeterminatePhase()
                        CircularProgressIndicator {
                            CircularIndeterminateDrawing(phase)
                        }
                    },
                    Demo("Custom drawing", "The primitives accept any content, including labels.") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            LinearProgressIndicator(progress = { 0.4f }) { progress ->
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    LinearDrawing(progress)
                                    Text("40%", color = SiteTheme.colors.textSecondary)
                                }
                            }
                        }
                    },
                ),
            usage =
                """
                LinearProgressIndicator(progress = { progress }) { value ->
                    // Draw your linear indicator using value.
                }

                CircularProgressIndicator {
                    // Draw your caller-owned indeterminate indicator.
                }
                """.trimIndent(),
            props =
                listOf(
                    Prop("progress (determinate overload)", "() -> Float", required = true),
                    Prop(
                        "content (determinate overload)",
                        "@Composable BoxScope.(Float) -> Unit",
                        required = true,
                    ),
                    Prop(
                        "content (indeterminate overload)",
                        "@Composable BoxScope.() -> Unit",
                        required = true,
                    ),
                    Prop("modifier", "Modifier", default = "Modifier"),
                ),
            platforms = listOf(Platform.Android, Platform.AndroidTV, Platform.Desktop, Platform.Web),
        )
}
