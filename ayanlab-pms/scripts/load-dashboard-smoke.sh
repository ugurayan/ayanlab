#!/usr/bin/env bash
# P6.5 smoke: hammer dashboard list endpoint (requires PMS + JWT or disable auth in dev).
set -euo pipefail

BASE_URL="${PMS_BASE_URL:-http://127.0.0.1:8105}"
TOKEN="${PMS_BEARER_TOKEN:-}"
REQUESTS="${LOAD_REQUESTS:-200}"
CONCURRENCY="${LOAD_CONCURRENCY:-20}"

if [[ -z "$TOKEN" ]]; then
  echo "Set PMS_BEARER_TOKEN (platform JWT with ipms:dashboard:read)" >&2
  exit 1
fi

path="/pms/api/dashboard/pm/widgets"
echo "Load smoke: $REQUESTS GETs to $BASE_URL$path (concurrency $CONCURRENCY)"

run_one() {
  curl -sS -o /dev/null -w "%{http_code}\n" \
    -H "Authorization: Bearer $TOKEN" \
    -H "X-Client-App: ipms" \
    -H "X-Client-Org: demo_ayanlab" \
    "$BASE_URL$path"
}

export -f run_one
export BASE_URL TOKEN path

seq 1 "$REQUESTS" | xargs -P "$CONCURRENCY" -I{} bash -c 'run_one' | sort | uniq -c