// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import io.daio.wild.style.Style
import io.daio.wild.style.StyleScope

internal sealed interface StyleResolver {
    data class Value(val style: Style) : StyleResolver

    class Block(val block: StyleScope.() -> Unit) : StyleResolver {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Block) return false
            return block === other.block
        }

        override fun hashCode(): Int = block.hashCode()
    }
}
