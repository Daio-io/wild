# Style

Supplies Style classes for styles such as colors and borders to your components.

```kotlin
implementation("io.daio.wild.style:<version>")
```

## Usage

The main usage of Wild `Style` classes is to setup stateful styling that can change based on the
current focused, pressed, enabled states etc. This is particularly useful for platforms with
hardware input (like Tv remotes), where focus indication is required. However, the styling is not
constrained to this, you can decide to not set any styling for the certain aspects if not required
for that platform.

In order to build Style, you can use the `StyleDefaults` functions

```kotlin
val style = StyleDefaults.style(
    colors = StyleDefaults.colors(
        focusedBackgroundColor = Color.Red,
        focusedContentColor = Color.White,
    ),
    scale = StyleDefaults.scale(focusedScale = 1.2f),
)
```

### Modifiers

One of the main Modifiers provided by Wild is `Modifier.interactionStyle`. This Modifier has more
relevance for Tv developers to be able to setup Styles on components that will need to respond and
change based on the current `InteractionSource` state, things like Focus and Press.

`interactionStyle` has three overloads:

| Overload | When to use |
|----------|-------------|
| `interactionStyle(..., style: Style)` | Preferred for immutable `Style` values from `StyleDefaults` |
| `interactionStyle(..., block: StyleScope.() -> Unit)` | Advanced / custom resolution in a DSL |
| `interactionStyle(..., style: StyleSpec)` | Experimental reusable base `Style` + ordered overrides |

#### Style value overload

Pass a `Style` directly. Wild resolves colors, scale, alpha, shape, border, and
`scaleAnimationSpec` from that value for the current interaction and component state
(`enabled`, `selected`, focused, hovered, pressed).

Equal or unchanged `Style` values do not force a style update on recomposition—the modifier
compares by value (`Style` + `enabled` + `selected`), not by a freshly allocated lambda.

```kotlin
@Composable
fun MyComponent(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = StyleDefaults.style(),
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            onClick = onClick
        )
            .interactionStyle(style = style, interactionSource = interactionSource)
    ) {
        // Some content.
    }
}
```

#### StyleScope DSL overload

For custom logic, use the block overload. Inside the block you can read interaction flags and set
visual properties directly.

```kotlin
Modifier.interactionStyle(interactionSource = interactionSource) {
    color = if (focused) Color.Blue else Color.Red
    scale = if (pressed) 0.95f else 1f
}
```

`StyleScope.backgroundColor` is an alias for `color`, so older lambda migrations that assigned
`backgroundColor` continue to compile.

Clickable, selectable, and interactable also expose a required-but-nullable `styleBlock` overload
that installs the same block-based `interactionStyle` chain. Prefer named `styleBlock = { … }`
(or `styleBlock = null` for no style parent) when migrating from deprecated lambda helpers; keep
the existing `style: Style?` overload for value styles. Fully positional null call sites should
name `style =` or `styleBlock =` so overload resolution stays unambiguous. Deprecated
`experimentalClickable` / `experimentalSelectable` / `experimentalInteractable` /
`experimentalInteractionStyle` lambda `ReplaceWith` expressions target these current APIs
(`styleBlock = style` and `interactionStyle(..., block)`).

#### Experimental StyleSpec

`StyleSpec` is an `@ExperimentalWildApi` immutable definition: a base `Style` plus ordered
`ComponentStyleScope` callbacks. The style parent seeds chrome (and content color on the scope)
from the base tables for the current flags, then runs blocks **without** resetting between them.
Omitting a property keeps the base or an earlier write for that evaluation; the next evaluation
re-seeds from the base tables (omit does not carry prior evaluations).

```kotlin
@OptIn(ExperimentalWildApi::class)
val spec = styleSpec(StyleDefaults.style()) {
    if (focused) scale = 1.1f
}.then {
    if (pressed) alpha = 0.9f
}

Modifier.interactionStyle(interactionSource, style = spec)
// also: staticStyle(spec), clickable(..., style = spec), selectable(...), interactable(...)
```

Standalone Spec chrome modifiers (`interactionStyle` / `staticStyle` / input helpers) do **not**
publish content composition locals. Interactive component overloads on `Container`, `Button`,
`ListItem`, and `Toggleable`/`Selectable` that take `style: StyleSpec` **do** bridge resolved
content color (including Spec callback overrides) through an equality-gated publisher: content
recomposes only when the resolved color changes. Prefer a stable `StyleSpec` (hoisted /
remembered callbacks); there is no default `rememberStyleSpec` helper.

