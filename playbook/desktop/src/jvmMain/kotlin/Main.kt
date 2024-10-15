import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.daio.common.CustomDesignSystemApp

fun main() =
    application {
        Window(onCloseRequest = ::exitApplication) {
            CustomDesignSystemApp()
        }
    }
