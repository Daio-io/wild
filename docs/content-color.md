# Content-Color

Supplies a `LocalContentColor` similar to Material.

```kotlin
implementation("io.daio.wild.content-color:<version>")
```

## Usage

Just like Material, Wild supplies a composition local, `LocalContentColor` as a way to supply the 
current content color for all child components within your Composables.

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

If you are using the `ProvidesContentColor`, and you also use any of the Material 
libs (Tv, Material3 or Material), this function will also set the `LocalContentColor` of those 
libraries too, ensuring you do not always need set the correct LocalContentColor if you also use
some Material components.

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

You can see the full api [here](https://todo.link)