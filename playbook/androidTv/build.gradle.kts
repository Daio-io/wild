plugins {
    id("io.daio.compose")
    id("io.daio.android.application")
    id("io.daio.kotlin.android")
}

dependencies {
    implementation(project(":playbook:shared"))

    implementation(compose.foundation)
    implementation(projects.tv.layout.container)
    implementation(projects.tv.components.button)
    implementation(projects.tv.base)
    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("androidx.activity:activity-compose:1.8.2")
}

android {
    namespace = "io.daio.wild.tv"
}
