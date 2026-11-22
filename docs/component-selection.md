# Choose a Wild component

Wild supplies Compose Multiplatform primitives and interaction semantics. Use
[Style](style.md) for the visuals shared by your product, including focused,
pressed, selected, and disabled states.

| Need | Wild API | Usage |
| --- | --- | --- |
| Run an explicit action | `Button` | Give the button a visible label or an accessible name. Use `ListItem` when the row itself opens or selects something. |
| Draw a custom panel or tile | `Container` | Use the static overload for chrome and content color. Supply a click or long-click callback to make an interactive surface focusable on TV. |
| Display a label or graphic | `Text`, `Icon` | Use `AnnotatedString` with `Text` for styled spans. Describe meaningful icons and pass a null description for decorative ones. |
| Enter or edit text | `TextField`, `TextArea` | Hoist `TextFieldState` and label the editable control. Use `TextField` with `TextFieldLineLimits.SingleLine` for one line or `TextArea` to set visible multiline limits. |
| Make independent selections | `Checkbox`, `TriStateCheckbox` | Use the tri-state variant for a partially selected group. The caller decides how a click changes that group. |
| Turn a setting on or off | `Switch` | Update the caller-owned Boolean from the callback and render the track and thumb in the content slot. |
| Choose one option | `RadioButton`, `RadioGroup` | Keep one selected option in caller state. Use a Row or Column inside the group to lay out its children. |
| Build a custom selection control | `Toggleable`, `Selectable` | Use `Toggleable` for a state change and `Selectable` for one item in a caller-managed selection. Give the control an accessible name and a role appropriate to its behavior. |
| Adjust a value or interval | `Slider`, `RangeSlider` | Supply the track and thumb visuals and feed updates back into the caller-owned value. Give the adjustable control a label and distinguish the two range thumbs. |
| Show task progress | `LinearProgressIndicator`, `CircularProgressIndicator` | Pass a normalized progress provider when the total is measurable. Use the overload without a provider while the total is unknown. |
| Separate groups visually | `HorizontalDivider`, `VerticalDivider` | Give the parent bounded width for a horizontal line or bounded height for a vertical line. Both inherit the local content color by default. |
| Render an A2UI payload | `A2uiSurface`, `A2uiComponent` | Start with a surface and its matching catalog. Render children inside that surface's scope. See [A2UI](a2ui.md). |

## Style and accessibility

Use the component's defaults as a starting point, and keep app theme values in
your app's design tokens. Apply `StyleDefaults.style` for state-dependent visuals
and use the component's `StyleSpec` factory when you need experimental ordered
overrides. For text and icon colors inside a styled surface, prefer the local
content color so the foreground can adapt to interaction states.
[Style](style.md) · [Content color](content-color.md)

## Migrate interactive containers

Replace `ExperimentalContainer` with the interactive `Container` overload. Use
named arguments for the existing callbacks, state, and `Style` so they go to the
intended parameters. See [Container](container.md) for static and selectable
examples.
