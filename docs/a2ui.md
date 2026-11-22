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

Create the processor and feed it JSONL messages outside composition. In the UI,
observe its surface models and render the one matching your surface ID. Handle
dispatched user actions with `onAction`. Custom catalog renderers can use
`A2uiComponent` for children so they share the surface's catalog and data scope.

```kotlin
val processor = A2uiMessageProcessor()
processor.processJsonl(fixture)
val catalog = wildA2uiBasicCatalogV1()
// Inside composition
val surfaces by processor.surfaces.collectAsState()
surfaces[surfaceId]?.let { surface ->
    A2uiSurface(surface, catalog, processor, onAction = onAction)
}
```

## AndroidX note

AndroidX Compose A2UI is an Android-only reference implementation. It is not a
dependency of Wild; Wild provides its own multiplatform protocol, Compose surface,
and catalog modules.
