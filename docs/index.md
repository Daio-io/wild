# Wild Primitives

Lightweight Compose Primitive components library to help bootstrap your design system with CMP 
and Android Tv 📺.

All components are unstyled, accessible & modular to be used directly or as building
blocks for your design system.

## Motivation

I started this originally to help make it easier to share components for Design Systems with 
Android Tv and other Compose platforms. Most design systems are either built on top of Material, 
made completely from scratch or a mix of both.

Wild takes some of the main aspects of Material library and breaks it up into smaller
lighter weight modules so you only need to depend on what you need while also maintaining full
support for accessibility and states like focus. Making it a lighter weight choice to build your
design system from.

## The Platform Problem

Developing a design system component library cross platform can often lead to a lot of duplication.
Some of this is good and allows you to make flexible decisions for each platform as the requirements
change and evolve. However, a lot of the times the duplication comes from the foundational layer 
which could be shared.

A prime example today is the way Tv Material library works. Its currently a copy of Compose
Material3 with some adaptation to the behaviour to support focus and click events from Tv remotes.

Since it is a copy you find a lot of the same classes, functions and Composable UI exists in both,
so trying to create more foundational modules for your DS can get trickier when trying to share and
depend on material across CMP and Android Tv. Some examples:

| Class/Construct   | Material3                                    | Material3 Tv                            |
|-------------------|----------------------------------------------|-----------------------------------------|
| LocalContentColor | androidx.compose.material3.LocalContentColor | androidx.tv.material3.LocalContentColor |
| Surface           | androidx.compose.material3.Surface           | androidx.tv.material3.Surface           |

Take something like `LocalContentColor` from above, that's fairly lightweight, and could be shared
between both libraries.

`Surface` is a slightly different example. On Tv its the base of most components, enabling support
to become focusable/clickable from Tv remotes and by also aiding styling options based on focus,
pressed, focusedDisabled and so on. So that behaviour being tied to the Surface make it a lot 
harder to create a simple sharable foundational or primitive layer to build shared and platform 
specific components.

Wild is trying to solve these issues by breaking up each part into smaller isolated layers so
you can take full components or just the parts that matter to you, like the functions and modifiers
that enable the components to work on more platforms.
