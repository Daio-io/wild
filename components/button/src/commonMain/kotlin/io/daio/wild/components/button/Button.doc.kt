// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.button

import io.daio.wild.docs.model.Platform
import io.daio.wild.docs.model.componentDoc

internal val ButtonDoc =
    componentDoc(
        name = "Button",
        symbol = "io.daio.wild.components.button.Button",
        artifact = "io.daio.wild:button",
        importStatement = "import io.daio.wild.components.button.Button",
        category = "Action",
        platforms =
            setOf(
                Platform.Common,
                Platform.Android,
                Platform.AndroidTV,
                Platform.Desktop,
                Platform.Web,
                Platform.IOS,
            ),
    ) {
        summary(
            "An unstyled action primitive with click, long-click, double-click, " +
                "focus, hover, press, and disabled-state support.",
        )
        keywords(
            "button",
            "action",
            "click",
            "press",
            "cta",
            "submit",
            "focus",
            "remote",
            "tv",
        )
        parameter("onClick", "Invoked when the button is clicked.")
        parameter("modifier", "Applies layout, drawing, input, and semantics modifications.")
        parameter("enabled", "Controls whether the button accepts input.")
        parameter("onLongClick", "Optionally handles a long click.")
        parameter("onDoubleClick", "Optionally handles a double click.")
        parameter("style", "Controls visual values for each interaction state.")
        parameter("contentPadding", "Adds padding between the button surface and its content.")
        parameter("interactionSource", "Observes or emits the button's interactions.")
        parameter("content", "Provides content inside the button's BoxScope.")
        guidance {
            doThis("Use Button for actions such as confirming, submitting, or changing state.")
            doThis("Provide focus styling when the target platform supports remote or keyboard input.")
            avoid("Use Button for navigation when a link-style semantic is more appropriate.")
            avoid("Replace the content slot with a manually clickable child.")
        }
        example(
            sample = BasicButtonSample,
            title = "Basic",
            description = "A minimal button with text content.",
        )
    }
