// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsNode

actual fun SemanticsNode.styleScopeParentCount(): Int {
    val infos =
        layoutInfo.javaClass
            .getMethod("getModifierInfo")
            .invoke(layoutInfo)
            .let { it as List<*> }
            .filterNotNull()
    return infos
        .asSequence()
        .flatMap { info ->
            (info.javaClass.getMethod("getModifier").invoke(info) as Modifier)
                .elements()
                .asSequence()
        }.count { element -> element::class.simpleName == "StyleScopeParentElement" }
}

private fun Modifier.elements(): List<Modifier.Element> =
    foldIn(mutableListOf()) { acc, element ->
        acc.add(element)
        acc
    }
