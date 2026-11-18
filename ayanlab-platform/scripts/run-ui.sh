#!/usr/bin/env bash
set -euo pipefail
PLATFORM_DIR="$(cd "$(dirname "$0")/.." && pwd)"
UI_DIR="$PLATFORM_DIR/ayanlab-platform-ui"

cd "$UI_DIR"
if [[ ! -d node_modules ]]; then
  echo "Installing npm dependencies…"
  npm install
fi

# Host never uses 8080 — platform API listens on 8102
export VITE_PROXY_TARGET="${VITE_PROXY_TARGET:-http://localhost:8102}"
echo "Running ayanlab-platform-ui (Vite)"
echo "  UI:    http://localhost:3102"
echo "  proxy: ${VITE_PROXY_TARGET} (/auth, /core)"
exec npm run dev "$@"