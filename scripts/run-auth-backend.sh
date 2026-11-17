#!/usr/bin/env bash
# Wrapper → ayanlab-platform/run-local.sh
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
exec "${ROOT}/ayanlab-platform/run-local.sh" "$@"