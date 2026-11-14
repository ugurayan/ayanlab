# Source before running curls:  source docs/curl/env.example.sh
# Copy to env.local.sh and override tokens after login/register.

export BASE_URL="${BASE_URL:-http://localhost:8080}"

# App UUIDs (from platform/src/main/resources/db/default-data.json)
export APP_ID_CORE="1e7c675c-53e5-41cc-ae94-8f09244c8f91"
export APP_ID_IPMS="ad94b2d2-5022-4818-a785-55807ab139ba"
export APP_ID_CRM="5fd9fde7-e513-40fb-9622-9ce3e9699f89"
export APP_ID_VAULT="0cd4d187-f53e-4ebc-94cd-a601ab467094"
export APP_ID_STAFFIQ="f01ae844-85fd-48a0-90ed-8bdd7e0e48a6"

# Seed admin role (core)
export ROLE_ID_ADMIN="4f0018f6-ebb4-438b-8981-404cefdce0a7"

# Set after register/login (see vault auth example)
export ACCESS_TOKEN="${ACCESS_TOKEN:-}"
export REFRESH_TOKEN="${REFRESH_TOKEN:-}"

# Helpers
correlation_id() {
  if command -v uuidgen >/dev/null 2>&1; then
    uuidgen
  else
    python3 -c 'import uuid; print(uuid.uuid4())'
  fi
}

# Headers required on every request (see CONTROLLER_API_HEADERS.md)
ayanlabs_headers_public() {
  echo \
    -H "X-Correlation-Id: $(correlation_id)" \
    -H "X-App-Id: ${1:?app id required}" \
    -H "X-Client-Type: web" \
    -H "X-Client-Language: tr"
}

ayanlabs_headers_auth() {
  local app_id="${1:?app id required}"
  if [ -z "$ACCESS_TOKEN" ] || [ -z "$REFRESH_TOKEN" ]; then
    echo "ERROR: set ACCESS_TOKEN and REFRESH_TOKEN (register/login first)" >&2
    return 1
  fi
  echo \
    -H "Authorization: Bearer ${ACCESS_TOKEN}" \
    -H "X-Correlation-Id: $(correlation_id)" \
    -H "X-App-Id: ${app_id}" \
    -H "X-Client-Type: web" \
    -H "X-Client-Token: ${ACCESS_TOKEN}" \
    -H "X-Client-Refresh-Token: ${REFRESH_TOKEN}" \
    -H "X-Client-Language: tr"
}
