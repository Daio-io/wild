#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROFILE="confirmation"
SERIAL=""
VARIANTS="current,container,material"
INVOCATIONS=1

usage() {
  cat <<'EOF'
Usage: ./scripts/run-tv-style-benchmarks.sh [options]

Options:
  --profile confirmation|local_short   Benchmark profile (default: confirmation)
  --serial <adb-serial>                Required if multiple devices are connected
  --variants current,container,material
  --invocations <N>                    Repeat the selected set N times (default: 1)
  -h, --help                           Show help
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --profile)
      PROFILE="${2:-}"
      shift 2
      ;;
    --serial)
      SERIAL="${2:-}"
      shift 2
      ;;
    --variants)
      VARIANTS="${2:-}"
      shift 2
      ;;
    --invocations)
      INVOCATIONS="${2:-}"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
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

alias_to_folder() {
  case "$1" in
    current) echo "current_traversal" ;;
    container) echo "candidate_composite" ;;
    material) echo "material_surface" ;;
    *)
      echo "Unknown variant alias: $1" >&2
      exit 1
      ;;
  esac
}

alias_to_method() {
  case "$1" in
    current) echo "scrollGridWithCurrentTraversal" ;;
    container) echo "scrollGridWithCandidateComposite" ;;
    material) echo "scrollGridWithMaterialSurface" ;;
    *)
      echo "Unknown variant alias: $1" >&2
      exit 1
      ;;
  esac
}

resolve_serial() {
  local devices
  devices="$(adb devices | awk 'NR>1 && $2=="device" {print $1}')"
  local count
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
for alias in "${VARIANT_ALIASES[@]}"; do
  alias="$(echo "$alias" | tr -d '[:space:]')"
  [[ -z "$alias" ]] && continue
  SELECTED_ALIASES+=("$alias")
  alias_to_folder "$alias" >/dev/null
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
MANIFEST_JSON="$SESSION_DIR/manifest.json"

mkdir -p "$SESSION_DIR"

echo "Installing TV playbook on $SERIAL ($MODEL)..."
(
  cd "$ROOT_DIR"
  ./gradlew :playbook:androidTv:installDebug
)

python3 - <<PY
import json
from pathlib import Path
manifest = {
    "profile": "$PROFILE",
    "device": {
        "model": "$MODEL",
        "serial": "$SERIAL",
        "androidVersion": "$ANDROID_VERSION",
    },
    "gitSha": "$GIT_SHA",
    "composeVersion": "$COMPOSE_VERSION",
    "aliases": $(printf '%s\n' "${SELECTED_ALIASES[@]}" | python3 -c 'import json,sys; print(json.dumps([l.strip() for l in sys.stdin if l.strip()]))'),
    "invocations": $INVOCATIONS,
    "commands": [],
    "runs": [],
}
Path("$MANIFEST_JSON").write_text(json.dumps(manifest, indent=2) + "\n")
PY

append_run() {
  local invocation_index="$1"
  local alias="$2"
  local folder="$3"
  local method="$4"
  local command="$5"
  local variant_dir="$6"
  python3 - <<PY
import json
from pathlib import Path
path = Path("$MANIFEST_JSON")
manifest = json.loads(path.read_text())
manifest["commands"].append("""$command""")
manifest["runs"].append(
    {
        "invocation": $invocation_index,
        "alias": "$alias",
        "folder": "$folder",
        "method": "$method",
        "benchmarkData": str(Path("$variant_dir") / "benchmarkData.json"),
    }
)
path.write_text(json.dumps(manifest, indent=2) + "\n")
PY
}

run_variant() {
  local invocation_index="$1"
  local alias="$2"
  local folder method invocation_dir variant_dir connected_dir json_src message_src
  folder="$(alias_to_folder "$alias")"
  method="$(alias_to_method "$alias")"
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

  local command="./gradlew ${gradle_args[*]}"
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
  append_run "$invocation_index" "$alias" "$folder" "$method" "$command" "$variant_dir"
}

for ((i = 1; i <= INVOCATIONS; i++)); do
  for alias in "${SELECTED_ALIASES[@]}"; do
    run_variant "$i" "$alias"
  done
done

python3 - "$ROOT_DIR" "$SESSION_DIR" "$MANIFEST_JSON" <<'PY'
from __future__ import annotations

import json
import sys
from pathlib import Path

root = Path(sys.argv[1])
session_dir = Path(sys.argv[2])
manifest = json.loads(Path(sys.argv[3]).read_text())

sys.path.insert(0, str(root / "scripts"))
from tv_benchmark_report import extract_variant_metrics, write_session_artifacts

alias_to_folder = {
    "current": "current_traversal",
    "container": "candidate_composite",
    "material": "material_surface",
}

folders = [alias_to_folder[alias] for alias in manifest["aliases"]]
session = {
    "profile": manifest["profile"],
    "device": manifest["device"],
    "gitSha": manifest["gitSha"],
    "composeVersion": manifest["composeVersion"],
    "variants": folders,
    "commands": manifest["commands"],
    "invocations": [],
}

by_invocation: dict[int, dict] = {}
for run in manifest["runs"]:
    invocation = int(run["invocation"])
    by_invocation.setdefault(invocation, {})
    by_invocation[invocation][run["folder"]] = extract_variant_metrics(
        Path(run["benchmarkData"]),
        run["method"],
    )

for invocation in sorted(by_invocation):
    session["invocations"].append(
        {
            "index": invocation,
            "results": by_invocation[invocation],
        }
    )

write_session_artifacts(session_dir, session)
print(session_dir / "summary.md")
PY

rm -f "$MANIFEST_JSON"
echo "Session archived at: $SESSION_DIR"
echo "Summary: $SESSION_DIR/summary.md"
