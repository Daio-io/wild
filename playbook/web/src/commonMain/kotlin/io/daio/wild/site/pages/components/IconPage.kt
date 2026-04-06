// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import io.daio.wild.components.icon.Icon
import io.daio.wild.site.components.ComponentPage
import io.daio.wild.site.components.ComponentPageData
import io.daio.wild.site.components.Demo
import io.daio.wild.site.components.Platform
import io.daio.wild.site.components.Prop
import io.daio.wild.site.theme.SiteTheme

private val StarIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Star",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 2f)
            lineTo(15.09f, 8.26f)
            lineTo(22f, 9.27f)
            lineTo(17f, 14.14f)
            lineTo(18.18f, 21.02f)
            lineTo(12f, 17.77f)
            lineTo(5.82f, 21.02f)
            lineTo(7f, 14.14f)
            lineTo(2f, 9.27f)
            lineTo(8.91f, 8.26f)
            close()
        }
    }.build()
}

private val HeartIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Heart",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 21.35f)
            lineTo(10.55f, 20.03f)
            curveTo(5.4f, 15.36f, 2f, 12.28f, 2f, 8.5f)
            curveTo(2f, 5.42f, 4.42f, 3f, 7.5f, 3f)
            curveTo(9.24f, 3f, 10.91f, 3.81f, 12f, 5.09f)
            curveTo(13.09f, 3.81f, 14.76f, 3f, 16.5f, 3f)
            curveTo(19.58f, 3f, 22f, 5.42f, 22f, 8.5f)
            curveTo(22f, 12.28f, 18.6f, 15.36f, 13.45f, 20.04f)
            close()
        }
    }.build()
}

private val CircleIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Circle",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.EvenOdd) {
            moveTo(12f, 2f)
            arcToRelative(10f, 10f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 20f)
            arcToRelative(10f, 10f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, -20f)
        }
    }.build()
}

private val DiamondIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Diamond",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 2f)
            lineTo(22f, 12f)
            lineTo(12f, 22f)
            lineTo(2f, 12f)
            close()
        }
    }.build()
}

@Composable
fun IconPage(
    modifier: Modifier = Modifier,
    data: ComponentPageData = IconPageDefaults.data,
) {
    ComponentPage(
        modifier = modifier,
        data = data,
    )
}

object IconPageDefaults {
    val data =
        ComponentPageData(
            name = "Icon",
            description =
                "Displays an icon from a Painter, ImageVector, or ImageBitmap. " +
                    "Tints with LocalContentColor by default. Pass Color.Unspecified to disable tinting.",
            module = "io.daio.wild:icon",
            demos =
                listOf(
                    Demo("Default Tint", "Icons tinted with the primary text color.") {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = StarIcon,
                                contentDescription = "Star",
                                modifier = Modifier.size(24.dp),
                                tint = SiteTheme.colors.textPrimary,
                            )
                            Icon(
                                imageVector = HeartIcon,
                                contentDescription = "Heart",
                                modifier = Modifier.size(24.dp),
                                tint = SiteTheme.colors.textPrimary,
                            )
                            Icon(
                                imageVector = CircleIcon,
                                contentDescription = "Circle",
                                modifier = Modifier.size(24.dp),
                                tint = SiteTheme.colors.textPrimary,
                            )
                            Icon(
                                imageVector = DiamondIcon,
                                contentDescription = "Diamond",
                                modifier = Modifier.size(24.dp),
                                tint = SiteTheme.colors.textPrimary,
                            )
                        }
                    },
                    Demo("Custom Tint", "Icons with custom tint colors.") {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = HeartIcon,
                                contentDescription = "Red heart",
                                modifier = Modifier.size(24.dp),
                                tint = Color(0xFFE53E3E),
                            )
                            Icon(
                                imageVector = StarIcon,
                                contentDescription = "Gold star",
                                modifier = Modifier.size(24.dp),
                                tint = Color(0xFFD69E2E),
                            )
                            Icon(
                                imageVector = CircleIcon,
                                contentDescription = "Green circle",
                                modifier = Modifier.size(24.dp),
                                tint = SiteTheme.colors.accent,
                            )
                        }
                    },
                    Demo("Sizes", "Icons at different sizes.") {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = StarIcon,
                                contentDescription = "Small",
                                modifier = Modifier.size(16.dp),
                                tint = SiteTheme.colors.textPrimary,
                            )
                            Icon(
                                imageVector = StarIcon,
                                contentDescription = "Default",
                                modifier = Modifier.size(24.dp),
                                tint = SiteTheme.colors.textPrimary,
                            )
                            Icon(
                                imageVector = StarIcon,
                                contentDescription = "Large",
                                modifier = Modifier.size(32.dp),
                                tint = SiteTheme.colors.textPrimary,
                            )
                            Icon(
                                imageVector = StarIcon,
                                contentDescription = "Extra large",
                                modifier = Modifier.size(48.dp),
                                tint = SiteTheme.colors.textPrimary,
                            )
                        }
                    },
                ),
            usage =
                """
                // ImageVector
                Icon(
                    imageVector = myImageVector,
                    contentDescription = "Favorite",
                    tint = LocalContentColor.current,
                )

                // Painter
                Icon(
                    painter = painterResource(Res.drawable.my_icon),
                    contentDescription = "Custom icon",
                )

                // Disable tinting
                Icon(
                    imageVector = myImageVector,
                    contentDescription = "Original colors",
                    tint = Color.Unspecified,
                )
                """.trimIndent(),
            props =
                listOf(
                    Prop("painter / imageVector / bitmap", "Painter / ImageVector / ImageBitmap"),
                    Prop("contentDescription", "String?"),
                    Prop("modifier", "Modifier", default = "Modifier"),
                    Prop("tint", "Color", default = "LocalContentColor.current"),
                ),
            platforms = listOf(Platform.Android, Platform.AndroidTV, Platform.Desktop, Platform.Web),
        )
}
