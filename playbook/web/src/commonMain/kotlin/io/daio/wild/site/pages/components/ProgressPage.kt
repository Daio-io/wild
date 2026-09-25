// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.daio.wild.components.progress.CircularProgressIndicator
import io.daio.wild.components.progress.LinearProgressIndicator
import io.daio.wild.components.progress.ProgressIndicatorDefaults
import io.daio.wild.components.text.Text
import io.daio.wild.content.ProvidesContentColor
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
    val data =
        ComponentPageData(
            name = "Progress",
            description =
                "Linear and circular progress indicators with opinionated defaults, " +
                    "customizable strokes and animation, and a full custom content slot.",
            module = "io.daio.wild.components:progress",
            demos =
                listOf(
                    Demo("Default determinate", "Omit the content slot to use ProgressIndicatorDefaults.") {
                        ProvidesContentColor(SiteTheme.colors.accent) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                LinearProgressIndicator(
                                    progress = { 0.65f },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                CircularProgressIndicator(progress = { 0.7f })
                            }
                        }
                    },
                    Demo("Default indeterminate", "Built-in looping animation with LocalContentColor.") {
                        ProvidesContentColor(SiteTheme.colors.accent) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                CircularProgressIndicator()
                            }
                        }
                    },
                    Demo("Customized stroke", "Override stroke height/width on the default indicators.") {
                        ProvidesContentColor(SiteTheme.colors.accent) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                LinearProgressIndicator(progress = { 0.45f }) { progress ->
                                    ProgressIndicatorDefaults.LinearIndicator(
                                        progress = progress,
                                        strokeHeight = 8.dp,
                                    )
                                }
                                CircularProgressIndicator(progress = { 0.55f }) { progress ->
                                    ProgressIndicatorDefaults.CircularIndicator(
                                        progress = progress,
                                        strokeWidth = 8.dp,
                                    )
                                }
                            }
                        }
                    },
                    Demo("Animated stroke width", "Pass an animated Dp into the default indicator.") {
                        ProvidesContentColor(SiteTheme.colors.accent) {
                            val strokeWidth by
                                animateDpAsState(
                                    targetValue = 10.dp,
                                    label = "stroke-width",
                                )
                            CircularProgressIndicator {
                                ProgressIndicatorDefaults.CircularIndeterminateIndicator(
                                    strokeWidth = strokeWidth,
                                )
                            }
                        }
                    },
                    Demo("Full custom content", "Replace the slot with any artwork or labels.") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            LinearProgressIndicator(progress = { 0.4f }) { progress ->
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    ProvidesContentColor(SiteTheme.colors.accent) {
                                        ProgressIndicatorDefaults.LinearIndicator(progress = progress)
                                    }
                                    Text("40%", color = SiteTheme.colors.textSecondary)
                                }
                            }
                        }
                    },
                ),
            usage =
                """
                LinearProgressIndicator(progress = { progress })

                CircularProgressIndicator {
                    ProgressIndicatorDefaults.CircularIndeterminateIndicator(strokeWidth = 8.dp)
                }

                LinearProgressIndicator(progress = { progress }) { value ->
                    // Fully custom drawing using value.
                }
                """.trimIndent(),
            props =
                listOf(
                    Prop("progress (determinate overload)", "() -> Float", required = true),
                    Prop(
                        "content (determinate overload)",
                        "@Composable BoxScope.(Float) -> Unit",
                        default = "ProgressIndicatorDefaults.*Indicator",
                    ),
                    Prop(
                        "content (indeterminate overload)",
                        "@Composable BoxScope.() -> Unit",
                        default = "ProgressIndicatorDefaults.*IndeterminateIndicator",
                    ),
                    Prop("modifier", "Modifier", default = "Modifier"),
                    Prop(
                        "strokeHeight / strokeWidth",
                        "Dp",
                        default = "ProgressIndicatorDefaults.*",
                    ),
                    Prop(
                        "animationSpec",
                        "DurationBasedAnimationSpec<Float>",
                        default = "ProgressIndicatorDefaults.indeterminateAnimationSpec()",
                    ),
                ),
            platforms = listOf(Platform.Android, Platform.AndroidTV, Platform.Desktop, Platform.Web),
        )
}
