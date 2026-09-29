// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog

import io.daio.wild.a2ui.catalog.components.AudioPlayerComponent
import io.daio.wild.a2ui.catalog.components.CardComponent
import io.daio.wild.a2ui.catalog.components.ChoicePickerComponent
import io.daio.wild.a2ui.catalog.components.ColumnComponent
import io.daio.wild.a2ui.catalog.components.DateTimeInputComponent
import io.daio.wild.a2ui.catalog.components.DividerComponent
import io.daio.wild.a2ui.catalog.components.IconComponent
import io.daio.wild.a2ui.catalog.components.ImageComponent
import io.daio.wild.a2ui.catalog.components.ListComponent
import io.daio.wild.a2ui.catalog.components.ModalComponent
import io.daio.wild.a2ui.catalog.components.RowComponent
import io.daio.wild.a2ui.catalog.components.SliderComponent
import io.daio.wild.a2ui.catalog.components.TabsComponent
import io.daio.wild.a2ui.catalog.components.TextComponent
import io.daio.wild.a2ui.catalog.components.VideoComponent
import io.daio.wild.a2ui.compose.A2uiCatalog

/** The A2UI Basic Catalog v0.9.1 identifier used by Wild. @since 0.1.0 */
const val WILD_BASIC_CATALOG_ID = "https://a2ui.org/specification/v0_9_1/catalogs/basic/catalog.json"

/**
 * Creates Wild's display, layout, and Phase 3a stub Basic Catalog.
 *
 * Example: `A2uiSurface(surface, wildA2uiBasicCatalogV1(), processor)`
 *
 * @since 0.1.0
 */
fun wildA2uiBasicCatalogV1(): A2uiCatalog =
    A2uiCatalog(
        catalogId = WILD_BASIC_CATALOG_ID,
        components =
            listOf(
                TextComponent,
                ColumnComponent,
                RowComponent,
                CardComponent,
                DividerComponent,
                IconComponent,
                ListComponent,
                ImageComponent,
                VideoComponent,
                AudioPlayerComponent,
                ModalComponent,
                TabsComponent,
                SliderComponent,
                DateTimeInputComponent,
                ChoicePickerComponent,
            ),
    )
