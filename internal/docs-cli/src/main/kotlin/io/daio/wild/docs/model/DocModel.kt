// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.docs.model

import androidx.compose.runtime.Composable

enum class Platform(val displayName: String) {
    Common("Compose Multiplatform"),
    Android("Android"),
    AndroidTV("Android TV"),
    Desktop("Desktop"),
    Web("Web"),
    IOS("iOS"),
}

data class ComponentSample(
    val id: String,
    val sourceFile: String,
    val symbol: String,
    val content: @Composable () -> Unit,
)

data class ParameterGuidance(
    val name: String,
    val description: String,
)

data class Guidance(
    val recommended: Boolean,
    val description: String,
)

data class DocExample(
    val sample: ComponentSample,
    val title: String,
    val description: String,
)

data class ComponentDoc(
    val name: String,
    val symbol: String,
    val artifact: String,
    val importStatement: String,
    val category: String,
    val platforms: Set<Platform>,
    val summary: String,
    val keywords: List<String>,
    val parameters: List<ParameterGuidance>,
    val guidance: List<Guidance>,
    val examples: List<DocExample>,
)

fun componentSample(
    id: String,
    sourceFile: String,
    symbol: String,
    content: @Composable () -> Unit,
): ComponentSample =
    ComponentSample(
        id = id,
        sourceFile = sourceFile,
        symbol = symbol,
        content = content,
    )

fun componentDoc(
    name: String,
    symbol: String,
    artifact: String,
    importStatement: String,
    category: String,
    platforms: Set<Platform>,
    block: ComponentDocBuilder.() -> Unit,
): ComponentDoc =
    ComponentDocBuilder(
        name = name,
        symbol = symbol,
        artifact = artifact,
        importStatement = importStatement,
        category = category,
        platforms = platforms,
    ).apply(block).build()

class ComponentDocBuilder internal constructor(
    private val name: String,
    private val symbol: String,
    private val artifact: String,
    private val importStatement: String,
    private val category: String,
    private val platforms: Set<Platform>,
) {
    private var summary: String = ""
    private val keywords = mutableListOf<String>()
    private val parameters = mutableListOf<ParameterGuidance>()
    private val guidance = mutableListOf<Guidance>()
    private val examples = mutableListOf<DocExample>()

    fun summary(value: String) {
        summary = value
    }

    fun keywords(vararg values: String) {
        keywords += values
    }

    fun parameter(
        name: String,
        description: String,
    ) {
        parameters += ParameterGuidance(name, description)
    }

    fun guidance(block: GuidanceBuilder.() -> Unit) {
        guidance += GuidanceBuilder().apply(block).build()
    }

    fun example(
        sample: ComponentSample,
        title: String,
        description: String,
    ) {
        examples += DocExample(sample, title, description)
    }

    internal fun build(): ComponentDoc =
        ComponentDoc(
            name = name,
            symbol = symbol,
            artifact = artifact,
            importStatement = importStatement,
            category = category,
            platforms = platforms.toSet(),
            summary = summary,
            keywords = keywords.toList(),
            parameters = parameters.toList(),
            guidance = guidance.toList(),
            examples = examples.toList(),
        )
}

class GuidanceBuilder {
    private val entries = mutableListOf<Guidance>()

    fun doThis(description: String) {
        entries += Guidance(recommended = true, description = description)
    }

    fun avoid(description: String) {
        entries += Guidance(recommended = false, description = description)
    }

    internal fun build(): List<Guidance> = entries.toList()
}
