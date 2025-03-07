# Foundations

Foundational elements for building up your Design System

```kotlin
implementation("io.daio.wild.foundations:<version>")
```

Foundations are included with any component dependency but they can be used on their own.

Foundations mostly supply Modifiers to help interop with hardware input platforms like Android Tv
and standard Compose UI like mobile.

You can use both the `Modifier.clickable` and `Modifier.selectable` from Wild to automatically
detect when remote input is required by the platform (currently just Android Tv) and adjust the
clickable/selectable to ensure the Tv remote can navigate and click elements with the Modifier's
applied.

| Platform   | Available |
|------------|-----------|
| CMP        | ✅         |
| Android Tv | ✅         |

You can see the full api [here](https://daio-io.github.io/wild/reference/foundations/index.html)