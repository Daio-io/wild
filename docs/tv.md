# Android Tv

If you have built for Android Tv, you are most likely very familiar with Tv Material libraries and
the challenges sharing any components or foundational pieces with other Compose Multiplatform
targets.

!!! TLDR
    - Tv Material is duplicated/fork of Material3 with Tv specifics added.
    - Material3 supports Compose Multiplatform, Tv Material does not.
    - Wild provides just the Modifiers and foundations you need to support Tv and Compose Multiplatform 🎉.

### Material Challenges

Tv Material is affectively a fork of Material3 with more Tv support added. Since a lot of the Tv
support is baked into Tv Material, with private modifiers inside `Surface` like `tvClickable` (
helping handle click
support), you can't easily interop foundational components.

This table illustrates some other constructs that are duplicated between material libraries.

| Class/Construct   | Material3                                    | Material3 TV                            |
|-------------------|----------------------------------------------|-----------------------------------------|
| LocalContentColor | androidx.compose.material3.LocalContentColor | androidx.tv.material3.LocalContentColor |
| Surface           | androidx.compose.material3.Surface           | androidx.tv.material3.Surface           |
| Text              | androidx.compose.material3.Text              | androidx.tv.material3.Text              |               |                                              |                                         |
| Icon              | androidx.compose.material3.Icon              | androidx.tv.material3.Icon              |  

### Wild

Wild pulls out a lot of the functionality and modifiers hidden within the Material Tv Surface and
makes it work multiplatform as part of [foundations](https://daio-io.github.io/wild/foundations/).
This allows you to build your own Surfaces/Containers that work cross platform handling click on
all form factors with a handle Wild version `Modifier.clickable`, handling interop for you.

Combined with the [content color](https://daio-io.github.io/wild/content-color/)
and [style](https://daio-io.github.io/wild/style/)
modules you can also build up your own styling and indication for interactions such as focused,
pressed, selected, that work on all platforms and form factors.

!!! note
    It is not fair to compare Wild directly with Material. Material is a Design System with more
    opinions. Wild is a primitive building blocks library leaving the opinions and components up
    to you, but its an important note for the motivation.

