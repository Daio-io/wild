#!/usr/bin/env bash

set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
release_script="$script_dir/release.sh"
fixture_root="$(mktemp -d "${TMPDIR:-/tmp}/wild-release.XXXXXX")"
trap 'rm -rf "$fixture_root"' EXIT

fail() {
    echo "FAIL: $*" >&2
    exit 1
}

new_fixture() {
    local name="$1"
    local root="$fixture_root/$name"

    mkdir -p "$root/scripts"
    cp "$release_script" "$root/scripts/release.sh"
    chmod +x "$root/scripts/release.sh"
    printf '%s\n' \
        '# Project-wide Gradle settings.' \
        'GROUP=io.daio.wild' \
        'VERSION_NAME=0.6.0' \
        'SONATYPE_HOST=CENTRAL_PORTAL' > "$root/gradle.properties"
    printf '%s\n' "$root"
}

assert_version() {
    local properties_file="$1"
    local expected_version="$2"
    local actual_version

    actual_version="$(sed -n 's/^VERSION_NAME=//p' "$properties_file")"
    [[ "$actual_version" == "$expected_version" ]] || \
        fail "expected VERSION_NAME=$expected_version, got VERSION_NAME=$actual_version"
}

stable_root="$(new_fixture stable)"
stable_output="$("$stable_root/scripts/release.sh" 0.8.0)"
assert_version "$stable_root/gradle.properties" 0.8.0
[[ "$stable_output" == *"Release version: 0.8.0"* ]] || \
    fail "stable release output did not report the stamped version"

prerelease_root="$(new_fixture prerelease)"
"$prerelease_root/scripts/release.sh" 1.0.0-rc.1+build.7 >/dev/null
assert_version "$prerelease_root/gradle.properties" 1.0.0-rc.1+build.7

zero_prerelease_root="$(new_fixture zero-prerelease)"
"$zero_prerelease_root/scripts/release.sh" 1.0.0-0 >/dev/null
assert_version "$zero_prerelease_root/gradle.properties" 1.0.0-0

for invalid_version in v0.8.0 01.0.0 1.0.0-01 1.0 1.0.0-; do
    fixture_name="invalid-${invalid_version//[^0-9A-Za-z]/-}"
    invalid_root="$(new_fixture "$fixture_name")"
    if "$invalid_root/scripts/release.sh" "$invalid_version" \
        >"$invalid_root/output" 2>&1; then
        fail "expected '$invalid_version' to be rejected"
    fi
    assert_version "$invalid_root/gradle.properties" 0.6.0
    grep -q 'must be a semantic version' "$invalid_root/output" || \
        fail "invalid tag failure did not explain the expected format"
done

missing_property_root="$(new_fixture missing-property)"
sed '/^VERSION_NAME=/d' "$missing_property_root/gradle.properties" > \
    "$missing_property_root/without-version.properties"
mv "$missing_property_root/without-version.properties" \
    "$missing_property_root/gradle.properties"
if "$missing_property_root/scripts/release.sh" 0.8.0 \
    >"$missing_property_root/output" 2>&1; then
    fail "expected a missing VERSION_NAME property to be rejected"
fi
grep -q 'exactly one VERSION_NAME property' "$missing_property_root/output" || \
    fail "missing property failure did not explain the required property"

echo "release script tests passed"
