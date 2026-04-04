// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

// VS Code Dark+ inspired palette — readable on #0D1117 background
private val colorKeyword = Color(0xFF569CD6) // blue
private val colorString = Color(0xFFCE9178) // orange
private val colorComment = Color(0xFF6A9955) // green
private val colorAnnotation = Color(0xFF9CDCFE) // light blue
private val colorNumber = Color(0xFFB5CEA8) // light green
private val colorDefault = Color(0xFFD4D4D4) // near-white
private val colorFunction = Color(0xFFDCDCAA) // yellow
private val colorType = Color(0xFF4EC9B0) // teal

private val kotlinKeywordPattern =
    Regex(
        "\\b(fun|val|var|class|object|interface|enum|sealed|data|abstract|open|override|" +
            "private|public|protected|internal|companion|suspend|inline|operator|when|if|else|" +
            "for|while|do|return|import|package|by|is|as|in|out|null|true|false|this|super|" +
            "typealias|init|constructor|get|set|lateinit|expect|actual|reified|vararg)\\b",
    )

private val gradleKeywordPattern =
    Regex(
        "\\b(implementation|api|testImplementation|plugins|id|version|kotlin|android|jvm|wasm|js|" +
            "dependencies|apply|plugin)\\b",
    )

private data class TokenRule(
    val regex: Regex,
    val color: Color,
    val bold: Boolean = false,
)

// Order matters: comments and strings must come before keywords/types so that
// tokens inside literals are not incorrectly highlighted.
private val rules =
    listOf(
        // 1. Comments & strings first — prevents keyword highlighting inside literals
        TokenRule(Regex("/\\*[\\s\\S]*?\\*/"), colorComment),
        TokenRule(Regex("//[^\n]*"), colorComment),
        TokenRule(Regex("\"\"\"[\\s\\S]*?\"\"\""), colorString),
        TokenRule(Regex("\"(?:[^\"\\\\]|\\\\.)*\""), colorString),
        // 2. Annotations, numbers
        TokenRule(Regex("@[A-Za-z][A-Za-z0-9_]*"), colorAnnotation),
        TokenRule(Regex("\\b\\d+\\.?\\d*[fFLdD]?\\b"), colorNumber),
        // 3. Identifiers & keywords last
        TokenRule(Regex("\\b[a-z][A-Za-z0-9_]*(?=\\s*\\()"), colorFunction),
        TokenRule(Regex("\\b[A-Z][A-Za-z0-9_]*\\b"), colorType),
        TokenRule(kotlinKeywordPattern, colorKeyword, bold = true),
        TokenRule(gradleKeywordPattern, colorKeyword, bold = true),
    )

/**
 * Returns an [AnnotatedString] with Kotlin/Gradle syntax coloring applied.
 *
 * Pre-computes all matches per rule once, then merges them in document order.
 * This avoids repeated [Regex.find] calls at every character position.
 */
fun highlightCode(code: String): AnnotatedString {
    // Collect all matches across all rules, sorted by start position (earliest first),
    // then by rule order (earlier rules win ties — comments/strings before keywords).
    val allMatches =
        buildList {
            for ((ruleIndex, rule) in rules.withIndex()) {
                var match = rule.regex.find(code)
                while (match != null) {
                    add(Triple(match, rule, ruleIndex))
                    match = match.next()
                }
            }
        }.sortedWith(compareBy({ it.first.range.first }, { it.third }))

    return buildAnnotatedString {
        var pos = 0
        for ((match, rule, _) in allMatches) {
            // Skip matches that overlap with an already-consumed region
            if (match.range.first < pos) continue

            // Append unhighlighted text before this match
            if (match.range.first > pos) {
                withStyle(SpanStyle(color = colorDefault)) {
                    append(code.substring(pos, match.range.first))
                }
            }

            withStyle(
                SpanStyle(
                    color = rule.color,
                    fontWeight = if (rule.bold) FontWeight.SemiBold else null,
                ),
            ) {
                append(match.value)
            }

            pos = match.range.last + 1
        }

        // Append any remaining text after the last match
        if (pos < code.length) {
            withStyle(SpanStyle(color = colorDefault)) { append(code.substring(pos)) }
        }
    }
}
