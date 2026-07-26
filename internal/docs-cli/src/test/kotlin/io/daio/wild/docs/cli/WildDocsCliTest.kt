// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.docs.cli

import io.daio.wild.docs.api.MetalavaApi
import io.daio.wild.docs.catalog.WildCatalog
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WildDocsCliTest {
    private val cli =
        WildDocsCli(
            docs = WildCatalog.components,
            api = MetalavaApi.parse(buttonApiText()),
        )

    @Test
    fun `search explains the next command`() {
        val result = run("search", "press")

        assertEquals(0, result.exitCode)
        assertTrue(result.output.contains("[component] Button"))
        assertTrue(result.output.contains("component Button"))
    }

    @Test
    fun `search emits deterministic machine-readable output`() {
        val result = run("search", "cta", "--json")

        assertEquals(0, result.exitCode)
        assertEquals(
            """{"type":"search","data":{"query":"cta","results":[""" +
                """{"type":"component","name":"Button","score":90,"command":"component Button"}]}}""",
            result.output.trim(),
        )
    }

    @Test
    fun `component dense view exposes setup and public parameters`() {
        val result = run("component", "Button", "--dense")

        assertEquals(0, result.exitCode)
        assertTrue(result.output.contains("io.daio.wild:button"))
        assertTrue(result.output.contains("import io.daio.wild.components.button.Button"))
        assertTrue(result.output.contains("onClick: kotlin.jvm.functions.Function0<kotlin.Unit> (required)"))
        assertTrue(result.output.contains("modifier: androidx.compose.ui.Modifier (optional)"))
    }

    @Test
    fun `component JSON exposes setup guidance and compiled sample metadata`() {
        val result = run("component", "Button", "--json")

        assertEquals(0, result.exitCode)
        assertTrue(result.output.startsWith("""{"type":"component","data":{"""))
        assertTrue(result.output.contains(""""artifact":"io.daio.wild:button""""))
        assertTrue(result.output.contains(""""required":true"""))
        assertTrue(result.output.contains(""""id":"button-basic""""))
    }

    @Test
    fun `validate returns a machine-readable success envelope`() {
        val result = run("validate", "--json")

        assertEquals(0, result.exitCode)
        assertEquals(
            """{"type":"validate","data":{"valid":true,"issueCount":0,"issues":[]}}""",
            result.output.trim(),
        )
    }

    @Test
    fun `unknown components return a stable error code`() {
        val result = run("component", "Missing", "--json")

        assertEquals(1, result.exitCode)
        assertEquals(
            """{"error":"No component named \"Missing\".","code":"ERR_UNKNOWN_COMPONENT"}""",
            result.error.trim(),
        )
    }

    @Test
    fun `help advertises the discovery workflow`() {
        val result = run("help")

        assertEquals(0, result.exitCode)
        assertTrue(result.output.contains("search <query>"))
        assertTrue(result.output.contains("component <name>"))
        assertTrue(result.output.contains("validate"))
        assertTrue(result.output.contains("--json"))
        assertTrue(result.output.contains("--dense"))
    }

    private fun run(vararg args: String): CliResult {
        val output = StringWriter()
        val error = StringWriter()
        val exitCode =
            cli.run(
                args = args.toList(),
                out = PrintWriter(output, true),
                err = PrintWriter(error, true),
            )
        return CliResult(exitCode, output.toString(), error.toString())
    }

    private fun buttonApiText(): String =
        Path
            .of(
                System.getProperty("wild.repoRoot"),
                "components",
                "button",
                "api",
                "api.txt",
            ).readText()

    private data class CliResult(
        val exitCode: Int,
        val output: String,
        val error: String,
    )
}
