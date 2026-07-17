#!/usr/bin/env bash

set -euo pipefail

echo "======================================="
echo " Spatial Repository Verification"
echo "======================================="

./gradlew --no-daemon verifyRepository

echo ""
echo "Repository verification completed successfully."