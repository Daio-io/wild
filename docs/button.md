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
    style = StyleDefaults.style(),
    onClick = {
        println("Clicked")
    },
) {
    Text("Click Me!")
}
```

All customisation options support state based values for focus, pressed etc.

```kotlin
StyleDefaults.colors(
    backgroundColor = Color.Blue,
    contentColor = Color.White,
    focusedBackgroundColor = Color.Red,
    pressedBackgroundColor = Color.Green,
    disabledBackgroundColor = Color.Gray.copy(alpha = .6f),
    focusedDisabledBackgroundColor = Color.Red.copy(alpha = .6f),
)
```

| Platform   | Available |
|------------|-----------|
| CMP        | ✅         |
| Android Tv | ✅         |

You can see the full api [here](https://todo.link)
