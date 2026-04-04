// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.layout.divider.HorizontalDivider
import io.daio.wild.layout.divider.VerticalDivider
import io.daio.wild.site.components.ComponentPage
import io.daio.wild.site.components.ComponentPageData
import io.daio.wild.site.components.Demo
import io.daio.wild.site.components.Platform
import io.daio.wild.site.components.Prop
import io.daio.wild.site.theme.SiteTheme

@Composable
fun DividerPage(modifier: Modifier = Modifier) {
    ComponentPage(
        modifier = modifier,
        data =
            ComponentPageData(
                name = "Divider",
                description = "Horizontal and vertical dividers for visually separating content. Uses LocalContentColor by default.",
                module = "io.daio.wild:divider",
                demos =
                    listOf(
                        Demo("Horizontal", "Horizontal dividers between text.") {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Text("Section One", color = SiteTheme.colors.textPrimary)
                                HorizontalDivider(color = SiteTheme.colors.border)
                                Text("Section Two", color = SiteTheme.colors.textPrimary)
                                HorizontalDivider(color = SiteTheme.colors.accent, thickness = 2.dp)
                                Text("Section Three", color = SiteTheme.colors.textPrimary)
                            }
                        },
                        Demo("Vertical", "Vertical divider between inline elements.") {
                            Row(
                                modifier = Modifier.height(40.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            ) {
                                Text("Left", color = SiteTheme.colors.textPrimary)
                                VerticalDivider(color = SiteTheme.colors.border)
                                Text("Center", color = SiteTheme.colors.textPrimary)
                                VerticalDivider(color = SiteTheme.colors.accent, thickness = 2.dp)
                                Text("Right", color = SiteTheme.colors.textPrimary)
                            }
                        },
                    ),
                usage =
                    """
                    // Horizontal divider
                    HorizontalDivider(
                        color = MyTheme.colors.border,
                        thickness = 1.dp,
                    )

                    // Vertical divider
                    VerticalDivider(
                        color = MyTheme.colors.border,
                        thickness = 1.dp,
                    )
                    """.trimIndent(),
                props =
                    listOf(
                        Prop("modifier", "Modifier", default = "Modifier"),
                        Prop("color", "Color", default = "LocalContentColor.current"),
                        Prop("thickness", "Dp", default = "1.dp"),
                    ),
                platforms = listOf(Platform.Android, Platform.AndroidTV, Platform.Desktop, Platform.Web),
            ),
    )
}
