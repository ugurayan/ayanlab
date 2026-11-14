#!/usr/bin/env bash
# Modül başına en az 1 endpoint smoke test.
# Kullanım: bash docs/curl/smoke-test.sh
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=env.example.sh
source "${SCRIPT_DIR}/env.example.sh"

PASS=0
FAIL=0
SKIP=0

run() {
  local name="$1"
  shift
  echo ""
  echo "==> ${name}"
  if output="$("$@" 2>&1)"; then
    echo "$output" | head -c 500
    echo ""
    PASS=$((PASS + 1))
    echo "OK"
  else
    echo "$output"
    FAIL=$((FAIL + 1))
    echo "FAIL"
  fi
}

run_skip() {
  local name="$1"
  local reason="$2"
  echo ""
  echo "==> ${name} — SKIP (${reason})"
  SKIP=$((SKIP + 1))
}

http_ok() {
  local url="$1"
  shift
  local code
  code=$(curl -s -o /tmp/ayanlabs_smoke_body.json -w "%{http_code}" "$url" "$@")
  echo "HTTP ${code}"
  cat /tmp/ayanlabs_smoke_body.json | head -c 300
  echo ""
  [[ "$code" =~ ^2 ]]
}

echo "Smoke test — BASE_URL=${BASE_URL}"

# --- Platform ---
run "Platform: actuator health" \
  http_ok "${BASE_URL}/actuator/health"

run "Platform: OpenAPI docs" \
  http_ok "${BASE_URL}/v3/api-docs"

# --- Core (public) ---
run "Core: subscription plans (IPMS app)" \
  http_ok "${BASE_URL}/api/core/subscription-plans?appId=${APP_ID_IPMS}&activeOnly=true" \
  $(ayanlabs_headers_public "${APP_ID_IPMS}")

# --- Vault register → tokens ---
echo ""
echo "==> Vault: register (obtain tokens)"
REGISTER_EMAIL="smoke.vault.$(date +%s)@example.com"
REGISTER_RESP=$(curl -s -X POST "${BASE_URL}/ayanlabs/api/vault/auth/register" \
  $(ayanlabs_headers_public "${APP_ID_VAULT}") \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"${REGISTER_EMAIL}\",\"password\":\"Admin123!\",\"firstName\":\"Smoke\",\"lastName\":\"Test\",\"phone\":\"+15550001111\"}") || true

if command -v jq >/dev/null 2>&1; then
  ACCESS_TOKEN=$(echo "$REGISTER_RESP" | jq -r '.data.accessToken // empty')
  REFRESH_TOKEN=$(echo "$REGISTER_RESP" | jq -r '.data.refreshToken // empty')
  export ACCESS_TOKEN REFRESH_TOKEN
fi

if [ -n "${ACCESS_TOKEN:-}" ] && [ -n "${REFRESH_TOKEN:-}" ]; then
  echo "Tokens obtained from register"
  PASS=$((PASS + 1))
else
  echo "Register response (no jq or no token):"
  echo "$REGISTER_RESP" | head -c 400
  echo ""
  if [ -z "${ACCESS_TOKEN:-}" ]; then
    run_skip "Authenticated module tests" "set ACCESS_TOKEN and REFRESH_TOKEN manually"
    echo ""
    echo "Summary: PASS=${PASS} FAIL=${FAIL} SKIP=${SKIP}"
    exit 0
  fi
fi

# --- Authenticated smokes ---
run "Core: role by id" \
  http_ok "${BASE_URL}/api/core/roles/${ROLE_ID_ADMIN}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}")

run "Core: authorization effective" \
  http_ok "${BASE_URL}/api/core/authorization/effective" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}")

run "IPMS: health" \
  http_ok "${BASE_URL}/ayanlabs/api/property-mgmt/health" \
  $(ayanlabs_headers_public "${APP_ID_IPMS}")

run "IPMS: authorization context" \
  http_ok "${BASE_URL}/ayanlabs/api/property-mgmt/authorization/context" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}")

run "CRM: health" \
  http_ok "${BASE_URL}/ayanlabs/api/accounting/health" \
  $(ayanlabs_headers_public "${APP_ID_CRM}")

run "Vault: me" \
  http_ok "${BASE_URL}/ayanlabs/api/vault/me" \
  $(ayanlabs_headers_auth "${APP_ID_VAULT}")

run "StaffIQ: health" \
  http_ok "${BASE_URL}/ayanlabs/api/rideshare/health" \
  $(ayanlabs_headers_public "${APP_ID_STAFFIQ}")

echo ""
echo "Summary: PASS=${PASS} FAIL=${FAIL} SKIP=${SKIP}"
[ "$FAIL" -eq 0 ]
