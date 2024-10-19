package io.daio.wild.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * CompositionLocal containing the preferred content color for a given position in the hierarchy.
 *
 * Defaults to Color. Black if no color has been explicitly set.
 *
 * Use with [ProvidesContentColor] to ensure all Material library LocalContentColor match.
 *
 * @since 0.2.0
 */
val LocalContentColor = compositionLocalOf { Color.Black }

/**
 * Sets [LocalContentColor] to the provided [color].
 *
 * This function also acts as a shim to set LocalContentColor from all Material libraries.
 *
 * @param color The color to set for the [LocalContentColor].
 * @param content Main composable content.
 *
 * @since 0.2.0
 */
@Composable
fun ProvidesContentColor(
    color: Color,
    content: @Composable () -> Unit,
) = CompositionLocalProvider(*getLocals(color), content = content)

private val materialLocals: List<ProvidableCompositionLocal<Color>>
    by lazy(LazyThreadSafetyMode.NONE) {
        buildList {
            getMaterialContentColorLocal()?.let(::add)
            getMaterial3ContentColorLocal()?.let(::add)
            addAll(getAdditionalContentColorLocals())
        }
    }

@Stable
private fun getLocals(color: Color): Array<ProvidedValue<Color>> {
    return materialLocals.map { it provides color }
        .toMutableList().apply { add(LocalContentColor provides color) }
        .toTypedArray()
}

internal expect fun getAdditionalContentColorLocals(): List<ProvidableCompositionLocal<Color>>

internal fun getMaterial3ContentColorLocal(): ProvidableCompositionLocal<Color>? =
    try {
        androidx.compose.material3.LocalContentColor
    } catch (t: Throwable) {
        null
    }

internal fun getMaterialContentColorLocal(): ProvidableCompositionLocal<Color>? =
    try {
        androidx.compose.material.LocalContentColor
    } catch (t: Throwable) {
        null
    }
