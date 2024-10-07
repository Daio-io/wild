package io.daio.wild.foundation

import androidx.compose.runtime.Immutable

@Immutable
data class Style(
    val colors: Colors = ContainerDefaults.colors(),
    val borders: Borders = ContainerDefaults.borders(),
    val scale: Scale = ContainerDefaults.scale(),
    val shapes: Shapes = ContainerDefaults.shapes(),
    val alpha: Alpha = ContainerDefaults.alpha(),
)
