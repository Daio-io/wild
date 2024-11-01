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
        focusBackgroundColor = Color.Red, focusContentColor = Color.White,
        scale = StyleDefaults.scale(focusScale = 1.2f),
    )
)
```

### Modifiers

One of the main Modifiers provided by Wild is `Modifier.interactionStyle`. This Modifier has more
relevance for Tv developers to be able to setup Styles on components that will need to respond and
change based on the current `InteractionSource` state, things like Focus and Press.

Example

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

In order for the styles to work you must use the same `InteractionSource` when setting up
Wild `io.daio.wild.foundation.Modifier.clickable`, otherwise non of the events will be picked up to
change the style based on the state.

!!! note
    Having to ensure you share the same `InteractionSource` is is an awkward part of the library
    right now which I am looking to solve with indication.

| Platform   | Available |
|------------|-----------|
| CMP        | ✅         |
| Android Tv | ✅         |

You can see the full api [here](https://daio-io.github.io/wild/reference/style/index.html)