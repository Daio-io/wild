#!/usr/bin/env bash

set -euo pipefail

readonly max_file_bytes=512000
readonly max_total_bytes=20971520

fail() {
    echo "verify-screenshot-assets: $*" >&2
    exit 1
}

roots=()
if (( $# > 0 )); then
    roots=("$@")
else
    roots+=("internal/screenshot-tests/screenshots")
    while IFS= read -r -d '' root; do
        roots+=("$root")
    done < <(find playbook -type d -name screenshots -print0 2>/dev/null)
fi

files=()
for root in "${roots[@]}"; do
    [[ -e "$root" ]] || continue
    [[ -d "$root" ]] || fail "screenshot root is not a directory: $root"
    while IFS= read -r -d '' file; do
        files+=("$file")
    done < <(find "$root" -type f -print0)
done

(( ${#files[@]} > 0 )) || fail "no screenshot assets found"

total_bytes=0
for file in "${files[@]}"; do
    case "$file" in
        *.png|*.webp) ;;
        *) fail "unsupported screenshot asset: $file" ;;
    esac

    file_bytes=$(wc -c < "$file")
    if (( file_bytes > max_file_bytes )); then
        fail "screenshot asset exceeds 512000 bytes: $file ($file_bytes bytes)"
    fi
    total_bytes=$((total_bytes + file_bytes))

    first_line=""
    IFS= read -r first_line < "$file" || true
    if [[ "$first_line" == version\ https://git-lfs.github.com/spec/v1* ]]; then
        fail "Git LFS pointer found: $file"
    fi

    file_directory=$(cd "$(dirname "$file")" && pwd)
    git_root=$(git -C "$file_directory" rev-parse --show-toplevel 2>/dev/null || true)
    if [[ -n "$git_root" ]]; then
        relative_file=${file#"$git_root"/}
        filter_attribute=$(git -C "$git_root" check-attr filter -- "$relative_file" 2>/dev/null || true)
        if [[ "$filter_attribute" == *": lfs" ]]; then
            fail "Git LFS filter found: $file"
        fi
    fi
done

if (( total_bytes > max_total_bytes )); then
    fail "screenshot assets exceed 20971520 bytes in aggregate ($total_bytes bytes)"
fi

echo "Screenshot assets verified: ${#files[@]} files, $total_bytes bytes"
