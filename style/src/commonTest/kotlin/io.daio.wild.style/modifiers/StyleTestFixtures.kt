// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import io.daio.wild.style.Border
import io.daio.wild.style.StyleScope

internal data class SnapshotStyleBundle(
    val alpha: Float,
    val shape: Shape,
    val border: Border,
    val scale: Float,
    val scaleAnimationSpec: AnimationSpec<Float>?,
)

internal data class StyleSnapshot(
    val enabled: Boolean,
    val selected: Boolean,
    val focused: Boolean,
    val hovered: Boolean,
    val pressed: Boolean,
    val color: Color,
    val alpha: Float,
    val scale: Float,
    val shape: Shape,
    val border: Border,
    val scaleAnimationSpec: AnimationSpec<Float>?,
)

internal class StyleRecorder {
    val snapshots = mutableListOf<StyleSnapshot>()
    val colors: List<Color>
        get() = snapshots.map { it.color }
    val last: StyleSnapshot
        get() = snapshots.last()
}

internal fun Modifier.recordStyle(recorder: StyleRecorder): Modifier = this then RecordingStyleChildElement(recorder)

private data class RecordingStyleChildElement(
    val recorder: StyleRecorder,
) : ModifierNodeElement<RecordingStyleChildNode>() {
    override fun create() = RecordingStyleChildNode(recorder)

    override fun update(node: RecordingStyleChildNode) {
        node.recorder = recorder
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "RecordingStyleChildElement"
    }
}

private class RecordingStyleChildNode(
    var recorder: StyleRecorder,
) : Modifier.Node(), StyleScopeChildNode {
    override fun onAttach() {
        requestInitialStyleFromParent()
    }

    override fun onDetach() {
        recorder.snapshots.clear()
    }

    override fun onReset() {
        recorder.snapshots.clear()
    }

    override fun updateStyle(styleScope: StyleScope) {
        recorder.snapshots +=
            StyleSnapshot(
                enabled = styleScope.enabled,
                selected = styleScope.selected,
                focused = styleScope.focused,
                hovered = styleScope.hovered,
                pressed = styleScope.pressed,
                color = styleScope.color,
                alpha = styleScope.alpha,
                scale = styleScope.scale,
                shape = styleScope.shape,
                border = styleScope.border,
                scaleAnimationSpec = styleScope.scaleAnimationSpec,
            )
    }
}
