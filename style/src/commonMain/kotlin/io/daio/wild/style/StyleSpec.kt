// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.ui.graphics.Color
import io.daio.wild.foundation.ExperimentalWildApi

/**
 * Experimental immutable style definition: a base [Style] plus ordered [ComponentStyleScope]
 * callbacks evaluated by the style parent without resetting between blocks.
 *
 * Equality compares [base] by value and callbacks by identity and order.
 *
 * Example:
 * ```
 * val spec = styleSpec(StyleDefaults.style()) {
 *     if (focused) scale = 1.1f
 * }.then {
 *     if (pressed) alpha = 0.9f
 * }
 * Modifier.interactionStyle(interactionSource, style = spec)
 * ```
 *
 * @param base Immutable style tables seeded before callbacks run.
 * @since 0.8.0
 */
@ExperimentalWildApi
class StyleSpec
    internal constructor(
        val base: Style,
        private val blocks: List<ComponentStyleScope.() -> Unit>,
    ) {
        /**
         * Returns a new [StyleSpec] that appends [block] after existing callbacks.
         *
         * @param block Additional override applied after earlier blocks in the same evaluation.
         * @since 0.8.0
         */
        fun then(block: ComponentStyleScope.() -> Unit): StyleSpec = StyleSpec(base, blocks + block)

        /**
         * Resolves content color for the given flags by seeding chrome and content color from
         * [base] tables then applying ordered Spec callbacks — the same path the style parent
         * uses before publishing.
         *
         * Component Spec overloads use this to seed the equality-gated bridge for the first frame.
         *
         * Example:
         * ```
         * val spec = styleSpec(StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Red))) {
         *     contentColor = color // derives from chrome seeded from base
         * }
         * val initial = spec.contentColorFor(enabled = true) // Color.Red
         * ```
         *
         * @param enabled Whether the component is enabled.
         * @param focused Whether the component is focused.
         * @param hovered Whether the component is hovered.
         * @param pressed Whether the component is pressed.
         * @param selected Whether the component is selected.
         * @return Spec-resolved content color for these flags.
         * @since 0.8.0
         */
        fun contentColorFor(
            enabled: Boolean,
            focused: Boolean = false,
            hovered: Boolean = false,
            pressed: Boolean = false,
            selected: Boolean = false,
        ): Color {
            val scope = DefaultComponentStyleScope()
            scope.updateState(
                enabled = enabled,
                focused = focused,
                selected = selected,
                pressed = pressed,
                hovered = hovered,
            )
            // Mirror StyleScopeParentElement.resolveSpec: seed chrome from base tables before
            // applyBlocks so callbacks that derive contentColor from color/alpha/… match the parent.
            scope.color =
                base.colors.colorFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            scope.alpha =
                base.alpha.alphaFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            scope.scale =
                base.scale.scaleFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            scope.shape =
                base.shapes.shapeFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            scope.border =
                base.borders.borderFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            scope.scaleAnimationSpec = base.scale.animationSpec
            scope.contentColor =
                base.colors.contentColorFor(
                    enabled = enabled,
                    focused = focused,
                    hovered = hovered,
                    pressed = pressed,
                    selected = selected,
                )
            applyBlocks(scope)
            return scope.contentColor
        }

        internal fun applyBlocks(scope: ComponentStyleScope) {
            for (block in blocks) {
                block(scope)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as StyleSpec

            if (base != other.base) return false
            if (blocks.size != other.blocks.size) return false
            return blocks.indices.all { index -> blocks[index] === other.blocks[index] }
        }

        override fun hashCode(): Int {
            var result = base.hashCode()
            for (block in blocks) {
                result = 31 * result + block.hashCode()
            }
            return result
        }
    }

/**
 * Creates a [StyleSpec] from a base [Style] and an initial override block.
 *
 * Example:
 * ```
 * val spec = styleSpec(StyleDefaults.style()) {
 *     if (focused) scale = 1.1f
 * }
 * Modifier.interactionStyle(interactionSource, style = spec)
 * ```
 *
 * @param base Immutable style tables seeded before [block] runs.
 * @param block First ordered override applied to [ComponentStyleScope].
 * @since 0.8.0
 */
@ExperimentalWildApi
fun styleSpec(
    base: Style,
    block: ComponentStyleScope.() -> Unit,
): StyleSpec = StyleSpec(base, listOf(block))
