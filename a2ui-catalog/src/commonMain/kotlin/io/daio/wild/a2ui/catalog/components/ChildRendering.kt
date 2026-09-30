// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.daio.wild.a2ui.compose.A2uiComponent
import io.daio.wild.a2ui.compose.A2uiComponentProperties
import io.daio.wild.a2ui.compose.A2uiComponentScope
import io.daio.wild.a2ui.compose.A2uiComponentState
import io.daio.wild.components.progress.CircularProgressIndicator
import io.daio.wild.components.text.Text
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Composable
internal fun A2uiComponentScope.RenderChild(
    id: String,
    modifier: Modifier = Modifier,
) {
    when (val child = observeComponentState(id)) {
        A2uiComponentState.Loading -> CircularProgressIndicator(modifier)
        is A2uiComponentState.Error -> Text(child.message, modifier)
        is A2uiComponentState.Success -> A2uiComponent(child, modifier)
    }
}

internal fun A2uiComponentProperties.stringValue(name: String): String? = raw(name)?.jsonPrimitive?.contentOrNull

internal fun verticalArrangement(value: String?): Arrangement.Vertical =
    when (value) {
        "center" -> Arrangement.Center
        "end" -> Arrangement.Bottom
        "spaceBetween" -> Arrangement.SpaceBetween
        "spaceAround" -> Arrangement.SpaceAround
        "spaceEvenly" -> Arrangement.SpaceEvenly
        else -> Arrangement.Top
    }

internal fun horizontalArrangement(value: String?): Arrangement.Horizontal =
    when (value) {
        "center" -> Arrangement.Center
        "end" -> Arrangement.End
        "spaceBetween" -> Arrangement.SpaceBetween
        "spaceAround" -> Arrangement.SpaceAround
        "spaceEvenly" -> Arrangement.SpaceEvenly
        else -> Arrangement.Start
    }

internal fun verticalAlignment(value: String?): Alignment.Vertical =
    when (value) {
        "center" -> Alignment.CenterVertically
        "end" -> Alignment.Bottom
        else -> Alignment.Top
    }

internal fun horizontalAlignment(value: String?): Alignment.Horizontal =
    when (value) {
        "center" -> Alignment.CenterHorizontally
        "end" -> Alignment.End
        else -> Alignment.Start
    }
