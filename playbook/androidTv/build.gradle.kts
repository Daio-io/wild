plugins {
    id("io.daio.compose")
    id("io.daio.android.application")
    id("io.daio.kotlin.android")
}

dependencies {
    implementation(projects.playbook.shared)
    implementation(compose.foundation)
    implementation(projects.layout.container)
    implementation(projects.components.button)
}

android {
    namespace = "io.daio.wild.playbook.tv"
}
