// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.tooling.CompositionData
import androidx.compose.runtime.tooling.CompositionGroup

internal fun CompositionData.ownedInteractionSources(): List<MutableInteractionSource> =
    firstSourceOwnerGroup()?.allInteractionSources().orEmpty()

internal fun CompositionData.firstSourceOwnerHasDirectLayoutNode(): Boolean =
    firstSourceOwnerGroup()
        ?.compositionGroups
        ?.any { group -> group.data.any { value -> value?.let { it::class.simpleName } == "LayoutNode" } }
        ?: false

internal fun CompositionData.dump(depth: Int = 0): String =
    compositionGroups.joinToString(separator = "\n") { group ->
        "${"  ".repeat(depth)}${group.data.map { value -> value?.let { it::class.simpleName } }}\n${group.dump(depth + 1)}"
    }

private fun CompositionGroup.allInteractionSources(): List<MutableInteractionSource> =
    mutableListOf<MutableInteractionSource>().also(::collectInteractionSources)

private fun CompositionGroup.collectInteractionSources(sources: MutableList<MutableInteractionSource>) {
    data.filterIsInstance<MutableInteractionSource>().forEach { source ->
        if (sources.none { it === source }) sources += source
    }
    compositionGroups.forEach { group -> group.collectInteractionSources(sources) }
}

private fun CompositionData.firstSourceOwnerGroup(): CompositionGroup? =
    compositionGroups.firstNotNullOfOrNull { group ->
        group.takeIf {
            it.compositionGroups.any { child -> child.data.any { value -> value is MutableInteractionSource } }
        } ?: group.firstSourceOwnerGroup()
    }

private fun CompositionGroup.firstSourceOwnerGroup(): CompositionGroup? =
    compositionGroups.firstNotNullOfOrNull { group ->
        group.takeIf {
            it.compositionGroups.any { child -> child.data.any { value -> value is MutableInteractionSource } }
        } ?: group.firstSourceOwnerGroup()
    }
