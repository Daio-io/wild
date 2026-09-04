// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.docs.cli

import io.daio.wild.docs.catalog.WildCatalog
import java.io.PrintWriter
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val cli = WildDocsCli(WildCatalog.components)
    val exitCode =
        cli.run(
            args = args.toList(),
            out = PrintWriter(System.out, true),
            err = PrintWriter(System.err, true),
        )
    exitProcess(exitCode)
}
