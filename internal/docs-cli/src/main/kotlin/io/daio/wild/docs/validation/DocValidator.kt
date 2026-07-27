// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.docs.validation

import io.daio.wild.docs.model.ComponentDoc

enum class DocIssueCode {
    BLANK_SUMMARY,
    DUPLICATE_KEYWORD,
    DUPLICATE_PARAMETER,
}

data class DocIssue(
    val code: DocIssueCode,
    val message: String,
)

object DocValidator {
    fun validate(doc: ComponentDoc): List<DocIssue> =
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
        }

    private fun duplicateValues(values: List<String>): Set<String> =
        values
            .groupingBy(String::lowercase)
            .eachCount()
            .filterValues { it > 1 }
            .keys
}
