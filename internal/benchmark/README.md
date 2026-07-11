# Wild TV style benchmarks

The TV macrobenchmark suite compares equivalent grid items across explicit style variants:

- `current_traversal`: production Wild `Modifier.clickable(style = ...)` traversable-node chain.
- `candidate_composite`: benchmark-only candidate path that routes the same shared item configuration through `Container` so it can be replaced by a unified-node prototype without changing scenario setup.
- `material_surface`: Android TV Material `Surface` baseline using matching size, colors, shape, border, scale target, item count, and deterministic focus input.

The benchmark hoists the shared Wild `Style` out of lazy item bodies for the Wild variants. This keeps style construction out of the scroll measurement; add a separate microbenchmark if style construction or node creation cost is the target.

## Reproduction

Run the suite on the same physical Android TV or matching device profile for every comparison. Emulator runs are useful for development only.

```bash
./gradlew :internal:benchmark:connectedCheck
```

Run at least two invocations and compare variance before making performance conclusions:

```bash
./gradlew :internal:benchmark:connectedCheck
./gradlew :internal:benchmark:connectedCheck
```

The macrobenchmarks use warm startup, 20 measured iterations, `CompilationMode.Partial()`, `FrameTimingMetric`, and `MemoryUsageMetric(Mode.Max)`. Record the device model, Android version, build type, Compose version, compilation mode, iteration count, and item count with exported benchmark results.

Report median and tail frame times, missed or overrun frames, max memory usage, and run-to-run variance. Establish a baseline before adding regression thresholds.
