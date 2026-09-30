#!/usr/bin/env bash
# Ensures verifyHostScreenshots only depends on iOS Roborazzi verify tasks for
# modules that already have committed iosSimulatorArm64 goldens (or the empty
# internal harness module, which has no consumer screenshot cases).

set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

fail() {
    echo "FAIL: $*" >&2
    exit 1
}

map_gradle_path_to_dir() {
    local gradle_path="$1"
    case "$gradle_path" in
        :internal:screenshot-tests) echo "internal/screenshot-tests" ;;
        :components:button) echo "components/button" ;;
        :components:icon) echo "components/icon" ;;
        :components:list-item) echo "components/list-item" ;;
        :components:progress) echo "components/progress" ;;
        :components:text) echo "components/text" ;;
        :components:toggleable) echo "components/toggleable" ;;
        :content-color) echo "content-color" ;;
        :layout:container) echo "layout/container" ;;
        :layout:divider) echo "layout/divider" ;;
        :playbook:android) echo "playbook/android" ;;
        :playbook:androidTv) echo "playbook/androidTv" ;;
        :playbook:desktop) echo "playbook/desktop" ;;
        *) fail "unmapped Gradle path: $gradle_path" ;;
    esac
}

# Extract verifyHostScreenshots block, then iOS verify dependency lines.
block="$(
    awk '
        /tasks\.register\("verifyHostScreenshots"\)/ { in_task=1 }
        in_task { print }
        in_task && /^}/ { exit }
    ' build.gradle.kts
)"
[[ -n "$block" ]] || fail "verifyHostScreenshots task not found"

deps=()
while IFS= read -r line; do
    [[ -n "$line" ]] || continue
    deps+=("$line")
done < <(printf '%s\n' "$block" | grep -o ':[[:alnum:]:-]*:verifyRoborazziIosSimulatorArm64')

(( ${#deps[@]} > 0 )) || fail "no iOS verify deps found in verifyHostScreenshots"

for dep in "${deps[@]}"; do
    module="${dep%:verifyRoborazziIosSimulatorArm64}"
    dir="$(map_gradle_path_to_dir "$module")"
    if [[ "$module" == ":internal:screenshot-tests" ]]; then
        continue
    fi
    golden_count="$(git ls-files "$dir/screenshots/iosSimulatorArm64/" | wc -l | tr -d ' ')"
    if (( golden_count == 0 )); then
        fail "$dep is wired into verifyHostScreenshots but $dir/screenshots/iosSimulatorArm64 has no committed goldens"
    fi
done

echo "verify-host-ios-golden-coverage tests passed"
