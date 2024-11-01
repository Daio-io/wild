# Content-Color

Supplies a `LocalContentColor` similar to Material.

```kotlin
implementation("io.daio.wild.content-color:<version>")
```

## Usage

Just like Material, Wild provides a `LocalContentColor` composition local as a way to supply the 
current content color for all child composables.

This api comes with a convenience `ProvidesContentColor` to wire up the colors within your 
components and also ensures interop with Material libraries.

```kotlin
MySurface(content: @Composable BoxScope.() -> Unit) {
    ProvidesContentColor(color = Color.White) {
        content()
    }
}
```

And then used like so: 

```kotlin
MySurface {
    MyIcon(tint = LocalContentColor.current) // Will be white
}
```

## Material Interop

If you are using the `ProvidesContentColor`, this function will also set the `LocalContentColor` of 
all material libraries (Tv, Material3 or Material) too, ensuring you do not always need set the 
correct LocalContentColor if you still use some Material components.

Taking from the example above

```kotlin
MySurface {
    // Material lib icon component.
    Icon() // Tint will still be white.
}
```

| Platform   | Available |
|------------|-----------|
| CMP        | ✅         |
| Android Tv | ✅         |

You can see the full api [here](https://daio-io.github.io/wild/reference/content-color/index.html)