package io.daio.wild.content

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.graphics.Color

private fun getTvContentColorLocal(): ProvidableCompositionLocal<Color>? =
    try {
        androidx.tv.material3.LocalContentColor
    } catch (t: Throwable) {
        null
    }

internal actual fun getAdditionalContentColorLocals(): List<ProvidableCompositionLocal<Color>> {
    val tvColorLocal = getTvContentColorLocal()
    return if (tvColorLocal != null) listOf(tvColorLocal) else emptyList()
}
