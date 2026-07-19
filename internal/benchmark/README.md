# Wild TV style benchmarks

The TV macrobenchmark suite compares equivalent grid items across explicit style variants:

- `current_traversal`: production Wild `Modifier.clickable(style = ...)` traversable-node chain.
- `explicit_source_fast_path`: production Wild styled clickable with one remembered, non-null
  `MutableInteractionSource`, exercising the ordinary modifier path.
- `null_source_compatibility`: the same styled clickable and item configuration with a null source,
  exercising the compatibility `composed` path.
- `candidate_composite`: benchmark-only candidate path that routes the same shared item configuration through `Container` so it can be replaced by a unified-node prototype without changing scenario setup.
- `material_surface`: Android TV Material `Surface` baseline using matching size, colors, shape, border, scale target, item count, and deterministic focus input.

The benchmark hoists the shared Wild `Style` out of lazy item bodies for the Wild variants. This keeps style construction out of the scroll measurement; add a separate microbenchmark if style construction or node creation cost is the target.

`recomposeUnchangedGridWithExplicitSourceFastPath` and
`recomposeUnchangedGridWithNullSourceCompatibility` are the directly comparable source-path pair.
They use the same implementation, text, style, layout, item count, compilation mode, metrics, and
focus sequence; only the interaction-source strategy differs. After every deterministic focus move,
the benchmark injects a handled `R` key-up. Every visible item observes the same stable driver's
snapshot generation, so the items recompose while their parameters and clickable configuration stay
unchanged. A `SideEffect` acknowledges the generation only after item composition applies; the app
then exposes `benchmark-recomposition-N` so the macrobenchmark waits for completion before continuing.

## Reproduction

Run the suite on the same physical Android TV or matching device profile for every comparison. Emulator runs are useful for development only.

```bash
./gradlew :internal:benchmark:connectedCheck
```

To run only the directly comparable unchanged-recomposition cases:

```bash
./gradlew :internal:benchmark:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.class="io.daio.wild.benchmark.TvBenchmarkTest#recomposeUnchangedGridWithExplicitSourceFastPath"
./gradlew :internal:benchmark:connectedCheck \
  -Pandroid.testInstrumentationRunnerArguments.class="io.daio.wild.benchmark.TvBenchmarkTest#recomposeUnchangedGridWithNullSourceCompatibility"
```

Run at least two invocations and compare variance before making performance conclusions:

```bash
./gradlew :internal:benchmark:connectedCheck
./gradlew :internal:benchmark:connectedCheck
```

The macrobenchmarks use warm startup, 20 measured iterations, `CompilationMode.Partial()`, `FrameTimingMetric`, and `MemoryUsageMetric(Mode.Max)`. Record the device model, Android version, build type, Compose version, compilation mode, iteration count, and item count with exported benchmark results.

Report median and tail frame times, missed or overrun frames, max memory usage, and run-to-run variance. Establish a baseline before adding regression thresholds.

Peak memory supports a relative allocation-pressure comparison but is not an exact allocation count.
Use the captured traces or Android Studio's memory profiler when object-level allocation attribution is
required; do not infer exact allocation counts from `MemoryUsageMetric` alone.
