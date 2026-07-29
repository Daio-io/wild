# Snapshot: 2026-07-29 AFTR local_short

First dated release-runner snapshot on the trusted AFTR Fire TV.

- Profile: `local_short` (exploration; not a confirmation claim)
- Device: AFTR / Android 9
- Git SHA at run time: `813fb73` (pre-rebase worktree SHA; branch later rebased onto `origin/main`)
- Variants: `current_traversal`, `candidate_composite` (Wild Container), `material_surface`
- Included: `session.json`, `summary.md`, per-variant `benchmarkData.json` + `message.txt`
- Omitted: Perfetto traces (~200MB) — keep those local under `benchmark_results/sessions/`

Use this as the first human-comparison baseline. Confirmation-profile snapshots should be added separately before release claims.
