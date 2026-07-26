// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.docs.validation

import io.daio.wild.components.button.ButtonDoc
import io.daio.wild.docs.api.MetalavaApi
import io.daio.wild.docs.model.ParameterGuidance
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DocValidatorTest {
    private val api = MetalavaApi.parse(buttonApiText())

    @Test
    fun `Button documentation matches its tracked public API`() {
        assertEquals(emptyList(), DocValidator.validate(ButtonDoc, api))
    }

    @Test
    fun `reports documentation for parameters absent from the public API`() {
        val doc =
            ButtonDoc.copy(
                parameters =
                    ButtonDoc.parameters +
                        ParameterGuidance("missing", "This parameter does not exist."),
            )

        assertTrue(
            DocValidator
                .validate(doc, api)
                .any { it.code == DocIssueCode.UNKNOWN_PARAMETER },
        )
    }

    @Test
    fun `reports public parameters missing from the documentation`() {
        val doc =
            ButtonDoc.copy(
                parameters = ButtonDoc.parameters.filterNot { it.name == "onClick" },
            )

        assertTrue(
            DocValidator
                .validate(doc, api)
                .any { it.code == DocIssueCode.UNDOCUMENTED_PARAMETER },
        )
    }

    @Test
    fun `reports missing symbols and malformed semantic fields`() {
        val doc =
            ButtonDoc.copy(
                symbol = "io.daio.wild.components.button.Missing",
                summary = "",
                keywords = ButtonDoc.keywords + "button",
                parameters = ButtonDoc.parameters + ButtonDoc.parameters.first(),
            )
        val codes = DocValidator.validate(doc, api).map { it.code }.toSet()

        assertTrue(DocIssueCode.COMPONENT_NOT_FOUND in codes)
        assertTrue(DocIssueCode.BLANK_SUMMARY in codes)
        assertTrue(DocIssueCode.DUPLICATE_KEYWORD in codes)
        assertTrue(DocIssueCode.DUPLICATE_PARAMETER in codes)
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
}
