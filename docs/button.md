# Button

Primitive unstyled Button component for Tv.

```kotlin
implementation("io.daio.wild.tv:button:<version>")
```

## Usage
```kotlin
Button(onClick = {
    println("Clicked")
}) {
    Text("Click Me!")
}

```

### Customisation

You can customise the `Button` by overriding the default options.

```kotlin
Button(
    modifier = Modifier,
    colors = ButtonDefaults.colors(),
    shapes = ButtonDefaults.shapes(),
    borders = ButtonDefaults.borders(),
    onClick = {
        println("Clicked")
    },
) {
    Text("Click Me!")
}
```

All customisation options support state based values for focus, pressed etc.

```kotlin
ButtonDefaults.colors(
    color = Color.Blue,
    focusedColor = Color.Red,
    pressedColor = Color.Green,
    disabledColor = Color.Gray.copy(alpha = .6f),
    focusedDisabledColor = Color.Red.copy(alpha = .6f),
)
```