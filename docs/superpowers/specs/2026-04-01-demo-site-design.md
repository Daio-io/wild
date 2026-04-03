# Wild Demo Site — Design Spec

A Compose Multiplatform (WASM) documentation and demo site for the Wild primitives library, inspired by Radix UI's component documentation pattern.

## Goals

- Showcase all Wild components and foundations with live rendered demos
- Provide clear API documentation (props, usage, platform availability) for each module
- Demonstrate Wild's value proposition: unstyled primitives that you theme yourself
- The site itself is built with Wild primitives, serving as a real-world usage example

## Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Build target | WASM only | Better performance, Compose WASM support is mature |
| Navigation | Android Navigation 3 | Experimental web support, proper declarative navigation instead of hand-rolled routing |
| Layout | Top nav + sidebar + content | Top nav for sections, grouped collapsible sidebar for items, scrollable content area |
| Component demos | Live demos + code | Rendered Wild components with a docs theme applied, alongside Kotlin code snippets |
| Theme | Dark first, light toggle later | Dark is standard for dev tooling; toggle demonstrates Wild's theming capability |
| Component pages | Reusable framework | `ComponentPage` composable template — each page provides data, framework renders layout |

## Integration with Existing Playbook

The WASM entry point (`playbook/web/src/wasmJsMain/kotlin/Main.kt`) currently renders `CustomDesignSystemApp()`. Phase 1 replaces this with `SiteApp()` as the default. The existing `CustomDesignSystemApp` and `CustomDesignSystem` theme code remain in the shared module as reference examples — they demonstrate exactly the kind of custom design system a user would build on top of Wild.

## Architecture

### Navigation Model

Three-level navigation:

1. **Top nav** — Switches between major sections: Getting Started, Foundations, Components
2. **Sidebar** — Shows items within the active section, grouped by category with collapsible headers
3. **Content area** — Renders the selected page

Uses `androidx.navigation3` with:
- Route definitions as data classes/objects (type-safe)
- `NavDisplay` as the root navigation host
- `rememberNavBackStack()` for back stack management

### Sidebar Groups

**Components section:**
- Interactive: Button, Toggleable
- Display: Text, Icon
- Layout: Container, ListItem, Divider

**Foundations section:**
- Style
- Content Color
- Modifier
- Interaction State

### File Structure

```
playbook/shared/src/commonMain/kotlin/io/daio/wild/site/
├── SiteApp.kt                  # Root composable, NavDisplay host
├── theme/
│   ├── SiteTheme.kt            # Dark (and later light) theme
│   └── SiteColors.kt           # Color tokens
├── navigation/
│   ├── TopNav.kt               # Top navigation bar
│   ├── Sidebar.kt              # Collapsible grouped sidebar
│   └── Routes.kt               # Navigation 3 route definitions
├── pages/
│   ├── GettingStarted.kt
│   ├── components/
│   │   ├── ButtonPage.kt
│   │   ├── ContainerPage.kt
│   │   ├── TextPage.kt
│   │   ├── IconPage.kt
│   │   ├── ListItemPage.kt
│   │   ├── ToggleablePage.kt
│   │   └── DividerPage.kt
│   └── foundations/
│       ├── StylePage.kt
│       ├── ContentColorPage.kt
│       ├── ModifierPage.kt
│       └── InteractionStatePage.kt
└── components/                  # Reusable site UI pieces
    ├── ComponentPage.kt         # Component page framework/template
    ├── DemoSection.kt           # Live demo container
    ├── CodeBlock.kt             # Code snippet display
    └── PropsTable.kt            # API reference table
```

## Site Theme

Built using Wild's own primitives (Style, ContentColor, Container) to dogfood the library.

### Dark Theme (default)

- Background: Deep navy/charcoal (#0f1117)
- Surface/cards: Slightly lighter (#161b22)
- Accent: Purple (#a78bfa range) — consistent with existing Wild branding
- Text primary: White/near-white
- Text secondary: Muted gray
- Borders: Subtle dark gray (#2d333b)
- Code blocks: Darker background with syntax-colored text

### Light Theme (Phase 6)

Inverted palette — light backgrounds, dark text, same accent. Theme toggle swaps color token values via CompositionLocal, demonstrating Wild's theming in action.

### Theme Implementation

- `SiteColors` data class with all color tokens
- `SiteTheme` composable providing colors via CompositionLocal
- Typography scale (headings, body, code, labels) via Compose `TextStyle`
- Spacing scale for consistent padding/margins
- Uses Wild's `ProvidesContentColor` for content color inheritance

## Component Page Framework

A reusable `ComponentPage` composable that accepts structured data and renders all sections consistently.

### Data Model

```kotlin
data class ComponentPageData(
    val name: String,
    val description: String,
    val module: String,
    val demos: List<Demo>,
    val usage: String,
    val props: List<Prop>,
    val platforms: List<Platform>,
)

data class Demo(
    val title: String,
    val description: String,
    val content: @Composable () -> Unit,
)

data class Prop(
    val name: String,
    val type: String,
    val default: String? = null,
    val required: Boolean = false,
)

enum class Platform { Android, AndroidTV, Desktop, Web, iOS }
```

### Sections (rendered in order)

1. **Header** — Large title + description subtitle
2. **Live Demo** — Rendered Wild component(s) in a contained card. Multiple demos stacked or tabbed. Each has a label and the actual component with the site theme applied.
3. **Installation** — Gradle dependency code block: `implementation("io.daio.wild:<module>:<version>")`
4. **Usage** — Kotlin code block with basic example
5. **API Reference** — Table with columns: Param, Type, Default. Required params listed first.
6. **Platform Availability** — Row of platform badges/chips showing supported targets

Adding a new component page = defining `ComponentPageData` + writing demo composables. No layout duplication.

## Content Pages

### Getting Started

- What is Wild — one-paragraph pitch (unstyled, accessible primitives for Compose Multiplatform)
- Installation — Maven Central repo setup + module dependency examples
- Quick example — Build a themed button from primitive to styled, showing Wild's value proposition
- Links to Foundations and Components sections

### Foundations Pages

Same general structure as component pages but adapted:
- Interactive examples instead of component demos (e.g., Style page shows a Container responding to hover/press/focus states)
- Code examples for each foundation concept
- API reference for key composables/classes

## Phasing

Each phase is a separate PR.

### Phase 1: Site Shell
- Top nav with Wild logo and section links (Getting Started, Foundations, Components)
- Collapsible grouped sidebar
- Content area with scrolling
- Navigation 3 routing between pages
- Dark theme
- Placeholder content for all pages
- WASM build target wired up

### Phase 2: Component Page Framework
- `ComponentPage` composable template
- `DemoSection` — live demo container with card styling
- `CodeBlock` — code snippet display with monospace font
- `PropsTable` — API reference table
- Platform availability badges
- Test with a single placeholder component to validate the framework

### Phase 3: First Component Pages
- Button page (all 6 sections populated)
- Container page
- Text page
- Validates the framework works end-to-end with real Wild components

### Phase 4: Remaining Component Pages
- Icon page
- ListItem page
- Toggleable page
- Divider page

### Phase 5: Getting Started + Foundations
- Getting Started page with installation guide and quick example
- Style page with interactive state demos
- Content Color page
- Modifier page
- Interaction State page

### Phase 6: Light Theme + Search
- Theme toggle in top nav
- Light theme color tokens
- Search functionality in top nav
- Visual polish and refinements
