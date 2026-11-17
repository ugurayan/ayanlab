#!/usr/bin/env bash
# Product backends use Maven (pom.xml). This script is a no-op after old/ removal.
set -euo pipefail
echo "Skipped: Gradle wrapper install — backends are Maven-based (see generate-maven-backends.sh)." >&2
exit 0