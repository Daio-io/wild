package io.daio.wild.modifier

import androidx.compose.ui.Modifier

/**
 * Applies either the [ifTrueModifier] or the [ifFalseModifier] depending on the [condition].
 *
 * @param condition to evaluate which Modifier should be applied.
 * @param ifTrueModifier The Modifier to apply if the [condition] equals true.
 * @param ifFalseModifier The Modifier to apply if the [condition] equals false.
 *
 * @since 0.2.0
 */
fun Modifier.thenIf(
    condition: Boolean,
    ifTrueModifier: Modifier,
    ifFalseModifier: Modifier = Modifier,
): Modifier = then(if (condition) ifTrueModifier else ifFalseModifier)

/**
 * Applies either the [ifNotNullModifier] or the [ifNullModifier] depending on the [value].
 *
 * @param value the nullable value to supply with the Modifier.
 * @param ifNotNullModifier The Modifier to apply if the [value] is not null.
 * @param ifNullModifier The Modifier to apply if the [value] is null.
 *
 * @since 0.2.0
 */
fun <T> Modifier.thenIfNotNull(
    value: T?,
    ifNotNullModifier: (T) -> Modifier,
    ifNullModifier: Modifier = Modifier,
): Modifier = then(if (value != null) ifNotNullModifier(value) else ifNullModifier)
