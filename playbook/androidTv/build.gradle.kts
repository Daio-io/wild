plugins {
    id("io.daio.compose")
    id("io.daio.android.application")
    id("io.daio.kotlin.android")
}

dependencies {
    implementation(projects.playbook.shared)
    implementation(compose.foundation)
    implementation(projects.tv.layout.container)
    implementation(projects.tv.components.button)
    implementation(projects.tv.common)
}

android {
    namespace = "io.daio.wild.playbook.tv"
}
