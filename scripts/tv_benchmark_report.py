#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Any


def extract_variant_metrics(path: Path, benchmark_name: str) -> dict[str, Any]:
    data = json.loads(path.read_text())
    match = next(b for b in data["benchmarks"] if b["name"] == benchmark_name)
    metrics = match["metrics"]
    sampled = match["sampledMetrics"]["frameDurationCpuMs"]
    return {
        "name": match["name"],
        "frameCount": metrics["frameCount"],
        "frameDurationCpuMs": {
            "P50": sampled["P50"],
            "P90": sampled["P90"],
            "P95": sampled["P95"],
            "P99": sampled["P99"],
        },
        "memoryHeapSizeMaxKb": metrics.get("memoryHeapSizeMaxKb"),
        "memoryRssAnonMaxKb": metrics.get("memoryRssAnonMaxKb"),
        "memoryRssFileMaxKb": metrics.get("memoryRssFileMaxKb"),
        "totalRunTimeNs": match["totalRunTimeNs"],
    }


def percent_delta(value: float | None, baseline: float | None) -> float | None:
    if value is None or baseline is None or baseline == 0:
        return None
    return ((value - baseline) / baseline) * 100.0


def _fmt_number(value: float | None, digits: int = 2) -> str:
    if value is None:
        return "—"
    if float(value).is_integer():
        return str(int(value))
    return f"{value:.{digits}f}"


def _fmt_ms(value: float | None) -> str:
    return _fmt_number(value, digits=2)


def _fmt_delta(value: float | None) -> str:
    if value is None:
        return "—"
    sign = "+" if value > 0 else ""
    return f"{sign}{value:.1f}%"


def _median_or_none(metric: Any) -> float | None:
    if metric is None:
        return None
    if isinstance(metric, dict):
        return metric.get("median")
    return None


def _runtime_s(ns: Any) -> float | None:
    if ns is None:
        return None
    return float(ns) / 1_000_000_000.0


def build_session_summary(session: dict[str, Any]) -> str:
    device = session.get("device", {})
    lines: list[str] = [
        "# TV style benchmark session",
        "",
        f"- Profile: `{session.get('profile', 'unknown')}`",
        f"- Device: {device.get('model', 'unknown')}",
        f"- Android: {device.get('androidVersion', 'unknown')}",
        f"- Git SHA: `{session.get('gitSha', 'unknown')}`",
        f"- Compose: {session.get('composeVersion', 'unknown')}",
        "",
        "Verdict is human-reviewed; this report does not apply automatic pass/fail thresholds.",
        "",
    ]

    invocations = session.get("invocations", [])
    if not invocations:
        lines.append("No invocations recorded.")
        return "\n".join(lines)

    primary = invocations[0]
    results: dict[str, Any] = primary.get("results", {})
    variant_order = session.get("variants") or list(results.keys())

    lines.extend(
        [
            "## Results",
            "",
            "| Variant | Frame count | P50 (ms) | P90 (ms) | P95 (ms) | P99 (ms) | Heap max (KB) | Runtime (s) |",
            "|---|---:|---:|---:|---:|---:|---:|---:|",
        ]
    )

    for variant in variant_order:
        metrics = results.get(variant)
        if not metrics:
            continue
        cpu = metrics.get("frameDurationCpuMs", {})
        lines.append(
            "| {variant} | {fc} | {p50} | {p90} | {p95} | {p99} | {heap} | {rt} |".format(
                variant=variant,
                fc=_fmt_number(_median_or_none(metrics.get("frameCount")), digits=0),
                p50=_fmt_ms(cpu.get("P50")),
                p90=_fmt_ms(cpu.get("P90")),
                p95=_fmt_ms(cpu.get("P95")),
                p99=_fmt_ms(cpu.get("P99")),
                heap=_fmt_number(_median_or_none(metrics.get("memoryHeapSizeMaxKb")), digits=0),
                rt=_fmt_ms(_runtime_s(metrics.get("totalRunTimeNs"))),
            )
        )

    material = results.get("material_surface")
    if material:
        lines.extend(["", "## Deltas vs material_surface", ""])
        lines.append("| Variant | P50 | P90 | P95 | P99 | Runtime |")
        lines.append("|---|---:|---:|---:|---:|---:|")
        material_cpu = material.get("frameDurationCpuMs", {})
        for variant in variant_order:
            if variant == "material_surface":
                continue
            metrics = results.get(variant)
            if not metrics:
                continue
            cpu = metrics.get("frameDurationCpuMs", {})
            lines.append(
                "| {variant} | {p50} | {p90} | {p95} | {p99} | {rt} |".format(
                    variant=variant,
                    p50=_fmt_delta(percent_delta(cpu.get("P50"), material_cpu.get("P50"))),
                    p90=_fmt_delta(percent_delta(cpu.get("P90"), material_cpu.get("P90"))),
                    p95=_fmt_delta(percent_delta(cpu.get("P95"), material_cpu.get("P95"))),
                    p99=_fmt_delta(percent_delta(cpu.get("P99"), material_cpu.get("P99"))),
                    rt=_fmt_delta(
                        percent_delta(
                            _runtime_s(metrics.get("totalRunTimeNs")) or 0.0,
                            _runtime_s(material.get("totalRunTimeNs")) or 0.0,
                        )
                    ),
                )
            )

    if len(invocations) > 1:
        lines.extend(["", "## Run-to-run variance (P99)", ""])
        lines.append("| Variant | Invocations P99 (ms) |")
        lines.append("|---|---|")
        for variant in variant_order:
            values = []
            for invocation in invocations:
                metrics = invocation.get("results", {}).get(variant)
                if not metrics:
                    continue
                p99 = metrics.get("frameDurationCpuMs", {}).get("P99")
                if p99 is not None:
                    values.append(_fmt_ms(p99))
            if values:
                lines.append(f"| {variant} | {', '.join(values)} |")

    return "\n".join(lines)


def write_session_artifacts(session_dir: Path, session: dict[str, Any]) -> None:
    session_dir.mkdir(parents=True, exist_ok=True)
    (session_dir / "session.json").write_text(json.dumps(session, indent=2) + "\n")
    (session_dir / "summary.md").write_text(build_session_summary(session) + "\n")


def main() -> None:
    parser = argparse.ArgumentParser(description="Write TV benchmark session summary artifacts")
    parser.add_argument("--session-json", type=Path, required=True)
    parser.add_argument("--out-dir", type=Path, required=True)
    args = parser.parse_args()
    session = json.loads(args.session_json.read_text())
    write_session_artifacts(args.out_dir, session)


if __name__ == "__main__":
    main()
