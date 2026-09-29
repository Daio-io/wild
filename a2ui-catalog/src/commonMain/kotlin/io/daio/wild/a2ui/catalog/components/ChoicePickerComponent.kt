// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.daio.wild.a2ui.compose.A2uiComponent
import io.daio.wild.a2ui.compose.A2uiComponentProperties
import io.daio.wild.a2ui.compose.A2uiComponentScope
import io.daio.wild.a2ui.compose.A2uiProperty
import io.daio.wild.components.text.Text

/** Phase 3a ChoicePicker placeholder. @since 0.1.0 */
object ChoicePickerComponent : A2uiComponent {
    override val name = "ChoicePicker"
    override val properties = emptyList<A2uiProperty<*>>()

    @Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        Text("Unsupported: ChoicePicker", modifier)
    }
}
