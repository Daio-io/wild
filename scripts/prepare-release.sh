#!/bin/bash

# Check if the version argument is provided
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <version>"
    exit 1
fi

VERSION=$1

# Find all api.txt files and copy them with the version appended
find . -type f -name "api.txt" -exec sh -c '
    VERSION="$1"
    shift
    for file do
        dir=$(dirname "$file")
        cp "$file" "$dir/$VERSION.txt"
    done
' _ "$VERSION" {} +

echo "All api.txt files copied with version $VERSION."