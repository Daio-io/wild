# Agent-ready Component Docs POC Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Prove that a colocated Kotlin documentation sidecar can describe Wild's `Button`, compile a real Compose sample, validate its parameter guidance against Metalava's tracked API, and power deterministic human and JSON CLI discovery.

**Architecture:** `Button.doc.kt` and `Button.samples.kt` live beside `Button.kt` but are excluded from the published component compilation. An internal JVM CLI compiles those sidecars against the real Button artifact, validates authored parameter names against `components/button/api/api.txt`, and exposes a small catalog through `search`, `component`, and `validate` commands.

**Tech Stack:** Kotlin/JVM 2.3.0, Compose Multiplatform 1.10.3, Gradle application plugin, `kotlin.test`, Metalava API text.

## Global Constraints

- Cover only `io.daio.wild.components.button.Button`.
- Do not publish the CLI or documentation model.
- Do not add runtime dependencies to the Button artifact.
- Do not author parameter types, requiredness, or defaults in `Button.doc.kt`; derive them from Metalava.
- Keep search deterministic and offline.
- Support human, dense, and JSON CLI output without introducing a CLI or serialization dependency.
- Every sample referenced by the doc must be a compiled Kotlin value.

---

## File Structure

- `components/button/src/commonMain/kotlin/io/daio/wild/components/button/Button.doc.kt`: semantic Button documentation.
- `components/button/src/commonMain/kotlin/io/daio/wild/components/button/Button.samples.kt`: compiled Compose samples referenced by the doc.
- `components/button/build.gradle.kts`: excludes `*.doc.kt` and `*.samples.kt` from the runtime artifact.
- `internal/docs-cli/build.gradle.kts`: internal JVM application and sidecar source inclusion.
- `internal/docs-cli/src/main/kotlin/io/daio/wild/docs/model/DocModel.kt`: immutable documentation model and DSL.
- `internal/docs-cli/src/main/kotlin/io/daio/wild/docs/api/MetalavaApi.kt`: minimal Metalava function-signature reader.
- `internal/docs-cli/src/main/kotlin/io/daio/wild/docs/validation/DocValidator.kt`: semantic/API consistency validation.
- `internal/docs-cli/src/main/kotlin/io/daio/wild/docs/catalog/WildCatalog.kt`: one-component POC catalog.
- `internal/docs-cli/src/main/kotlin/io/daio/wild/docs/search/DocSearch.kt`: deterministic ranking.
- `internal/docs-cli/src/main/kotlin/io/daio/wild/docs/cli/WildDocsCli.kt`: command dispatch and output rendering.
- `internal/docs-cli/src/main/kotlin/io/daio/wild/docs/cli/Main.kt`: application entry point.
- `internal/docs-cli/src/test/kotlin/io/daio/wild/docs/`: model, validation, search, and CLI tests.
- `internal/docs-cli/README.md`: runnable POC scenarios and deliberate limitations.

### Task 1: Compile colocated documentation and samples outside the Button artifact

**Files:**
- Modify: `settings.gradle.kts`
- Modify: `components/button/build.gradle.kts`
- Create: `internal/docs-cli/build.gradle.kts`
- Create: `internal/docs-cli/src/test/kotlin/io/daio/wild/docs/model/DocModelTest.kt`
- Create: `internal/docs-cli/src/main/kotlin/io/daio/wild/docs/model/DocModel.kt`
- Create: `components/button/src/commonMain/kotlin/io/daio/wild/components/button/Button.samples.kt`
- Create: `components/button/src/commonMain/kotlin/io/daio/wild/components/button/Button.doc.kt`

**Interfaces:**
- Produces: `componentDoc(...)`, `ComponentDocBuilder`, `componentSample(...)`, `ComponentDoc`, `ComponentSample`, `ParameterGuidance`, `DocExample`, `Platform`.
- Produces: `ButtonDoc` and `BasicButtonSample`.

- [ ] **Step 1: Add the internal JVM module configuration**

Include `:internal:docs-cli`, exclude `**/*.doc.kt` and `**/*.samples.kt` from Button `commonMain`, and configure the internal application to compile its own sources plus those two sidecar patterns from Button's common source directory.