```kotlin
@OptIn(ExperimentalWildApi::class)
val cardStyle = styleSpec(base = ButtonDefaults.style()) {
    contentColor = if (enabled) Color.White else Color.Gray
    if (focused) scale = 1.08f
}.then {
    if (pressed) alpha = 0.9f
}

Button(onClick = onClick, style = cardStyle) {
    Text("Continue") // reads LocalContentColor from the Spec bridge
}
```

Capture theme values from composition before building the Spec; read live snapshot state inside
callbacks when deferred observation is intended. Keep callbacks pure and synchronous.

!!! note "StyleScope DSL reset semantics"
    When using the `StyleScope` block overload of `interactionStyle`, each evaluation resets visual
    properties to defaults before your block runs: `color = Color.Unspecified`, `alpha = 1f`,
    `scale = 1f`, `shape = RectangleShape`, no border, and `scaleAnimationSpec = null`. If you omit
    a property in a branch (for example only setting `color` when focused), the other properties
    resolve to these defaults for that invocation—they do not persist from a previous state.
    Interaction flags (`focused`, `hovered`, `pressed`, `selected`, `enabled`) are inputs available
    inside the block, not style outputs that get reset. Child nodes are updated when the resolved
    visual output or interaction inputs change; duplicate evaluations with identical resolved state
    are skipped. Snapshot state (e.g. `State` / `mutableStateOf`) read inside the block is
    observed, so when those values change the block is re-evaluated without requiring
    recomposition or an interaction event.

In order for the styles to work you must use the same `InteractionSource` when setting up
Wild `io.daio.wild.foundation.Modifier.clickable`, otherwise none of the events will be picked up to
change the style based on the state.

!!! note
    Having to ensure you share the same `InteractionSource` is an awkward part of the library
    right now which I am looking to solve with indication.

### Style modifier chain order

`interactionStyle` and `staticStyle` install a fixed internal chain (outer → inner):

1. **Interaction source** (interactive only) — observes the hoisted `InteractionSource`.
2. **Style parent** — resolves colors, scale, alpha, shape, and border for the current state.
3. **Scale** — draw-time scale and focus z-index; descendant layout coordinates stay unscaled.
4. **Border** — drawn after content so focus rings sit above the surface; positive `Border.inset`
   can extend outside the inner shape clip.
5. **Background** — fills behind content.
6. **Shape** — clips content and applies group alpha.

That order keeps scale wrapping the full chrome, leaves inset focus rings outside the clipped
surface, and applies clipping/alpha innermost. Prefer not to insert custom modifiers between these
layers; compose around the public style modifier instead.

### Composing modifiers around styled surfaces

Wild components apply the caller's `modifier` **outside** the style chain. When you call
`interactionStyle` / `staticStyle` yourself, put caller modifiers before or after that call
deliberately:

| Concern | Recommended placement |
|---------|------------------------|
| Size / fill / aspect ratio | Before style (or on the component `modifier`) so constraints size the whole surface |
| Outer spacing (margin-like) | Padding **before** style |
| Inset content spacing | Padding on content inside the surface (or after style if you own the chain) |
| Clickable / selectable | Before `interactionStyle`, sharing the same hoisted `InteractionSource` |
| Semantics / focus requester | With or before input modifiers, outside the style chain |
| `graphicsLayer` / clip | Before style only when you intend to transform border and scale together |
| Custom draw | Before style to draw under/around the chrome; after style only when you want drawing inside the clipped shape |

```kotlin
val interactionSource = remember { MutableInteractionSource() }

Box(
    modifier = Modifier
        .size(120.dp) // sizes the styled surface
        .clickable(
            interactionSource = interactionSource,
            onClick = onClick,
        )
        .interactionStyle(
            interactionSource = interactionSource,
            style = style,
        ),
) {
    Text(
        text = "Label",
        modifier = Modifier.padding(12.dp), // inset inside the chrome
    )
}
```

Hoist `InteractionSource` to the composable that owns both input and style. Prefer
`Modifier.clickable(..., style = style)` / component `style` parameters when you do not need a
custom chain — they share one source internally. For the StyleScope DSL overload, set every visual
property you care about on each evaluation; omitted properties reset to defaults and do not carry
over from the previous state.

| Platform   | Available |
|------------|-----------|
| CMP        | ✅         |
| Android Tv | ✅         |

You can see the full api [here](https://daio-io.github.io/wild/reference/style/index.html)
