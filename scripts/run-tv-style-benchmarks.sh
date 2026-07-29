#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROFILE="confirmation"
SERIAL=""
VARIANTS="clickable,container,material"
INVOCATIONS=1

usage() {
  cat <<'EOF'
Usage: ./scripts/run-tv-style-benchmarks.sh [options]

Options:
  --profile confirmation|local_short   Benchmark profile (default: confirmation)
  --serial <adb-serial>                Required if multiple devices are connected
  --variants clickable,container,material
  --invocations <N>                    Repeat the selected set N times (default: 1)
  -h, --help                           Show help
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --profile) PROFILE="${2:-}"; shift 2 ;;
    --serial) SERIAL="${2:-}"; shift 2 ;;
    --variants) VARIANTS="${2:-}"; shift 2 ;;
    --invocations) INVOCATIONS="${2:-}"; shift 2 ;;
    -h|--help) usage; exit 0 ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
done

if [[ "$PROFILE" != "confirmation" && "$PROFILE" != "local_short" ]]; then
  echo "Unsupported profile: $PROFILE" >&2
  exit 1
fi

if ! [[ "$INVOCATIONS" =~ ^[1-9][0-9]*$ ]]; then
  echo "--invocations must be a positive integer" >&2
  exit 1
fi

# Prints: <folder> <method>
variant_info() {
  case "$1" in
    clickable) echo "wild_clickable scrollGridWithWildClickable" ;;
    container) echo "wild_container scrollGridWithWildContainer" ;;
    material) echo "material_surface scrollGridWithMaterialSurface" ;;
    *)
      echo "Unknown variant alias: $1" >&2
      exit 1
      ;;
  esac
}

resolve_serial() {
  local devices count
  devices="$(adb devices | awk 'NR>1 && $2=="device" {print $1}')"
  count="$(printf '%s\n' "$devices" | awk 'NF' | wc -l | tr -d ' ')"
  if [[ -n "$SERIAL" ]]; then
    adb -s "$SERIAL" get-state >/dev/null
    echo "$SERIAL"
    return
  fi
  if [[ "$count" -ne 1 ]]; then
    echo "Expected exactly one connected device, found $count. Pass --serial." >&2
    exit 1
  fi
  printf '%s\n' "$devices" | awk 'NF' | head -n1
}

IFS=',' read -r -a VARIANT_ALIASES <<<"$VARIANTS"
SELECTED_ALIASES=()
SELECTED_FOLDERS=()
for alias in "${VARIANT_ALIASES[@]}"; do
  alias="$(echo "$alias" | tr -d '[:space:]')"
  [[ -z "$alias" ]] && continue
  read -r folder _ <<<"$(variant_info "$alias")"
  SELECTED_ALIASES+=("$alias")
  SELECTED_FOLDERS+=("$folder")
done

