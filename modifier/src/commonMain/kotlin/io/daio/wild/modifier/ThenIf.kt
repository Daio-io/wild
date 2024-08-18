package io.daio.wild.modifier

import androidx.compose.ui.Modifier

fun Modifier.thenIf(
    condition: Boolean,
    ifTrueModifier: Modifier,
    ifFalseModifier: Modifier = Modifier,
): Modifier = then(if (condition) ifTrueModifier else ifFalseModifier)

fun <T> Modifier.thenIfNotNull(
    value: T?,
    ifNotNullModifier: (T) -> Modifier,
    ifNullModifier: Modifier = Modifier,
): Modifier = then(if (value != null) ifNotNullModifier(value) else ifNullModifier)
