// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import androidx.navigation.compose.rememberNavController
import io.daio.wild.site.SiteApp
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        val navController = rememberNavController()
        // LaunchedEffect runs after composition so NavHost inside SiteApp is
        // already registered before bindToBrowserNavigation is called.
        LaunchedEffect(navController) {
            navController.bindToBrowserNavigation()
        }
        SiteApp(navController = navController)
    }
}
