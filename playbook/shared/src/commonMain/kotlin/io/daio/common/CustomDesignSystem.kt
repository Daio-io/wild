package io.daio.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object Theme {
    val colors: DsColors
        @ReadOnlyComposable
        @Composable
        get() = LocalColors.current

    val shapes: DsShapes
        @ReadOnlyComposable
        @Composable
        get() = LocalShapes.current
}

@Immutable
data class DsColors(
    val background: Color,
    val container: Color,
    val onBackground: Color,
    val onContainer: Color,
    val interaction: Color,
    val onInteraction: Color,
)

@Immutable
data class DsShapes(
    val s: RoundedCornerShape,
    val m: RoundedCornerShape,
    val l: RoundedCornerShape,
)

internal val LocalShapes =
    staticCompositionLocalOf {
        DsShapes(
            s = RoundedCornerShape(4.dp),
            m = RoundedCornerShape(8.dp),
            l = RoundedCornerShape(16.dp),
        )
    }

internal val LocalColors =
    staticCompositionLocalOf {
        DsColors(
            background = Color.Black,
            container = Color.DarkGray,
            onBackground = Color.White,
            onContainer = Color.White,
            interaction = Color.White,
            onInteraction = Color.Black,
        )
    }

@Composable
fun DsTheme(
    themeColors: DsColors = Theme.colors,
    shapes: DsShapes = Theme.shapes,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalColors provides themeColors,
        LocalShapes provides shapes,
        content = content,
    )
}
