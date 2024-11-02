plugins {
    id("io.daio.compose")
    id("io.daio.android.library")
    id("io.daio.kotlin.multiplatform")
    id("io.daio.publish")
    alias(libs.plugins.dokka)
    alias(libs.plugins.metalava)
}

android {
    namespace = "io.daio.wild.content.color"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.foundation)
                compileOnly(compose.material)
                compileOnly(compose.material3)
            }
        }
        val androidMain by getting {
            dependencies {
                compileOnly(libs.tv.material)
            }
        }

        // nativeMain an jsMain require the compileOny libraries be included.
        val nativeMain by getting {
            dependencies {
                implementation(compose.material)
                implementation(compose.material3)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(compose.material)
                implementation(compose.material3)
            }
        }
    }
}

metalava {
    filename.set("api/api.txt")
}