- [ ] **Step 2: Write the failing documentation-model test**

The test constructs a component doc with this API and asserts the resulting immutable model:

```kotlin
val sample = componentSample("button-basic", "Button.samples.kt", "BasicButtonSample") {}
val doc =
    componentDoc(
        name = "Button",
        symbol = "io.daio.wild.components.button.Button",
        artifact = "io.daio.wild:button",
        importStatement = "import io.daio.wild.components.button.Button",
        category = "Action",
        platforms = setOf(Platform.Common, Platform.Android, Platform.Desktop, Platform.Web),
    ) {
        summary("An interactive button primitive.")
        keywords("button", "action")
        parameter("onClick", "Invoked when the button is clicked.")
        example(sample, "Basic", "A button with text content.")
    }
```

- [ ] **Step 3: Run the test and verify RED**

Run:

```bash
./gradlew :internal:docs-cli:test --tests "io.daio.wild.docs.model.DocModelTest"
```

Expected: compilation fails because the documentation model and DSL do not exist.

- [ ] **Step 4: Implement the minimal documentation model**

Implement immutable data classes and builders sufficient for the test. `componentSample` accepts a compiled `@Composable () -> Unit`, ensuring sample code is compiled. The doc stores the sample's ID, source file, symbol, title, and description without storing source as an unvalidated string.

- [ ] **Step 5: Add the real Button sidecars**

`BasicButtonSample` renders the real `Button` with `BasicText("Continue")`. `ButtonDoc` authors its purpose, discovery keywords, platforms, best-practice guidance, all public parameter descriptions, and a reference to `BasicButtonSample`.

- [ ] **Step 6: Run the focused tests and Button compilation**

Run:

```bash
./gradlew :internal:docs-cli:test :components:button:compileKotlinJvm
```

Expected: PASS. The CLI compiles both sidecars; Button compilation excludes them.

### Task 2: Validate ButtonDoc against Button's Metalava API

**Files:**
- Create: `internal/docs-cli/src/test/kotlin/io/daio/wild/docs/api/MetalavaApiTest.kt`
- Create: `internal/docs-cli/src/test/kotlin/io/daio/wild/docs/validation/DocValidatorTest.kt`
- Create: `internal/docs-cli/src/main/kotlin/io/daio/wild/docs/api/MetalavaApi.kt`
- Create: `internal/docs-cli/src/main/kotlin/io/daio/wild/docs/validation/DocValidator.kt`

**Interfaces:**
- Consumes: `ComponentDoc`, `ButtonDoc`, and Metalava API text.
- Produces: `MetalavaApi.function(name): ApiFunction?`.
- Produces: `DocValidator.validate(doc, api): List<DocIssue>`.

- [ ] **Step 1: Write failing API parsing tests**

Assert that parsing `components/button/api/api.txt` finds `Button` and returns these parameter names in order:

```kotlin
listOf(
    "onClick",
    "modifier",
    "enabled",
    "onLongClick",
    "onDoubleClick",
    "style",
    "contentPadding",
    "interactionSource",
    "content",
)
```

Also assert `onClick` and `content` are required while `modifier` is optional.

- [ ] **Step 2: Run the parser test and verify RED**

Run:

```bash
./gradlew :internal:docs-cli:test --tests "io.daio.wild.docs.api.MetalavaApiTest"
```

Expected: compilation fails because `MetalavaApi` does not exist.

- [ ] **Step 3: Implement the minimal Metalava reader**

Read `method` records, locate the requested function, split parameters only at top-level commas while respecting generic angle brackets, and derive each parameter's name, type text, and `optional` marker.

- [ ] **Step 4: Write failing validator tests**

Assert:

- `ButtonDoc` has zero issues against Button's current API.
- A copied doc containing `parameter("missing", "...")` reports `UNKNOWN_PARAMETER`.
- A copied doc omitting `onClick` reports `UNDOCUMENTED_PARAMETER`.
- A doc whose symbol names a missing function reports `COMPONENT_NOT_FOUND`.
- Blank summaries, duplicate keywords, and duplicate parameter guidance are reported.

- [ ] **Step 5: Run the validator tests and verify RED**

