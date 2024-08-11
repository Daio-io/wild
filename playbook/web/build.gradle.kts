plugins {
    id("io.daio.compose")
    id("io.daio.kotlin.multiplatform")
}

kotlin {
    js {
        browser()
        binaries.executable()
    }

    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    sourceSets {
        commonMain.dependencies {
            implementation(project(":playbook:shared"))
            implementation(compose.components.resources)
        }

        jsMain.dependencies {
            implementation(compose.html.core)
            implementation("app.softwork:routing-compose:0.2.14-1.6.0-beta01")
        }
    }
}
