#!/usr/bin/env bash

set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd "$script_dir/.." && pwd)"
published_module_count=0
failure_count=0

while IFS= read -r build_file; do
    if ! grep -Fq 'id("io.daio.publish")' "$repo_root/$build_file"; then
        continue
    fi

    module_dir="${build_file%/build.gradle.kts}"
    properties_file="$repo_root/$module_dir/gradle.properties"
    published_module_count=$((published_module_count + 1))

    if [[ ! -f "$properties_file" ]]; then
        echo "FAIL: $module_dir is publishable but has no gradle.properties" >&2
        failure_count=$((failure_count + 1))
        continue
    fi

    for property_name in GROUP POM_ARTIFACT_ID POM_NAME; do
        property_count="$(grep -c "^${property_name}=" "$properties_file" || true)"
        property_value="$(sed -n "s/^${property_name}=//p" "$properties_file")"
        non_whitespace_value="${property_value//[[:space:]]/}"

        if [[ "$property_count" -ne 1 || -z "$non_whitespace_value" ]]; then
            echo "FAIL: $module_dir must define one non-empty $property_name" >&2
            failure_count=$((failure_count + 1))
        fi
    done
done < <(git -C "$repo_root" ls-files '*build.gradle.kts')

if [[ "$failure_count" -ne 0 ]]; then
    exit 1
fi

echo "publication metadata verified for $published_module_count modules"
