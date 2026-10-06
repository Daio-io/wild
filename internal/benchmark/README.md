# Wild TV style benchmarks

The TV macrobenchmark suite compares equivalent grid items across explicit style variants:

- `wild_clickable`: production Wild `Modifier.clickable(style = ...)` traversable-node chain.
- `wild_lambda`: production Wild `Modifier.clickable(styleBlock = ...)` with a hoisted equivalent
  callback; default lambda candidate for value-vs-lambda comparisons.
- `wild_lambda_recreated`: diagnostic negative control that recreates the style callback each
  composition; not the default comparable candidate.
- `explicit_source_fast_path`: production Wild styled clickable with one remembered, non-null
  `MutableInteractionSource`, exercising the ordinary modifier path.
- `null_source_compatibility`: the same styled clickable and item configuration with a null source,
  exercising the compatibility `composed` path.
- `wild_container`: Wild `Container(...)` using the same shared item configuration as the other
  variants.
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

`recomposeOnlyWithWildClickable` / `recomposeOnlyWithWildLambda` keep focus fixed on
`benchmark-item-0-0` and issue 40 handled `R` requests per iteration with per-generation
acknowledgement. Use these for unchanged-item recomposition cost without focus movement.

Additional playbook modes for harness investigation:

- `snapshot_chrome`: snapshot-driven chrome color flip (`C` key); completion marker
  `benchmark-snapshot-chrome-N` advances only after apply-time acknowledgement (not at the
  state write), matching the recomposition-driver contract.
- `nested_styles` / `nested_styles_small` / `nested_styles_large`: nested chrome owners around the
  shared item fixture.

## Release workflow (preferred)

Use the single-device release runner for comparable Wild vs Material claims. Prefer one physical
Android TV or matching device profile. Emulator runs are useful for harness debugging only.

```bash
# Release claims (confirmation profile)
./scripts/run-tv-style-benchmarks.sh --profile confirmation

# Value vs hoisted-lambda comparison (rotate order across invocations)
./scripts/run-tv-style-benchmarks.sh --profile confirmation \
  --variants clickable,lambda --invocations 2

# Local exploration on a physical device
./scripts/run-tv-style-benchmarks.sh --profile local_short --invocations 1
```

Useful flags:

- `--variants clickable,container,material,lambda` — subset of the comparison set
  (`lambda_recreated` is diagnostic only)
- `--serial <adb-serial>` — required when more than one device is connected
- `--invocations N` — repeat the selected set; order rotates left each invocation to
  counterbalance thermal / position bias (prefer `N` equal to the variant count)
- `--allow-dirty` — permit uncommitted changes; session records `gitDirty: true`. Without
  this flag the runner refuses a dirty worktree so `gitSha` matches the measured APK.

Alias mapping:

| Alias | Variant folder | Test method |
|-------|----------------|-------------|
| `clickable` | `wild_clickable` | `scrollGridWithWildClickable` |
| `lambda` | `wild_lambda` | `scrollGridWithWildLambda` |
| `lambda_recreated` | `wild_lambda_recreated` | `scrollGridWithWildLambdaRecreated` |
| `container` | `wild_container` | `scrollGridWithWildContainer` |
| `material` | `material_surface` | `scrollGridWithMaterialSurface` |

Each session archives raw JSON, optional message text, perfetto traces, `session.json`, and
`summary.md` under:

```text
benchmark_results/sessions/<yyyy-mm-dd_HH-mm-ss>_<device>_<profile>/
```

Session metadata records git SHA, dirty flag, APK hash, device model/API, Compose version,
compilation mode (`Partial`), workload (`scroll_grid`), source strategy (`explicit`), variant
order per invocation, and frame distributions. `frameOverrunMs` is retained when present and
reported as unavailable on API < 31 (never coerced to 0). Invalid or incomplete sessions are
rejected rather than silently retried; incompatible device/API/compilation/workload pairs must not
be compared. Missing markers invalidate a session and prevent writing a lean baseline.

Full session directories (including traces) stay local and are gitignored. Lean dated baselines for
human comparison live under:

```text
benchmark_results/snapshots/<yyyy-mm-dd>_<device>_<profile>/
```

Those snapshots keep `session.json`, `summary.md`, and per-variant `benchmarkData.json` /
`message.txt`, but omit Perfetto traces. Do not treat
`benchmark_results/snapshots/2026-07-29_AFTR_local_short/` as a baseline — it predates
focused-marker validation; capture a new dated snapshot after harness changes.

**Confirmation / release profile (default):** warm startup, 20 measured iterations,
`CompilationMode.Partial()`, full scroll path ending at `benchmark-item-5-20`, fixed ~50ms key pace,
`FrameTimingMetric` + `MemoryUsageMetric(Mode.Max)`. This is the only profile valid for release
claims in docs or PRs. Durations remain report-only — no automatic CI duration gate.

**Local short profile:** same compilation mode and metrics, 5 iterations, shortened scroll path
ending at `benchmark-item-2-10`. Use for device bring-up and harness debugging only — not for
release claims.

The runner installs `:playbook:androidTv` before measuring, runs each selected variant in isolation
(with per-invocation order rotation), and copies outputs before the next Gradle run overwrites them.
Each invocation records its run `order` in `session.json`. Summaries are report-only: no automatic
pass/fail thresholds.

For confirmation claims, collect at least three complete sessions with rotated variant order on the
same physical device, then archive raw JSON/traces/metadata and lean baselines as above.

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

Focus-fixed recomposition (40 handled `R` per iteration):

```bash
./gradlew :internal:benchmark:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.class="io.daio.wild.benchmark.TvBenchmarkTest#recomposeOnlyWithWildClickable"
./gradlew :internal:benchmark:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.class="io.daio.wild.benchmark.TvBenchmarkTest#recomposeOnlyWithWildLambda"
```

Two-item focus-flip pair:

```bash
./gradlew :internal:benchmark:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.class="io.daio.wild.benchmark.TvBenchmarkTest#focusFlipWithWildClickable"
./gradlew :internal:benchmark:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.class="io.daio.wild.benchmark.TvBenchmarkTest#focusFlipWithWildLambda"
./gradlew :internal:benchmark:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.class="io.daio.wild.benchmark.TvBenchmarkTest#focusFlipWithWildContainer"
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

## StyleDefaults construction microbenchmark

The `:internal:style-benchmark` Android microbenchmark measures construction of the default
`StyleDefaults` leaf factories, `StyleDefaults.style()`, `ButtonDefaults.style()`, and a
partially customized style. Run it on a physical Android device using the release benchmark variant:

```bash
./gradlew :internal:style-benchmark:connectedCheck
```

`StyleModifierConstructionBenchmark` adds warmed construction cases for value and hoisted-lambda
`interactionStyle` modifiers (`valueInteractionStyle_construction`,
`hoistedLambdaInteractionStyle_construction`). Sources, base styles, and callbacks are hoisted
outside `measureRepeated`; results label definition/modifier construction only — never attached
node resolution. Keep `StyleDefaultsBenchmark` as the default-factory control.

AndroidX Benchmark writes JSON beneath
`internal/style-benchmark/build/outputs/connected_android_test_additional_output/releaseAndroidTest/connected/<device>/`.
Do not hardcode `<device>`; locate the report with:

```bash
find internal/style-benchmark/build/outputs/connected_android_test_additional_output \
  -name '*-benchmarkData.json' -print
```

Record the device, Android version, benchmark version, build SHA, median time, and
`allocationCount` when comparing revisions. Use the same device for before/after runs and do not
derive a hard timing threshold from a single device.
