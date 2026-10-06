// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import androidx.compose.ui.semantics.SemanticsNode

actual fun SemanticsNode.styleScopeParentCount(): Int = error("styleScopeParentCount is only supported on JVM/Android screenshot hosts")
