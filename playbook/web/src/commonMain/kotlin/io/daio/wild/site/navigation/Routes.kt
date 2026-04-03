// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.navigation

sealed interface Route {
    val path: String

    data object GettingStarted : Route {
        override val path = "getting-started"
    }

    sealed interface Component : Route {
        data object Button : Component {
            override val path = "components/button"
        }

        data object Container : Component {
            override val path = "components/container"
        }

        data object Text : Component {
            override val path = "components/text"
        }

        data object Icon : Component {
            override val path = "components/icon"
        }

        data object ListItem : Component {
            override val path = "components/list-item"
        }

        data object Toggleable : Component {
            override val path = "components/toggleable"
        }

        data object Divider : Component {
            override val path = "components/divider"
        }
    }

    sealed interface Foundation : Route {
        data object Style : Foundation {
            override val path = "foundations/style"
        }

        data object ContentColor : Foundation {
            override val path = "foundations/content-color"
        }

        data object Modifier : Foundation {
            override val path = "foundations/modifier"
        }

        data object InteractionState : Foundation {
            override val path = "foundations/interaction-state"
        }
    }
}

enum class Section {
    GettingStarted,
    Components,
    Foundations,
}

private val routesByPath: Map<String, Route> by lazy {
    buildMap {
        put(Route.GettingStarted.path, Route.GettingStarted)
        put(Route.Component.Button.path, Route.Component.Button)
        put(Route.Component.Container.path, Route.Component.Container)
        put(Route.Component.Text.path, Route.Component.Text)
        put(Route.Component.Icon.path, Route.Component.Icon)
        put(Route.Component.ListItem.path, Route.Component.ListItem)
        put(Route.Component.Toggleable.path, Route.Component.Toggleable)
        put(Route.Component.Divider.path, Route.Component.Divider)
        put(Route.Foundation.Style.path, Route.Foundation.Style)
        put(Route.Foundation.ContentColor.path, Route.Foundation.ContentColor)
        put(Route.Foundation.Modifier.path, Route.Foundation.Modifier)
        put(Route.Foundation.InteractionState.path, Route.Foundation.InteractionState)
    }
}

fun routeFromPath(path: String): Route? = routesByPath[path]

fun Route.section(): Section =
    when (this) {
        is Route.GettingStarted -> Section.GettingStarted
        is Route.Component -> Section.Components
        is Route.Foundation -> Section.Foundations
    }
