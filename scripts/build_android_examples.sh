#! /bin/bash
set -euo pipefail

CURRENT_DIR=$(dirname "$(realpath "$0")")
UBQ_ROOT=$CURRENT_DIR/../../..
ANDROID_EXAMPLES_DIR=$UBQ_ROOT/apps/cesdk_android_examples

echo "CURRENT_DIR: ${CURRENT_DIR:-not set}"
echo "UBQ_ROOT: ${UBQ_ROOT:-not set}"
echo "ANDROID_EXAMPLES_DIR: ${ANDROID_EXAMPLES_DIR:-not set}"
echo "BINDINGS_APPLE_DIR: ${BINDINGS_APPLE_DIR:-not set}"
echo "CURRENT_VERSION: ${CURRENT_VERSION:-not set}"

if [[ "${CURRENT_DIR:-}" == "" ]]
then
    echo "PWD not found, exiting..."
    exit 255
fi

echo "Copying examples and showcases in $ANDROID_EXAMPLES_DIR"
rm -rf "./build_github"
rsync -av --exclude '.git' --exclude-from "$ANDROID_EXAMPLES_DIR/public-repo-exclude-files.txt" --delete "$ANDROID_EXAMPLES_DIR/." "$ANDROID_EXAMPLES_DIR/build_github"