Run:

```bash
./gradlew :internal:docs-cli:test --tests "io.daio.wild.docs.validation.DocValidatorTest"
```

Expected: compilation fails because `DocValidator` does not exist.

- [ ] **Step 6: Implement minimal validation**

Implement stable issue codes and messages. Validate the doc as authored, then compare its parameter-name set with the Metalava function parameter-name set in both directions.

- [ ] **Step 7: Run validation tests**

Run:

```bash
./gradlew :internal:docs-cli:test --tests "io.daio.wild.docs.api.MetalavaApiTest" --tests "io.daio.wild.docs.validation.DocValidatorTest"
```

Expected: PASS.

### Task 3: Demonstrate catalog search and agent-readable CLI output

**Files:**
- Create: `internal/docs-cli/src/test/kotlin/io/daio/wild/docs/search/DocSearchTest.kt`
- Create: `internal/docs-cli/src/test/kotlin/io/daio/wild/docs/cli/WildDocsCliTest.kt`
- Create: `internal/docs-cli/src/main/kotlin/io/daio/wild/docs/catalog/WildCatalog.kt`
- Create: `internal/docs-cli/src/main/kotlin/io/daio/wild/docs/search/DocSearch.kt`
- Create: `internal/docs-cli/src/main/kotlin/io/daio/wild/docs/cli/WildDocsCli.kt`
- Create: `internal/docs-cli/src/main/kotlin/io/daio/wild/docs/cli/Main.kt`
- Create: `internal/docs-cli/README.md`

**Interfaces:**
- Consumes: `ButtonDoc`, `MetalavaApi`, and `DocValidator`.
- Produces: `DocSearch.search(query): List<SearchResult>`.
- Produces: `WildDocsCli.run(args, out, err): Int`.
- Produces commands: `search <query>`, `component <name>`, and `validate`.
- Produces flags: `--json` and `--dense`.

- [ ] **Step 1: Write failing search tests**

Assert exact-name search ranks Button first, keyword searches for `press`, `cta`, and `tv` find Button, matching is case-insensitive, and an unrelated query returns no results.

- [ ] **Step 2: Run the search tests and verify RED**

Run:

```bash
./gradlew :internal:docs-cli:test --tests "io.daio.wild.docs.search.DocSearchTest"
```

Expected: compilation fails because `DocSearch` does not exist.

- [ ] **Step 3: Implement deterministic search**

Score exact name at 100, name containment at 80, exact keyword at 90, keyword containment at 70, and summary/category/platform mentions at 50. Sort by descending score then component name.

- [ ] **Step 4: Write failing CLI tests**

Assert:

- `search press` emits a human result and follow-up `component Button` command.
- `search press --json` emits a `{"type":"search",...}` envelope.
- `component Button --dense` includes artifact, import, platforms, and API-derived parameters.
- `component Missing --json` exits 1 with `ERR_UNKNOWN_COMPONENT`.
- `validate --json` exits 0 with zero issues for the real Button doc.

- [ ] **Step 5: Run the CLI tests and verify RED**

Run:

```bash
./gradlew :internal:docs-cli:test --tests "io.daio.wild.docs.cli.WildDocsCliTest"
```

Expected: compilation fails because `WildDocsCli` does not exist.

- [ ] **Step 6: Implement command dispatch and rendering**

Use injected `PrintWriter` outputs for testability. Keep stdout parseable in JSON mode, return stable exit codes, and escape JSON strings locally without adding serialization dependencies.

- [ ] **Step 7: Document and run the POC scenarios**

Document:

```bash
./gradlew :internal:docs-cli:run --args="search press"
./gradlew :internal:docs-cli:run --args="search tv --json"
./gradlew :internal:docs-cli:run --args="component Button --dense"
./gradlew :internal:docs-cli:run --args="validate --json"
```

Run all four and record representative output in the README.

- [ ] **Step 8: Run full verification**

Run:

```bash
./gradlew :internal:docs-cli:test :internal:docs-cli:run --args="validate --json"
./gradlew spotlessCheck detekt
./gradlew :components:button:metalavaCheckCompatibility
```

Expected: all commands exit 0.
