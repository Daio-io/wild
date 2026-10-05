#!/usr/bin/env python3
from __future__ import annotations

import json
import shutil
from pathlib import Path
from typing import Any


VARIANT_ALIASES: dict[str, tuple[str, str]] = {
    "clickable": ("wild_clickable", "scrollGridWithWildClickable"),
    "container": ("wild_container", "scrollGridWithWildContainer"),
    "material": ("material_surface", "scrollGridWithMaterialSurface"),
    "lambda": ("wild_lambda", "scrollGridWithWildLambda"),
    "lambda_recreated": ("wild_lambda_recreated", "scrollGridWithWildLambdaRecreated"),
}

FOLDER_TO_METHOD = {folder: method for folder, method in VARIANT_ALIASES.values()}


def resolve_variant_alias(alias: str) -> tuple[str, str]:
    try:
        return VARIANT_ALIASES[alias]
    except KeyError as exc:
        raise ValueError(f"Unknown variant alias: {alias}") from exc


def extract_variant_metrics(
    path: Path,
    benchmark_name: str,
    *,
    api_level: int | None = None,
) -> dict[str, Any]:
    data = json.loads(path.read_text())
    match = next(b for b in data["benchmarks"] if b["name"] == benchmark_name)
    metrics = match["metrics"]
    sampled = match["sampledMetrics"]["frameDurationCpuMs"]
    overrun_sampled = match["sampledMetrics"].get("frameOverrunMs")
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
        "totalRunTimeNs": match["totalRunTimeNs"],
        "frameOverrunMs": _frame_overrun_metric(overrun_sampled, api_level),
    }


def _frame_overrun_metric(
    overrun_sampled: dict[str, Any] | None,
    api_level: int | None,
) -> dict[str, Any]:
    if overrun_sampled is not None:
        return {
            "available": True,
            "P50": overrun_sampled["P50"],
            "P90": overrun_sampled["P90"],
            "P95": overrun_sampled["P95"],
            "P99": overrun_sampled["P99"],
        }
    # API < 31 cannot supply frameOverrunMs; never coerce missing data to 0.
    if api_level is not None and api_level < 31:
        return {"available": False}
    return {"available": False}


def percent_delta(value: float | None, baseline: float | None) -> float | None:
    if value is None or baseline is None or baseline == 0:
        return None
    return ((value - baseline) / baseline) * 100.0


def session_compatibility_error(
    baseline: dict[str, Any],
    candidate: dict[str, Any],
) -> str | None:
    baseline_device = baseline.get("device", {})
    candidate_device = candidate.get("device", {})
    checks = [
        ("device.model", baseline_device.get("model"), candidate_device.get("model")),
        ("device.apiLevel", baseline_device.get("apiLevel"), candidate_device.get("apiLevel")),
        (
            "compilationMode",
            baseline.get("compilationMode"),
            candidate.get("compilationMode"),
        ),
        ("workload", baseline.get("workload"), candidate.get("workload")),
        ("profile", baseline.get("profile"), candidate.get("profile")),
        (
            "sourceStrategy",
            baseline.get("sourceStrategy"),
            candidate.get("sourceStrategy"),
        ),
    ]
    for label, left, right in checks:
        if left != right:
            return f"Incompatible sessions: {label} mismatch ({left!r} vs {right!r})"
    return None


def _fmt_number(value: float | None, digits: int = 2) -> str:
    if value is None:
        return "—"
    if float(value).is_integer():
        return str(int(value))
    return f"{value:.{digits}f}"


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


