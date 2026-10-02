# A2UI

## Overview

Wild includes support for rendering Agent-to-UI (A2UI) surfaces with Wild components.
The playbook gallery demonstrates protocol fixtures and dispatched user actions.

## Modules

Use the `a2ui` protocol model and processor, `a2ui-compose` rendering primitives,
and `a2ui-catalog` for the Wild basic component catalog.

## Protocol pin

Wild currently pins the A2UI fixtures and catalog format to protocol version `v0.9.1`.

## Quick start

```kotlin
val processor = A2uiMessageProcessor()
processor.processJsonl(fixture)
val catalog = wildA2uiBasicCatalogV1()
A2uiSurface(surface, catalog, processor)
```

## AndroidX note

AndroidX Compose A2UI is an Android-only reference implementation. It is not a
dependency of Wild; Wild provides its own multiplatform protocol, Compose surface,
and catalog modules.
