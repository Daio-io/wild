// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.foundations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.layout.divider.HorizontalDivider
import io.daio.wild.site.components.CodeBlock
import io.daio.wild.site.components.Prop
import io.daio.wild.site.components.PropsTable
import io.daio.wild.site.theme.SiteTheme

@Composable
fun ModifierPage(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = "Modifier",
            style = SiteTheme.typography.h1,
            color = SiteTheme.colors.textPrimary,
        )
        Text(
            text = "Utility modifier extensions for conditional modifier application.",
            style = SiteTheme.typography.body,
            color = SiteTheme.colors.textSecondary,
        )

        HorizontalDivider(color = SiteTheme.colors.border)

        // thenIf
        Text(
            text = "thenIf",
            style = SiteTheme.typography.h2,
            color = SiteTheme.colors.textPrimary,
        )
        Text(
            text = "Conditionally apply a modifier based on a boolean condition:",
            style = SiteTheme.typography.body,
            color = SiteTheme.colors.textSecondary,
        )
        CodeBlock(
            code =
                """
                Modifier
                    .padding(16.dp)
                    .thenIf(
                        condition = isHighlighted,
                        ifTrueModifier = Modifier.background(Color.Yellow),
                        ifFalseModifier = Modifier.background(Color.Transparent),
                    )
                """.trimIndent(),
            tabs = listOf("Kotlin"),
        )
        PropsTable(
            props =
                listOf(
                    Prop("condition", "Boolean", required = true),
                    Prop("ifTrueModifier", "Modifier", required = true),
                    Prop("ifFalseModifier", "Modifier", default = "Modifier"),
                ),
        )

        HorizontalDivider(color = SiteTheme.colors.border)

        // thenIfNotNull
        Text(
            text = "thenIfNotNull",
            style = SiteTheme.typography.h2,
            color = SiteTheme.colors.textPrimary,
        )
        Text(
            text = "Conditionally apply a modifier when a value is non-null, with the value available in the lambda:",
            style = SiteTheme.typography.body,
            color = SiteTheme.colors.textSecondary,
        )
        CodeBlock(
            code =
                """
                Modifier
                    .padding(16.dp)
                    .thenIfNotNull(
                        value = borderColor,
                        ifNotNullModifier = { color ->
                            Modifier.border(1.dp, color)
                        },
                    )
                """.trimIndent(),
            tabs = listOf("Kotlin"),
        )
        PropsTable(
            props =
                listOf(
                    Prop("value", "T?", required = true),
                    Prop("ifNotNullModifier", "(T) -> Modifier", required = true),
                    Prop("ifNullModifier", "Modifier", default = "Modifier"),
                ),
        )
    }
}
