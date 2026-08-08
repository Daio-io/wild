// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.docs.validation

import io.daio.wild.components.button.ButtonDoc
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DocValidatorTest {
    @Test
    fun `Button documentation passes semantic validation`() {
        assertEquals(emptyList(), DocValidator.validate(ButtonDoc))
    }

    @Test
    fun `reports malformed semantic fields`() {
        val doc =
            ButtonDoc.copy(
                summary = "",
                keywords = ButtonDoc.keywords + "button",
                parameters = ButtonDoc.parameters + ButtonDoc.parameters.first(),
            )
        val codes = DocValidator.validate(doc).map { it.code }.toSet()

        assertTrue(DocIssueCode.BLANK_SUMMARY in codes)
        assertTrue(DocIssueCode.DUPLICATE_KEYWORD in codes)
        assertTrue(DocIssueCode.DUPLICATE_PARAMETER in codes)
    }
}
