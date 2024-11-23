// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.foundation

/**
 * Annotation marker that the Api is currently experimental and likely to change or potentially
 * removed.
 *
 * @since 0.3.0
 */
@RequiresOptIn("This API is experimental and may change or be removed.")
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalWildApi
