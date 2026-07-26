// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.docs.validation

import io.daio.wild.docs.api.MetalavaApi
import io.daio.wild.docs.model.ComponentDoc

enum class DocIssueCode {
    COMPONENT_NOT_FOUND,
    UNKNOWN_PARAMETER,
    UNDOCUMENTED_PARAMETER,
    BLANK_SUMMARY,
    DUPLICATE_KEYWORD,
    DUPLICATE_PARAMETER,
}

data class DocIssue(
    val code: DocIssueCode,
    val message: String,
)

object DocValidator {
    fun validate(
        doc: ComponentDoc,
        api: MetalavaApi,
    ): List<DocIssue> =
        buildList {
            if (doc.summary.isBlank()) {
                add(DocIssue(DocIssueCode.BLANK_SUMMARY, "${doc.name} must have a summary."))
            }

            duplicateValues(doc.keywords).forEach { keyword ->
                add(
                    DocIssue(
                        DocIssueCode.DUPLICATE_KEYWORD,
                        "${doc.name} documents keyword '$keyword' more than once.",
                    ),
                )
            }

            val parameterNames = doc.parameters.map { it.name }
            duplicateValues(parameterNames).forEach { parameter ->
                add(
                    DocIssue(
                        DocIssueCode.DUPLICATE_PARAMETER,
                        "${doc.name} documents parameter '$parameter' more than once.",
                    ),
                )
            }

            val functionName = doc.symbol.substringAfterLast('.')
            val function = api.function(functionName)
            if (function == null) {
                add(
                    DocIssue(
                        DocIssueCode.COMPONENT_NOT_FOUND,
                        "No tracked public function matches '${doc.symbol}'.",
                    ),
                )
                return@buildList
            }

            val apiParameterNames = function.parameters.map { it.name }.toSet()
            parameterNames
                .filterNot(apiParameterNames::contains)
                .distinct()
                .forEach { parameter ->
                    add(
                        DocIssue(
                            DocIssueCode.UNKNOWN_PARAMETER,
                            "${doc.name} documents unknown parameter '$parameter'.",
                        ),
                    )
                }

            val documentedParameterNames = parameterNames.toSet()
            function.parameters
                .map { it.name }
                .filterNot(documentedParameterNames::contains)
                .forEach { parameter ->
                    add(
                        DocIssue(
                            DocIssueCode.UNDOCUMENTED_PARAMETER,
                            "${doc.name} does not document public parameter '$parameter'.",
                        ),
                    )
                }
        }

    private fun duplicateValues(values: List<String>): Set<String> =
        values
            .groupingBy(String::lowercase)
            .eachCount()
            .filterValues { it > 1 }
            .keys
}
