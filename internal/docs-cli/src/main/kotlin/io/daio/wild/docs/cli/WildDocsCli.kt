// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.docs.cli

import io.daio.wild.docs.model.ComponentDoc
import io.daio.wild.docs.search.DocSearch
import io.daio.wild.docs.validation.DocIssue
import io.daio.wild.docs.validation.DocValidator
import java.io.PrintWriter

class WildDocsCli(
    private val docs: List<ComponentDoc>,
) {
    fun run(
        args: List<String>,
        out: PrintWriter,
        err: PrintWriter,
    ): Int {
        val json = "--json" in args
        val dense = "--dense" in args
        val positional = args.filterNot { it == "--json" || it == "--dense" }

        return when (positional.firstOrNull() ?: "help") {
            "help", "--help", "-h" -> {
                printHelp(out)
                0
            }

            "search" -> search(positional.drop(1), json, out, err)
            "component" -> component(positional.drop(1), json, dense, out, err)
            "validate" -> validate(json, out)
            else ->
                fail(
                    message = "Unknown command '${positional.first()}'.",
                    code = "ERR_UNKNOWN_COMMAND",
                    json = json,
                    err = err,
                )
        }
    }

    private fun search(
        args: List<String>,
        json: Boolean,
        out: PrintWriter,
        err: PrintWriter,
    ): Int {
        val query = args.joinToString(" ").trim()
        if (query.isEmpty()) {
            return fail("Search requires a query.", "ERR_MISSING_QUERY", json, err)
        }

        val results = DocSearch(docs).search(query)
        if (json) {
            val encodedResults =
                results.joinToString(",") { result ->
                    """{"type":"component","name":${result.doc.name.json()},""" +
                        """"score":${result.score},"command":${"component ${result.doc.name}".json()}}"""
                }
            out.println(
                """{"type":"search","data":{"query":${query.json()},"results":[$encodedResults]}}""",
            )
            return 0
        }

        out.println("Results for \"$query\":")
        if (results.isEmpty()) {
            out.println("No matching components.")
        } else {
            results.forEach { result ->
                out.println("[component] ${result.doc.name}")
                out.println("  ${result.doc.summary}")
                out.println("  → component ${result.doc.name}")
            }
        }
        return 0
    }

    private fun component(
        args: List<String>,
        json: Boolean,
        dense: Boolean,
        out: PrintWriter,
        err: PrintWriter,
    ): Int {
        val name =
            args.firstOrNull()
                ?: return fail("Component requires a name.", "ERR_MISSING_COMPONENT", json, err)
        val doc =
            docs.firstOrNull { it.name.equals(name, ignoreCase = true) }
                ?: return fail("No component named \"$name\".", "ERR_UNKNOWN_COMPONENT", json, err)

        when {
            json -> out.println(componentJson(doc))
            dense -> printDenseComponent(doc, out)
            else -> printComponent(doc, out)
        }
        return 0
    }

    private fun validate(
        json: Boolean,
        out: PrintWriter,
    ): Int {
        val issues = docs.flatMap(DocValidator::validate)
        if (json) {
            val encodedIssues = issues.joinToString(",") { it.json() }
            out.println(
                """{"type":"validate","data":{"valid":${issues.isEmpty()},"issueCount":${issues.size},"issues":[$encodedIssues]}}""",
            )
        } else if (issues.isEmpty()) {
            out.println("Documentation is valid (${docs.size} component checked).")
        } else {
            out.println("Documentation has ${issues.size} issue(s):")
            issues.forEach { out.println("- ${it.code}: ${it.message}") }
        }
        return if (issues.isEmpty()) 0 else 1
    }

    private fun printComponent(
        doc: ComponentDoc,
        out: PrintWriter,
    ) {
        out.println(doc.name)
        out.println(doc.summary)
        out.println()
        out.println("Setup")
        out.println("  artifact: ${doc.artifact}")
        out.println("  import: ${doc.importStatement}")
        out.println("  platforms: ${doc.platforms.joinToString { it.displayName }}")
        out.println()
        out.println("Parameters")
        doc.parameters.forEach { parameter ->
            out.println("  ${parameter.name}: ${parameter.description}")
        }
        out.println()
        out.println("Guidance")
        doc.guidance.forEach { guidance ->
            out.println("  ${if (guidance.recommended) "Do" else "Avoid"}: ${guidance.description}")
        }
        out.println()
        out.println("Examples")
        doc.examples.forEach { example ->
            out.println(
                "  ${example.title}: ${example.description} " +
                    "(${example.sample.sourceFile}#${example.sample.symbol})",
            )
        }
    }

    private fun printDenseComponent(
        doc: ComponentDoc,
        out: PrintWriter,
    ) {
        out.println("${doc.name}: ${doc.summary}")
        out.println("artifact: ${doc.artifact}")
        out.println("import: ${doc.importStatement}")
        out.println("platforms: ${doc.platforms.joinToString { it.displayName }}")
        out.println(
            "parameters: " +
                doc.parameters.joinToString("; ") { parameter ->
                    "${parameter.name}: ${parameter.description}"
                },
        )
    }

    private fun componentJson(doc: ComponentDoc): String {
        val platforms = doc.platforms.joinToString(",") { it.displayName.json() }
        val keywords = doc.keywords.joinToString(",") { it.json() }
        val parameters =
            doc.parameters.joinToString(",") { parameter ->
                jsonObject(
                    "name" to parameter.name.json(),
                    "description" to parameter.description.json(),
                )
            }
        val guidance =
            doc.guidance.joinToString(",") {
                jsonObject(
                    "recommended" to it.recommended.toString(),
                    "description" to it.description.json(),
                )
            }
        val examples =
            doc.examples.joinToString(",") {
                jsonObject(
                    "id" to it.sample.id.json(),
                    "title" to it.title.json(),
                    "description" to it.description.json(),
                    "sourceFile" to it.sample.sourceFile.json(),
                    "symbol" to it.sample.symbol.json(),
                )
            }

        val data =
            jsonObject(
                "name" to doc.name.json(),
                "symbol" to doc.symbol.json(),
                "summary" to doc.summary.json(),
                "category" to doc.category.json(),
                "artifact" to doc.artifact.json(),
                "import" to doc.importStatement.json(),
                "platforms" to "[$platforms]",
                "keywords" to "[$keywords]",
                "parameters" to "[$parameters]",
                "guidance" to "[$guidance]",
                "examples" to "[$examples]",
            )
        return jsonObject(
            "type" to "component".json(),
            "data" to data,
        )
    }

    private fun printHelp(out: PrintWriter) {
        out.println("Wild component documentation")
        out.println()
        out.println("Commands:")
        out.println("  search <query>       Find components by name or intent")
        out.println("  component <name>     Show setup, parameters, guidance, and examples")
        out.println("  validate             Check authored docs for structural mistakes")
        out.println()
        out.println("Options:")
        out.println("  --json               Stable machine-readable output")
        out.println("  --dense              Compact component output for agent context")
    }

    private fun fail(
        message: String,
        code: String,
        json: Boolean,
        err: PrintWriter,
    ): Int {
        if (json) {
            err.println("""{"error":${message.json()},"code":${code.json()}}""")
        } else {
            err.println("Error: $message ($code)")
        }
        return 1
    }

    private fun DocIssue.json(): String = """{"code":${code.name.json()},"message":${message.json()}}"""

    private fun jsonObject(vararg fields: Pair<String, String>): String =
        fields.joinToString(separator = ",", prefix = "{", postfix = "}") { (name, value) ->
            "${name.json()}:$value"
        }

    private fun String.json(): String =
        buildString {
            append('"')
            this@json.forEach { character ->
                when (character) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(character)
                }
            }
            append('"')
        }
}
