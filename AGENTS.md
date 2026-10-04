# Wild codebase guide

Wild is a Kotlin Multiplatform design system built with Compose Multiplatform.

## Find the relevant code

| Change | Start here |
| --- | --- |
| Shared utilities, interactions | `foundations/` |
| Styles, content colors, modifiers | `style/`, `content-color/`, `modifier/` |
| Button, icon, text/input, toggleable, list item, progress, slider | `components/<name>/` |
| Container, divider | `layout/<name>/` |
| A2UI protocol and processing | `a2ui/` |
| A2UI Compose rendering and bindings | `a2ui-compose/` |
| Wild A2UI catalog and shared gallery fixtures | `a2ui-catalog/` |
| Gallery pages, routes, sidebar | `playbook/web/src/commonMain/kotlin/io/daio/wild/site/` |
| Demo dependency wiring | `playbook/shared/build.gradle.kts` |
| Screenshot harness | `internal/screenshot-tests/` |
| Benchmarks | `internal/benchmark/` |
| Gradle conventions | `gradle/build-logic/convention/src/main/kotlin/io/daio/gradle/` |
| CI jobs | `.github/workflows/` |

For a gallery change, public API change, or PR review, read the relevant section of
[Contributing](docs/contributing.md) for the exact files and commands. For screenshot
changes or failures, read [Screenshot testing](docs/screenshot-testing.md).

Search within the owning module first. Use `rg --files <module>/src` to locate a
symbol's source file; packages vary (for example, container uses `io.daio.wild.container`).

## Authoritative configuration

Read these when the task depends on versions, targets, modules, or dependencies:

- [settings.gradle.kts](settings.gradle.kts): included projects and Gradle paths.
- [gradle/libs.versions.toml](gradle/libs.versions.toml): dependency and plugin versions;
  the Compose compiler plugin uses the Kotlin version.
- [Versions.kt](gradle/build-logic/convention/src/main/kotlin/io/daio/gradle/Versions.kt): Android SDK levels.
- [Java.kt](gradle/build-logic/convention/src/main/kotlin/io/daio/gradle/Java.kt) and
  [Kotlin.kt](gradle/build-logic/convention/src/main/kotlin/io/daio/gradle/Kotlin.kt): toolchain and bytecode targets.
- [KotlinMultiplatformConventionPlugin.kt](gradle/build-logic/convention/src/main/kotlin/io/daio/gradle/KotlinMultiplatformConventionPlugin.kt): default platform targets.
- The owning module's `build.gradle.kts`: dependency edges, source sets, publishing,
  and applied plugins. Read it before assuming a module publishes an artifact.

Plans under `docs/plans/archive/` and `docs/superpowers/` describe earlier designs.
Use the current source and task's acceptance criteria to establish today's behavior.

## Code conventions

- Follow the owning module's package and existing expect/actual patterns.
- Kotlin source header: `// Copyright 2024, Dai Williams` followed by
  `// SPDX-License-Identifier: Apache-2.0`.
- Public APIs need KDoc, applicable `@param` tags, `@since`, and a usage example.
- Modules applying Metalava track their public surface in `api/api.txt`; generate
  and review it when changing public declarations (see Contributing).
- Shared logic belongs in `commonMain`; tests use `commonTest` or the relevant
  platform test source set. Android library screenshot adapters use `androidUnitTest`.

## Verify the change

Run the owning module's checks first, such as `./gradlew :style:jvmTest` or
`./gradlew :a2ui-catalog:jvmTest --tests "io.daio.wild.a2ui.catalog.A2uiFixturesTest"`.
Use `./gradlew spotlessApply` for formatting. The main CI quality command is
`./gradlew spotlessCheck detekt lint jvmTest`.

Any user-visible component or style change must update or add focused screenshot
coverage in the same PR. The commands and maintenance contract live in
[docs/screenshot-testing.md](docs/screenshot-testing.md).

Run a demo with `./gradlew :playbook:desktop:run`,
`:playbook:web:jsBrowserRun`, or `:playbook:web:wasmJsBrowserRun`.
Android apps use `:playbook:android:installDebug` or `:playbook:androidTv:installDebug`.
Generate API docs with `./gradlew dokkaGenerate`; preview MkDocs with `mkdocs serve`.

## Git and Pull Requests

- Use focused branches and PRs; keep unrelated local changes out of commits.
- Use conventional commit messages such as `feat:`, `fix:`, `docs:`, `ci:`, `test:`, and `chore:`.
- Prefer small commits that explain the intent of the change.
- Always commit as the git config `user.name`/`user.email`.
- No AI or tool attribution anywhere in git history. Commits and PR titles must read as human-authored work only.
- Do not add `Co-authored-by`, `Signed-off-by`, or any other trailer that credits an AI assistant, agent, or coding tool (Codex, Cursor, Claude, Copilot, etc.).
- Do not prefix commit messages or PR titles with tool markers such as `[codex]`, `[cursor]`, `[claude]`, or similar tags.
- Do not mention which tool drafted, reviewed, or generated the change in commit bodies, PR descriptions, or squash-merge messages unless a maintainer explicitly asks for that context outside git metadata.
- If a tool auto-inserts attribution, remove it before committing or opening a PR. Squash merges must not reintroduce attribution from branch commits.
