// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.daio.wild.a2ui.compose.A2uiComponent
import io.daio.wild.a2ui.compose.A2uiComponentScope
import io.daio.wild.a2ui.compose.A2uiComponentState
import io.daio.wild.components.progress.CircularProgressIndicator
import io.daio.wild.components.text.Text

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
