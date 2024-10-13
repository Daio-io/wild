# Container

Simple building block layer to be used as the base of any static, clickable or selectable 
component. Mimics the support of a Material Surface to work on CMP and Tv.

```kotlin
implementation("io.daio.wild.tv:container:<version>")
```

## Standard

You can use a `Container` as a simple layer for grouping of components.

```kotlin
Container {
    MyComponents()
}
```

### Customisation

You can customise the `Container` by overriding the default styling.

```kotlin
Container(
    modifier = Modifier,
    style = StyleDefaults.style(
        colors = StyleDefaults.colors()
    ),
) {
    MyComponents()
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

## Clickable

If you want to make any `Container` an interactive element, you can do so by passing an 
`onClick` or `onLongClick` callback.

```kotlin
Container(
    onClick = {
        println("Clicked")
    }
) {
    MyComponents()
}
```

After passing in an `onClick`/`onLongClick` The `Container` will become a focusable and clickable 
target. This makes the `Container` a perfect building block for Buttons or larger clickable 
Composables that you may need in your design system.

## Selectable

A `SelectableContainer` offers another layer to support a selected state. It works the same way
as a `Container` that has been made clickable with the additional `selected` param available to
set the `Container` state to "selected".  

```kotlin
var selected by remember { mutableStateOf(false) }

SelectableContainer(
    selected = selected,
    onClick = {
        selected = !selected
    }
) {
    MyComponents()
}
```

Use this component to build up your own selectable Composables.

| Platform   | Available |
|------------|-----------|
| CMP        | ✅         |
| Android Tv | ✅         |

You can see the full api [here](https://todo.link)
