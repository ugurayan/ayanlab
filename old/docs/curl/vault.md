# Vault — curl örnekleri

Base: `${BASE_URL}/ayanlabs/api/vault`  
App ID: `APP_ID_VAULT` = `0cd4d187-f53e-4ebc-94cd-a601ab467094`

Önce: `source docs/curl/env.example.sh`

---

## Public — register (token üretir, smoke test için kullanın)

```bash
REGISTER_EMAIL="vault.test.$(date +%s)@example.com"

RESP=$(curl -s -X POST "${BASE_URL}/ayanlabs/api/vault/auth/register" \
  $(ayanlabs_headers_public "${APP_ID_VAULT}") \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"${REGISTER_EMAIL}\",
    \"password\": \"Admin123!\",
    \"firstName\": \"Vault\",
    \"lastName\": \"Tester\",
    \"phone\": \"+15550009999\"
  }")

echo "$RESP" | jq .

# Token'ları ortama aktarın:
export ACCESS_TOKEN=$(echo "$RESP" | jq -r '.data.accessToken // .data.access_token // empty')
export REFRESH_TOKEN=$(echo "$RESP" | jq -r '.data.refreshToken // .data.refresh_token // empty')
echo "ACCESS_TOKEN set: ${ACCESS_TOKEN:0:20}..."
```

---

## Public — subscription planları

```bash
curl -s "${BASE_URL}/ayanlabs/api/vault/subscriptions/plans" \
  $(ayanlabs_headers_public "${APP_ID_VAULT}") | jq .
```

---

## Auth — authorization context

```bash
curl -s "${BASE_URL}/ayanlabs/api/vault/authorization/context" \
  $(ayanlabs_headers_auth "${APP_ID_VAULT}") | jq .
```

---

## Auth — me (vault profile)

```bash
curl -s "${BASE_URL}/ayanlabs/api/vault/me" \
  $(ayanlabs_headers_auth "${APP_ID_VAULT}") | jq .
```

---

## Auth — login (endpoint mevcut; implementasyon devam ediyor olabilir)

```bash
curl -s -X POST "${BASE_URL}/ayanlabs/api/vault/auth/login" \
  $(ayanlabs_headers_public "${APP_ID_VAULT}") \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@vault.local",
    "password": "Admin123!",
    "appKey": "vault"
  }' | jq .
```
