# Prepare Wild documentation for Wax

Use this guide when documenting a public Compose component or reviewing Wild's
publisher registry during onboarding. [Wax registry reference](https://github.com/Daio-io/wax/blob/main/website/docs/reference/registry.md)

## Write KDoc for component lookup

- Start with what the component does and when a caller should choose it.
  Describe state ownership, accessibility, layout constraints, and distinct
  overloads where they affect usage.
- Write one `@param name` for each declaration parameter. Use the spelling in
  Kotlin and cover the meaning of null, ranges, units, slot behavior, and when
  callbacks fire. The pack extracts parameter types, requiredness, defaults and
  locally resolved enum choices from the signature.
- Include one fenced `kotlin` example per declaration. Show the Wild call with
  real parameter names. Put example code inline so it is included in offline
  lookup. Keep Dokka `@sample` references when useful.
- Put each actionable rule on its own line starting with `@guidance`. Wax
  copies that line into `details.best_practices`. Choose rules that help
  callers select, compose, or label the component.
- Keep `@since` for API readers. Record deprecation in Kotlin with
  `@Deprecated` and `ReplaceWith` when available, and describe the migration
  in KDoc with `@deprecated`. Verify the extracted message and replacement.
  If the replacement is missing, record the reviewed replacement entry ID
  in the publisher registry.

The Compose pack discovers public top-level composables and extracts one
representative declaration per symbol within a file. Make that declaration's
KDoc useful on its own. Keep overload-specific examples in their own KDoc and
describe significant alternatives in the representative documentation or in an
associated Markdown guide.

## Preview discovery

From Wild's repository root, with the Compose language pack installed:

```bash
roots=()
for dir in components/*/src/commonMain/kotlin layout/*/src/commonMain/kotlin \
  content-color/src/commonMain/kotlin a2ui-compose/src/commonMain/kotlin; do
  roots+=(--root "$dir")
done

wax registry discover --language compose --design-system wild --name Wild \
  --repo-root "$PWD" --dry-run --force "${roots[@]}" > /tmp/wild-compose.registry.json
```

The command uses Bash or Zsh and previews the full component set. With Wax
`0.10.3-alpha2`, an unseeded dry run needs `--force` to enumerate Compose symbols.
Keep `--dry-run` when using `--force` for review because a replacement write can
prune entries. Compare the output with the maintained registry during
rediscovery, including package identity, source evidence, extraction status,
field gaps, parameters, examples, guidance, and deprecations. Review providers,
wrappers, and deprecated entries before deciding which entries to publish.

## Supply publisher metadata

Use stable registry IDs and the exact Kotlin package and symbol. Put search
vocabulary and groupings in `metadata.description` and `metadata.tags`. For
example, describe `TextArea` as a multiline input and tag it `input`. The
Compose scanner also resolves component aliases as symbols, so reserve
`aliases` for verified source identifiers.

Associate focused Markdown guides or examples through `metadata.sources`.
Use repository-relative paths and include a line range when only a section
applies. A `documentation` association for [Button](button.md) can use
`docs/button.md`. An `example` association can point at a Kotlin usage sample.
These associations allow Wax to materialize attachments for offline lookup.
Review the resulting attachment text, especially when a guide changes.

### Token evidence

Author tokens explicitly with a stable ID, exact source-facing key, supported
category, and canonical value where the source provides one. Use exact source
spellings for token aliases. Associate the declaration through an entry in
`metadata.sources` with `kind: "declaration"` and document the intended use in
`metadata.description`. Compose discovery can report the associated declaration
as source evidence. Review token keys and canonical values against Kotlin before
publishing.

| Candidate key | Category | Canonical value | Use |
| --- | --- | --- | --- |
| `ListItemDefaults.contentSpacing` | `spacing` | `16.dp` | Horizontal spacing between present list item slots. |
| `ListItemDefaults.defaultMinHeight` | `spacing` | `48.dp` | Minimum list row height. |
| `DividerDefaults.thickness` | `spacing` | `1.dp` | Default line thickness for both divider orientations. |

Keep contextual values such as `LocalContentColor.current` documented as
inherited or theme-dependent. A compound value such as
`ButtonDefaults.contentPadding` is a `PaddingValues` object. Use a scalar
canonical value only when it describes the source token faithfully.

## Verify search, lookup, and scanning

After authoring a registry, run `wax validate` from the configured publisher
repository. Search the candidate registry by use case and open a returned
component or token ID against the same snapshot. For example, with the preview
above:

```bash
wax search "multiline input" --design-system /tmp/wild-compose.registry.json \
  --language compose --kind component --format json
wax component ds.text-area --design-system /tmp/wild-compose.registry.json \
  --language compose --format json
```

Use `--snapshot` with the digest from search when the source can move. Check
`wax token` after adding reviewed tokens. In a consuming app, use `wax sync`,
`wax validate`, and `wax scan` to exercise the pinned registry. Verify a Wild
import resolves, a same-named import from another package stays separate, and
an actual token reference resolves. Check hard-coded values separately from
symbol usage. [Wax publisher and consumer workflow](https://github.com/Daio-io/wax/blob/main/website/docs/guides/registries.md)
