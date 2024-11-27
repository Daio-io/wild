<div align="center">
<img src="wild-logo-circle.svg" alt="Wild" />
</div>

A lightweight Compose primitive component and toolkit library for bootstrapping design systems
on CMP and Android TV 📺.

All components are unstyled, accessible, and modular, designed for direct use or as foundations in
design systems.

## Motivation

This library was created to streamline component sharing for design systems on Android Tv and other
Compose target platforms. Most design systems use Material as their base, but that comes with its
own styling and a lot of additional components that are not always needed.

Wild breaks down essential parts into modular, lightweight components. This allows teams to
include only what they need.

## The Platform Problem

Creating cross-platform component libraries often leads to duplicated code. While some duplication
is necessary for platform-specific requirements, foundational layers should be shareable. A
prominent example is how the Tv Material library adapts the Compose Material3 library, resulting in
duplicate classes and functions, making foundational modules difficult to share between CMP and
Android Tv.

| Class/Construct   | Material3                                    | Material3 TV                            |
|-------------------|----------------------------------------------|-----------------------------------------|
| LocalContentColor | androidx.compose.material3.LocalContentColor | androidx.tv.material3.LocalContentColor |
| Surface           | androidx.compose.material3.Surface           | androidx.tv.material3.Surface           |

For instance, `LocalContentColor` could be shared across libraries.
And `Surface` on TV is foundational component for all Tv components, enabling focus/click support
and visual states like focus and pressed. This coupling makes it challenging to create a universal
foundational layer for multi-platform design systems.

Wild addresses this by breaking components into isolated layers, allowing you to use complete
components or just essential functions and modifiers, enabling platform flexibility and scalability.