def build_comparison_summary(
    *,
    baseline_label: str,
    candidate_label: str,
    baseline_metrics: dict[str, Any],
    candidate_metrics: dict[str, Any],
) -> str:
    baseline_cpu = baseline_metrics.get("frameDurationCpuMs", {})
    candidate_cpu = candidate_metrics.get("frameDurationCpuMs", {})
    lines = [
        f"# Comparison: {candidate_label} vs {baseline_label}",
        "",
        "| Metric | Baseline | Candidate | Delta |",
        "|---|---:|---:|---:|",
    ]
    for percentile in ("P50", "P90", "P95", "P99"):
        base = baseline_cpu.get(percentile)
        cand = candidate_cpu.get(percentile)
        lines.append(
            f"| {percentile} | {_fmt_number(base)} | {_fmt_number(cand)} | "
            f"{_fmt_delta(percent_delta(cand, base))} |"
        )
    return "\n".join(lines)


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
    ]
    if session.get("workload"):
        lines.append(f"- Workload: `{session['workload']}`")
    if session.get("sourceStrategy"):
        lines.append(f"- Source strategy: `{session['sourceStrategy']}`")
    if session.get("compilationMode"):
        lines.append(f"- Compilation: `{session['compilationMode']}`")

    lines.extend(
        [
            "",
            "Verdict is human-reviewed; this report does not apply automatic pass/fail thresholds.",
            "",
            "`totalRunTimeNs` stays in `session.json` as harness wall time only — omitted from deltas.",
            "",
        ]
    )

    overrun_note = _session_overrun_availability(session)
    if overrun_note is not None:
        lines.append(f"`frameOverrunMs`: {overrun_note}.")
        lines.append("")

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
            "| Variant | Frame count | P50 (ms) | P90 (ms) | P95 (ms) | P99 (ms) | Heap max (KB) |",
            "|---|---:|---:|---:|---:|---:|---:|",
        ]
    )

    for variant in variant_order:
        metrics = results.get(variant)
        if not metrics:
            continue
        cpu = metrics.get("frameDurationCpuMs", {})
        lines.append(
            "| {variant} | {fc} | {p50} | {p90} | {p95} | {p99} | {heap} |".format(
                variant=variant,
                fc=_fmt_number(_median_or_none(metrics.get("frameCount")), digits=0),
                p50=_fmt_number(cpu.get("P50")),
                p90=_fmt_number(cpu.get("P90")),
                p95=_fmt_number(cpu.get("P95")),
                p99=_fmt_number(cpu.get("P99")),
                heap=_fmt_number(_median_or_none(metrics.get("memoryHeapSizeMaxKb")), digits=0),
            )
        )

    material = results.get("material_surface")
    if material:
        lines.extend(["", "## Deltas vs material_surface", ""])
        lines.append("| Variant | P50 | P90 | P95 | P99 |")
        lines.append("|---|---:|---:|---:|---:|")
        material_cpu = material.get("frameDurationCpuMs", {})
        for variant in variant_order:
            if variant == "material_surface":
                continue
            metrics = results.get(variant)
            if not metrics:
                continue
            cpu = metrics.get("frameDurationCpuMs", {})
            lines.append(
                "| {variant} | {p50} | {p90} | {p95} | {p99} |".format(
                    variant=variant,
                    p50=_fmt_delta(percent_delta(cpu.get("P50"), material_cpu.get("P50"))),
                    p90=_fmt_delta(percent_delta(cpu.get("P90"), material_cpu.get("P90"))),
                    p95=_fmt_delta(percent_delta(cpu.get("P95"), material_cpu.get("P95"))),
                    p99=_fmt_delta(percent_delta(cpu.get("P99"), material_cpu.get("P99"))),
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
                    values.append(_fmt_number(p99))
            if values:
                lines.append(f"| {variant} | {', '.join(values)} |")

    return "\n".join(lines)


def _session_overrun_availability(session: dict[str, Any]) -> str | None:
    invocations = session.get("invocations", [])
    if not invocations:
        return None
    for metrics in invocations[0].get("results", {}).values():
        overrun = metrics.get("frameOverrunMs")
        if isinstance(overrun, dict):
            if overrun.get("available"):
                return "available"
            return "unavailable"
    api_level = session.get("device", {}).get("apiLevel")
    if api_level is not None and api_level < 31:
        return "unavailable"
    return None


def write_session_artifacts(
    session_dir: Path,
    session: dict[str, Any],
    lean_baseline_dir: Path | None = None,
) -> None:
    session_dir.mkdir(parents=True, exist_ok=True)
    (session_dir / "session.json").write_text(json.dumps(session, indent=2) + "\n")
    (session_dir / "summary.md").write_text(build_session_summary(session) + "\n")

    if lean_baseline_dir is None:
        return

    if session.get("valid") is False:
        reason = session.get("invalidReason") or "session marked invalid"
        raise ValueError(reason)

    lean_baseline_dir.mkdir(parents=True, exist_ok=True)
    shutil.copy2(session_dir / "session.json", lean_baseline_dir / "session.json")
    shutil.copy2(session_dir / "summary.md", lean_baseline_dir / "summary.md")
