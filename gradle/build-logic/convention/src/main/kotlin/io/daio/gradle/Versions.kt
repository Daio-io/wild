package io.daio.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

object Versions {
    const val COMPILE_SDK = 36
    const val MIN_SDK = 23
    const val TARGET_SDK = 36
    const val JAVA_VERSION = 21
}

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")
