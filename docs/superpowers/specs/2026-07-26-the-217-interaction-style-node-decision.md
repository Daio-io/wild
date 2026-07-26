# THE-217 Decision: Unified InteractionStyleNode prototype

**Issue:** [THE-217](https://linear.app/the-good-egg-studio/issue/THE-217/prototype-and-benchmark-a-unified-interactionstylenode-architecture)  
**Date:** 2026-07-26  
**Branch:** `feat/the-217-interaction-style-node`

## Decision

**Defer adopting the composite as the default `Modifier.interactionStyle` path.**

Keep production `current_traversal` as the default. Keep the `candidate_composite` opt-in wired to `InteractionStyleNode` for continued measurement. Revisit adopt vs retain-with-fallback after THE-222 macrobenchmarks run on a physical Android TV (or matching device profile) against this branch.

## Node / delegate / layer counts

| Path | Owner / elements | Interaction | Resolution | Visual nodes | Style parent→child traversal |
|---|---|---|---|---|---|
| `current_traversal` | 6 `Modifier.Node`s installed by `interactionStyle` | `InteractionSourceNode` | `StyleScopeParentNode` | `ScaleLayoutModifier`, `BorderNode`, `BackgroundNode`, `ShapeLayoutModifier` | Yes — descendant traversal + per-child nearest-ancestor filter |
| `candidate_composite` | 1 `DelegatingNode` owner | Collected inside `InteractionStyleNode` | Same owner (`StyleResolver`) | 1 layout delegate (`InteractionStyleScaleLayoutNode`) + 1 draw delegate (`InteractionStyleBackgroundBorderNode`) | None |

**Candidate layers:** one layout layer (scale / z-index around the whole surface including border) and one draw pass (background → clipped/alpha content → border). Shape clip and group alpha are applied in the draw delegate rather than a second layout layer because `DelegatingNode` cannot host two `LayoutModifierNode` delegates unless the owner itself implements layout.

**API note:** `@ExperimentalWildApi` `Modifier.interactionStyleComposite` was added so the TV playbook can opt into the candidate without changing existing `interactionStyle` / `clickable` signatures. Default production wiring is unchanged.

## Parity evidence (this branch)

- Common tests: value/block resolution, THE-219 reset, THE-225 snapshot observation, equal-output no-op dispatch, change-mask isolation, nesting, no `TraversableNode`, unreachable under real style-parent traversal, interaction-source replacement.
- JVM smoke pixel tests: rectangle/circle clip + alpha, and group-alpha compositing for overlapping content (the novel draw-path behaviors).
- Verification run: `./gradlew :style:jvmTest :style:spotlessCheck` and `:playbook:androidTv:testDebugUnitTest` (green on this branch).

## Benchmark status

**Not executed in this delivery session.** No Android device was attached (`adb devices` empty).

Reproduction (same device and compilation mode for both variants):

```bash
./gradlew :internal:benchmark:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.class="io.daio.wild.benchmark.TvBenchmarkTest#scrollGridWithCurrentTraversal"

./gradlew :internal:benchmark:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.class="io.daio.wild.benchmark.TvBenchmarkTest#scrollGridWithCandidateComposite"
```

Harness details (THE-222): warm startup, 20 iterations, `CompilationMode.Partial()`, `FrameTimingMetric`, `MemoryUsageMetric(Mode.Max)`. See `internal/benchmark/README.md`.

Until those runs exist for the post-prototype `candidate_composite` implementation, frame-timing and allocation conclusions remain open.

## Why defer (not adopt / not reject)

1. Prototype + behavior parity are in place; adoption is gated on material performance wins without regressions.
2. Device macrobenchmarks for the new candidate path are still outstanding.
3. Fallback optimization from THE-217 (shared traversal key + `SkipSubtreeAndContinueTraversal`, singleton default child elements) remains available if composite loses or is deferred longer.

## Follow-ups

1. Run the two scroll-grid macrobenchmarks above on a physical TV/device profile; attach results to THE-217.
2. If composite wins materially with no visual/behavior regressions → switch default `interactionStyle` internally and remove obsolete style-only traversal nodes in a dedicated change.
3. If composite loses or is inconclusive → either keep candidate for further iteration or implement the shared-traversal-key fallback on `current_traversal`.
4. Optional: rename `InteractionStyleBackgroundBorderNode` once clip/alpha ownership is settled beyond prototype.
