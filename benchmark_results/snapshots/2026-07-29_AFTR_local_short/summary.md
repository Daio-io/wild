# TV style benchmark session

- Profile: `local_short`
- Device: AFTR (192.168.1.187:5555)
- Android: 9
- Git SHA: `813fb73`
- Compose: 1.11.1

Verdict is human-reviewed; this report does not apply automatic pass/fail thresholds.

## Results

| Variant | Frame count | P50 (ms) | P90 (ms) | P95 (ms) | P99 (ms) | Heap max (KB) | Runtime (s) |
|---|---:|---:|---:|---:|---:|---:|---:|
| current_traversal | 824 | 4.63 | 5.53 | 6.03 | 6.83 | 9475 | 233.78 |
| candidate_composite | 829 | 4.57 | 5.64 | 6.52 | 7.60 | 10305 | 236.77 |
| material_surface | 506 | 4.89 | 6.72 | 7.93 | 10.09 | 11304 | 143.09 |

## Deltas vs material_surface

| Variant | P50 | P90 | P95 | P99 | Runtime |
|---|---:|---:|---:|---:|---:|
| current_traversal | -5.3% | -17.8% | -23.9% | -32.3% | +63.4% |
| candidate_composite | -6.6% | -16.0% | -17.7% | -24.7% | +65.5% |
