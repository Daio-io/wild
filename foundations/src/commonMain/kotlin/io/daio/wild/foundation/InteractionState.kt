// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.foundation

/**
 * Captures the current state of an interactive element.
 */
interface InteractionState {
    /**
     * Whether the element is currently focused.
     */
    val focused: Boolean

    /**
     * Whether the element is currently hovered.
     */
    val hovered: Boolean

    /**
     * Whether the element is currently pressed.
     */
    val pressed: Boolean

    /**
     * Whether the element is currently selected.
     */
    val selected: Boolean

    /**
     * Whether the element is currently enabled.
     */
    val enabled: Boolean
}
