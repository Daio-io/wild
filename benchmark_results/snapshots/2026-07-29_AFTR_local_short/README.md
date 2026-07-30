# Snapshot: 2026-07-29 AFTR local_short

Historical `local_short` archive from the first release-runner session on AFTR.

**Do not use as a comparison baseline.** Captured before `waitUntilFocusedMarker`
(Fire TV-safe focused-ancestor waits). Terminal markers may have been present without
proven focus, so frame deltas are not trustworthy against the current harness.

- Profile: `local_short` (exploration; not a confirmation claim)
- Device: AFTR / Android 9
- Git SHA at run time: `813fb73` (pre-focus-validation harness)
- Variants: `wild_clickable`, `wild_container`, `material_surface`
- Included: `session.json`, `summary.md`, per-variant `benchmarkData.json` + `message.txt`
- Omitted: Perfetto traces

Replace with a new dated snapshot after a clean `local_short` run on AFTR once the
device is reachable again (`./scripts/run-tv-style-benchmarks.sh --profile local_short`).
