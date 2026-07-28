// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.container

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class StaticContainerModifierTest {
    @Test
    fun installsStaticStyleWithoutAnInteractionSourceElement() =
        runComposeUiTest {
            setContent {
                Container(
                    modifier = Modifier.testTag("static-container").size(48.dp),
                    color = Color.Red,
                ) {}
            }

            val elements =
                onNodeWithTag("static-container")
                    .fetchSemanticsNode()
                    .layoutInfo
                    .modifierInfos()
                    .flatMap { info -> info.modifier().elements() }
            val elementNames = elements.map { element -> element::class.simpleName }

            assertTrue(
                elementNames.containsAll(
                    listOf(
                        "StyleScopeParentElement",
                        "ScaleLayoutElement",
                        "BorderElement",
                        "BackgroundElement",
                        "ShapeLayoutElement",
                    ),
                ),
                elements.toString(),
            )

            assertTrue(
                elementNames.none { element -> element == "InteractionSourceElement" },
                elements.toString(),
            )
        }
}

private fun Modifier.elements(): List<Modifier.Element> =
    buildList {
        foldIn(Unit) { _, element -> add(element) }
    }

private fun Any.modifierInfos(): List<Any> =
    javaClass
        .getMethod("getModifierInfo")
        .invoke(this)
        .let { it as List<*> }
        .filterNotNull()

private fun Any.modifier(): Modifier = javaClass.getMethod("getModifier").invoke(this) as Modifier
