plugins {
    id("io.daio.android.library")
    id("io.daio.compose")
    id("io.daio.kotlin.multiplatform")
}

android {
    namespace = "io.daio.wild"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.foundation)
                api("io.github.qdsfdhvh:image-loader:1.7.8")
            }
        }

        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.5.1")
                api("androidx.core:core-ktx:1.9.0")
            }
        }

        val jsMain by getting

        val jvmMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
    }
}

java {
    toolchain {
        // Clamped due to some dependencies.
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}
