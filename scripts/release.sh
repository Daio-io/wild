#!/usr/bin/env bash

set -euo pipefail

if [[ "$#" -ne 1 ]]; then
    echo "Usage: $0 <release-version>" >&2
    exit 1
fi

release_version="$1"
numeric_identifier='0|[1-9][0-9]*'
non_numeric_identifier='[0-9A-Za-z-]*[A-Za-z-][0-9A-Za-z-]*'
prerelease_identifier="(${numeric_identifier}|${non_numeric_identifier})"
semver_pattern="^(0|[1-9][0-9]*)\\.(0|[1-9][0-9]*)\\.(0|[1-9][0-9]*)(-${prerelease_identifier}(\\.${prerelease_identifier})*)?(\\+[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?$"

if [[ ! "$release_version" =~ $semver_pattern ]]; then
    echo "Release tag '$release_version' must be a semantic version such as 0.8.0 or 0.8.0-rc.1." >&2
    exit 1
fi

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
properties_file="$script_dir/../gradle.properties"

if [[ ! -f "$properties_file" ]]; then
    echo "Could not find gradle.properties at $properties_file." >&2
    exit 1
fi

version_property_count="$(awk '/^VERSION_NAME=/ { count++ } END { print count + 0 }' "$properties_file")"
if [[ "$version_property_count" -ne 1 ]]; then
    echo "Expected exactly one VERSION_NAME property in $properties_file; found $version_property_count." >&2
    exit 1
fi

replacement_file="$(mktemp "$properties_file.XXXXXX")"
replacement_content="$(mktemp "$properties_file.XXXXXX")"
trap 'rm -f "$replacement_file" "$replacement_content"' EXIT

cp -p "$properties_file" "$replacement_file"

awk -v version="$release_version" '
    /^VERSION_NAME=/ {
        print "VERSION_NAME=" version
        next
    }
    { print }
' "$properties_file" > "$replacement_content"

cat "$replacement_content" > "$replacement_file"
mv "$replacement_file" "$properties_file"

stamped_version="$(sed -n 's/^VERSION_NAME=//p' "$properties_file")"
if [[ "$stamped_version" != "$release_version" ]]; then
    echo "Failed to stamp VERSION_NAME=$release_version in $properties_file." >&2
    exit 1
fi

echo "Release version: $stamped_version"
