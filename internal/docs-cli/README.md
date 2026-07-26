# Agent-ready component documentation POC

This internal module tests an Astryx-style documentation workflow on Wild's
`Button` component. It is deliberately not published.

The canonical component description is Kotlin:

- `Button.doc.kt` sits beside `Button.kt`.
- `Button.samples.kt` contains a real, compiled Compose sample.
- the Button module excludes both sidecars from its published artifact;
- this module compiles the sidecars with a small documentation DSL;
- validation compares the documented symbol and parameters with Button's
  Metalava `api.txt`.

This gives authors IDE completion and compile-time checks for samples while
Metalava catches documentation drift when a public parameter is added, removed,
or renamed.

## Try the workflow

Search by component name or user intent:

```shell
./gradlew :internal:docs-cli:run --args="search press"
```

```text
Results for "press":
[component] Button
  An unstyled action primitive with click, long-click, double-click, focus, hover, press, and disabled-state support.
  → component Button
```

Ask for compact setup and API context:

```shell
./gradlew :internal:docs-cli:run --args="component Button --dense"
```

The result includes:

```text
artifact: io.daio.wild:button
import: import io.daio.wild.components.button.Button
platforms: Compose Multiplatform, Android, Android TV, Desktop, Web, iOS
parameters: onClick: kotlin.jvm.functions.Function0<kotlin.Unit> (required); ...
```

Use a stable JSON envelope when another tool or agent is the caller:

```shell
./gradlew :internal:docs-cli:run --args="search cta --json"
./gradlew :internal:docs-cli:run --args="component Button --json"
./gradlew :internal:docs-cli:run --args="validate --json"
```

Validation currently returns:

```json
{"type":"validate","data":{"valid":true,"issueCount":0,"issues":[]}}
```

Errors use stable codes such as `ERR_UNKNOWN_COMPONENT`,
`ERR_MISSING_QUERY`, and `ERR_UNKNOWN_COMMAND`.

## What this proves

One colocated source can supply:

1. human component guidance;
2. intent-oriented search metadata;
3. concise agent context;
4. structured JSON for future site, MCP, or IDE adapters;
5. compiled Compose examples; and
6. validation against Wild's tracked public API.

The catalog and search engine are shared Kotlin code. The CLI is only an
adapter, so future outputs do not need to scrape Markdown or reimplement
ranking.

## Deliberate POC boundaries

- Only `Button` is registered.
- The catalog is handwritten rather than generated.
- The CLI runs from the repository through Gradle.
- Parameter types retain Metalava's raw signature spelling rather than being
  normalized to Kotlin source syntax.
- Search uses deterministic exact and substring ranking; it has no fuzzy
  matching or synonyms yet.
- There is no published CLI, generated website, `llms.txt`, MCP server, or
  project-mutating setup command.

If the authoring experience and output shape are accepted, the follow-up plan
should decide catalog discovery/code generation, schema versioning, CLI
distribution, richer search, and which adapters to ship first.