if [[ ${#SELECTED_ALIASES[@]} -eq 0 ]]; then
  echo "No variants selected." >&2
  exit 1
fi

SERIAL="$(resolve_serial)"
export ANDROID_SERIAL="$SERIAL"

MODEL="$(adb -s "$SERIAL" shell getprop ro.product.model | tr -d '\r')"
ANDROID_VERSION="$(adb -s "$SERIAL" shell getprop ro.build.version.release | tr -d '\r')"
GIT_SHA="$(git -C "$ROOT_DIR" rev-parse --short HEAD)"
COMPOSE_VERSION="$(awk -F' = ' '/^compose-multiplatform/ {gsub(/"/, "", $2); print $2; exit}' "$ROOT_DIR/gradle/libs.versions.toml")"
MODEL_SAFE="$(printf '%s' "$MODEL" | tr ' /' '__')"
STAMP="$(date +%Y-%m-%d_%H-%M-%S)"
SESSION_DIR="$ROOT_DIR/benchmark_results/sessions/${STAMP}_${MODEL_SAFE}_${PROFILE}"
CONNECTED_OUTPUT_ROOT="$ROOT_DIR/internal/benchmark/build/outputs/connected_android_test_additional_output/debug/connected"

mkdir -p "$SESSION_DIR"

echo "Installing TV playbook on $SERIAL ($MODEL)..."
(
  cd "$ROOT_DIR"
  ./gradlew :playbook:androidTv:installDebug
)

run_variant() {
  local invocation_index="$1"
  local alias="$2"
  local folder method invocation_dir variant_dir connected_dir json_src message_src
  read -r folder method <<<"$(variant_info "$alias")"
  invocation_dir="$SESSION_DIR/invocations/$(printf '%02d' "$invocation_index")"
  variant_dir="$invocation_dir/$folder"
  mkdir -p "$variant_dir/traces"

  local gradle_args=(
    :internal:benchmark:connectedCheck
    "-Pandroid.testInstrumentationRunnerArguments.class=io.daio.wild.benchmark.TvBenchmarkTest#${method}"
  )
  if [[ "$PROFILE" == "local_short" ]]; then
    gradle_args+=("-Pandroid.testInstrumentationRunnerArguments.benchmarkProfile=local_short")
  fi

  echo "Running $folder (invocation $invocation_index)..."
  (
    cd "$ROOT_DIR"
    ./gradlew "${gradle_args[@]}"
  )

  connected_dir="$(find "$CONNECTED_OUTPUT_ROOT" -mindepth 1 -maxdepth 1 -type d | head -n1 || true)"
  if [[ -z "$connected_dir" ]]; then
    echo "Missing connected Android test output directory under $CONNECTED_OUTPUT_ROOT" >&2
    exit 1
  fi

  json_src="$connected_dir/io.daio.wild.benchmark-benchmarkData.json"
  if [[ ! -f "$json_src" ]]; then
    echo "Missing benchmarkData.json for $folder at $json_src" >&2
    exit 1
  fi
  cp "$json_src" "$variant_dir/benchmarkData.json"

  message_src="$(find "$connected_dir" -maxdepth 1 -type f -name "additionaltestoutput.benchmark.message_*${method}.txt" | head -n1 || true)"
  if [[ -n "$message_src" ]]; then
    cp "$message_src" "$variant_dir/message.txt"
  fi

  find "$connected_dir" -maxdepth 1 -type f -name "TvBenchmarkTest_${method}_*.perfetto-trace" -exec cp {} "$variant_dir/traces/" \;
}

for ((i = 1; i <= INVOCATIONS; i++)); do
  for alias in "${SELECTED_ALIASES[@]}"; do
    run_variant "$i" "$alias"
  done
done

python3 - "$ROOT_DIR" "$SESSION_DIR" "$PROFILE" "$MODEL" "$ANDROID_VERSION" "$GIT_SHA" "$COMPOSE_VERSION" "${SELECTED_FOLDERS[@]}" <<'PY'
from __future__ import annotations

import json
import sys
from pathlib import Path

root = Path(sys.argv[1])
session_dir = Path(sys.argv[2])
profile, model, android, git_sha, compose = sys.argv[3:8]
folders = sys.argv[8:]

sys.path.insert(0, str(root / "scripts"))
from tv_benchmark_report import extract_variant_metrics, write_session_artifacts

FOLDER_TO_METHOD = {
    "wild_clickable": "scrollGridWithWildClickable",
    "wild_container": "scrollGridWithWildContainer",
    "material_surface": "scrollGridWithMaterialSurface",
}

session = {
    "profile": profile,
    "device": {"model": model, "androidVersion": android},
    "gitSha": git_sha,
    "composeVersion": compose,
    "variants": folders,
    "invocations": [],
}

for inv_dir in sorted(session_dir.glob("invocations/*")):
    results = {}
    for folder in folders:
        data = inv_dir / folder / "benchmarkData.json"
        results[folder] = extract_variant_metrics(data, FOLDER_TO_METHOD[folder])
    session["invocations"].append({"index": int(inv_dir.name), "results": results})

write_session_artifacts(session_dir, session)
print(session_dir / "summary.md")
PY

echo "Session archived at: $SESSION_DIR"
echo "Summary: $SESSION_DIR/summary.md"
