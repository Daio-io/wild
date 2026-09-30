#!/usr/bin/env bash

set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
validator="$script_dir/verify-screenshot-assets.sh"
fixture_root="$(mktemp -d "${TMPDIR:-/tmp}/verify-screenshot-assets.XXXXXX")"
trap 'rm -rf "$fixture_root"' EXIT

fail() {
    echo "FAIL: $*" >&2
    exit 1
}

assert_success() {
    local output
    if ! output=$("$@" 2>&1); then
        fail "expected success, got:\n$output"
    fi
    printf '%s\n' "$output"
}

assert_failure() {
    local output
    if output=$("$@" 2>&1); then
        fail "expected failure, got:\n$output"
    fi
    printf '%s\n' "$output"
}

small_assets="$fixture_root/small assets"
mkdir -p "$small_assets"
printf 'png' > "$small_assets/one.png"
printf 'webp' > "$small_assets/two.webp"
output="$(assert_success "$validator" "$small_assets")"
[[ "$output" == *"2 files"* ]] || fail "success output should include the file count"

large_file="$fixture_root/large.png"
dd if=/dev/zero of="$large_file" bs=512001 count=1 >/dev/null 2>&1
assert_failure "$validator" "$fixture_root" >/dev/null

aggregate_assets="$fixture_root/aggregate"
mkdir -p "$aggregate_assets"
dd if=/dev/zero of="$aggregate_assets/one.png" bs=10485761 count=1 >/dev/null 2>&1
dd if=/dev/zero of="$aggregate_assets/two.webp" bs=10485761 count=1 >/dev/null 2>&1
assert_failure "$validator" "$aggregate_assets" >/dev/null

unsupported_assets="$fixture_root/unsupported"
mkdir -p "$unsupported_assets"
printf 'text' > "$unsupported_assets/notes.txt"
assert_failure "$validator" "$unsupported_assets" >/dev/null

lfs_pointer="$fixture_root/lfs-pointer"
mkdir -p "$lfs_pointer"
printf '%s\n' 'version https://git-lfs.github.com/spec/v1' 'oid sha256:deadbeef' 'size 3' > "$lfs_pointer/pointer.png"
assert_failure "$validator" "$lfs_pointer" >/dev/null

lfs_attributes="$fixture_root/lfs attributes"
mkdir -p "$lfs_attributes"
(
    cd "$lfs_attributes"
    git init -q
    printf '*.png filter=lfs\n' > .gitattributes
)
printf 'png' > "$lfs_attributes/filtered.png"
assert_failure "$validator" "$lfs_attributes" >/dev/null

empty_root="$fixture_root/empty"
mkdir -p "$empty_root"
assert_failure "$validator" "$empty_root" "$fixture_root/missing" >/dev/null

echo "verify-screenshot-assets tests passed"
