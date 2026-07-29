# Wild TV style benchmarks

The TV macrobenchmark suite compares equivalent grid items across explicit style variants:

- `current_traversal`: production Wild `Modifier.clickable(style = ...)` traversable-node chain.
- `explicit_source_fast_path`: production Wild styled clickable with one remembered, non-null
  `MutableInteractionSource`, exercising the ordinary modifier path.
- `null_source_compatibility`: the same styled clickable and item configuration with a null source,
  exercising the compatibility `composed` path.
- `candidate_composite`: Wild `Container(...)` candidate path using the same shared item
  configuration as the other variants.
- `material_surface`: Android TV Material `Surface` baseline using matching size, colors, shape,
  border, scale target, item count, and deterministic focus input.

The benchmark hoists the shared Wild `Style` out of lazy item bodies for the Wild variants. This keeps style construction out of the scroll measurement; add a separate microbenchmark if style construction or node creation cost is the target.

`recomposeUnchangedGridWithExplicitSourceFastPath` and
`recomposeUnchangedGridWithNullSourceCompatibility` are the directly comparable source-path pair.
They use the same implementation, text, style, layout, item count, compilation mode, metrics, and
focus sequence; only the interaction-source strategy differs. After every deterministic focus move,
the benchmark injects a handled `R` key-up. Every visible item observes the same stable driver's
snapshot generation, so the items recompose while their parameters and clickable configuration stay
unchanged. A `SideEffect` acknowledges the generation only after item composition applies; the app
then exposes `benchmark-recomposition-N` so the macrobenchmark waits for completion before continuing.
The optional real-wiring test observer leaves one nullable field on the shared driver and one
lookup/null branch in each driven item. Both measured variants pay that same minimal overhead;
detailed composition records and their marker strings are created only when the test observer is
present.

## Release workflow (preferred)

Use the single-device release runner for comparable Wild vs Material claims. Prefer one physical
Android TV or matching device profile. Emulator runs are useful for harness debugging only.

```bash
# Release claims (confirmation profile)
./scripts/run-tv-style-benchmarks.sh --profile confirmation

# Local exploration on a physical device
./scripts/run-tv-style-benchmarks.sh --profile local_short --invocations 1
```

Useful flags:

- `--variants current,container,material` — subset of the release comparison set
- `--serial <adb-serial>` — required when more than one device is connected
- `--invocations N` — repeat the selected set for run-to-run variance

Alias mapping:

| Alias | Variant folder | Test method |
|-------|----------------|-------------|
| `current` | `current_traversal` | `scrollGridWithCurrentTraversal` |
| `container` | `candidate_composite` | `scrollGridWithCandidateComposite` |
| `material` | `material_surface` | `scrollGridWithMaterialSurface` |

Each session archives raw JSON, optional message text, perfetto traces, `session.json`, and
`summary.md` under:

```text
benchmark_results/sessions/<yyyy-mm-dd_HH-mm-ss>_<device>_<profile>/
```

**Confirmation / release profile (default):** warm startup, 20 measured iterations,
`CompilationMode.Partial()`, full scroll path ending at `benchmark-item-5-20`, fixed ~50ms key pace,
`FrameTimingMetric` + `MemoryUsageMetric(Mode.Max)`. This is the only profile valid for release
claims in docs or PRs.

**Local short profile:** same compilation mode and metrics, 5 iterations, shortened scroll path
ending at `benchmark-item-2-10`. Use for device bring-up and harness debugging only — not for
release claims.

The runner installs `:playbook:androidTv` before measuring, runs each selected variant in isolation,
and copies outputs before the next Gradle run overwrites them. Summaries are report-only: no
automatic pass/fail thresholds.

## Deep-dive Gradle commands

Raw Gradle remains available for source-path and focus-flip investigations.

```bash
./gradlew :internal:benchmark:connectedCheck
```

Directly comparable unchanged-recomposition cases:

```bash
./gradlew :internal:benchmark:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.class="io.daio.wild.benchmark.TvBenchmarkTest#recomposeUnchangedGridWithExplicitSourceFastPath"
./gradlew :internal:benchmark:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.class="io.daio.wild.benchmark.TvBenchmarkTest#recomposeUnchangedGridWithNullSourceCompatibility"
```

Two-item focus-flip pair:

```bash
./gradlew :internal:benchmark:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.class="io.daio.wild.benchmark.TvBenchmarkTest#focusFlipWithCurrentTraversal"
./gradlew :internal:benchmark:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.class="io.daio.wild.benchmark.TvBenchmarkTest#focusFlipWithCandidateComposite"
```

Optional short profile for a single deep-dive method:

```bash
./gradlew :internal:benchmark:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.class="io.daio.wild.benchmark.TvBenchmarkTest#scrollGridWithMaterialSurface" \
  -Pandroid.testInstrumentationRunnerArguments.benchmarkProfile=local_short
```

Nested `LazyRow` grids reset column on vertical moves, so scroll sequences re-scroll horizontally
after each `DOWN`. The playbook grid centers focused items with `BringIntoViewSpec` so scroll stays
aligned under rapid focus moves.

Record the device model, Android version, build type, Compose version, compilation mode, iteration
count, and item count with exported benchmark results. Prefer the archived `session.json` /
`summary.md` from the release runner when making claims.

Report median and tail frame times, max memory usage, and run-to-run variance. Establish a baseline
before adding regression thresholds.

Peak memory supports a relative allocation-pressure comparison but is not an exact allocation count.
Use the captured traces or Android Studio's memory profiler when object-level allocation attribution
is required; do not infer exact allocation counts from `MemoryUsageMetric` alone.
