#!/usr/bin/env bash
# Load repo-root .env and start platform (local dev).
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

if [[ -f .env ]]; then
  set -a
  # shellcheck source=/dev/null
  source .env
  set +a
  echo "Loaded .env (POSTGIS_HOST=${POSTGIS_HOST:-?})"
else
  echo "WARNING: .env not found — copy .env.example to .env" >&2
fi

exec ./gradlew :platform:bootRun
