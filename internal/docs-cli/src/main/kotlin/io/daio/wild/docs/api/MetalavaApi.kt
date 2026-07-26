// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.docs.api

data class ApiParameter(
    val name: String,
    val type: String,
    val optional: Boolean,
)

data class ApiFunction(
    val name: String,
    val parameters: List<ApiParameter>,
)

class MetalavaApi private constructor(
    private val functions: List<ApiFunction>,
) {
    fun function(name: String): ApiFunction? = functions.firstOrNull { it.name == name }

    companion object {
        fun parse(text: String): MetalavaApi =
            MetalavaApi(
                text
                    .lineSequence()
                    .map(String::trim)
                    .filter { it.startsWith("method ") }
                    .mapNotNull(::parseFunction)
                    .toList(),
            )

        private fun parseFunction(line: String): ApiFunction? {
            val parametersStart = line.indexOf('(')
            if (parametersStart < 0) return null

            val parametersEnd = line.lastIndexOf(");")
            if (parametersEnd < parametersStart) return null

            val functionName =
                line
                    .substring(0, parametersStart)
                    .substringAfterLast(' ')
            val parameters =
                splitParameters(line.substring(parametersStart + 1, parametersEnd))
                    .filter(String::isNotBlank)
                    .mapNotNull(::parseParameter)

            return ApiFunction(
                name = functionName,
                parameters = parameters,
            )
        }

        private fun splitParameters(parameters: String): List<String> {
            val result = mutableListOf<String>()
            var genericDepth = 0
            var start = 0

            parameters.forEachIndexed { index, character ->
                when (character) {
                    '<' -> genericDepth += 1
                    '>' -> genericDepth -= 1
                    ',' ->
                        if (genericDepth == 0) {
                            result += parameters.substring(start, index).trim()
                            start = index + 1
                        }
                }
            }

            result += parameters.substring(start).trim()
            return result
        }

        private fun parseParameter(value: String): ApiParameter? {
            val optional = value.startsWith("optional ")
            val declaration = value.removePrefix("optional ")
            val nameStart = declaration.lastIndexOf(' ')
            if (nameStart < 0) return null

            return ApiParameter(
                name = declaration.substring(nameStart + 1),
                type = declaration.substring(0, nameStart),
                optional = optional,
            )
        }
    }
}
