# CRM (Accounting) — curl örnekleri

Base: `${BASE_URL}/ayanlabs/api/accounting`  
App ID: `APP_ID_CRM` = `5fd9fde7-e513-40fb-9622-9ce3e9699f89`

Önce: `source docs/curl/env.example.sh` ve token ayarlayın.

---

## Public — health

```bash
curl -s "${BASE_URL}/ayanlabs/api/accounting/health" \
  $(ayanlabs_headers_public "${APP_ID_CRM}") | jq .
```

---

## Public — subscription planları

```bash
curl -s "${BASE_URL}/ayanlabs/api/accounting/subscriptions/plans" \
  $(ayanlabs_headers_public "${APP_ID_CRM}") | jq .
```

---

## Auth — authorization context

```bash
curl -s "${BASE_URL}/ayanlabs/api/accounting/authorization/context" \
  $(ayanlabs_headers_auth "${APP_ID_CRM}") | jq .
```

---

## Auth — accounts listesi

```bash
curl -s "${BASE_URL}/ayanlabs/api/accounting/accounts" \
  $(ayanlabs_headers_auth "${APP_ID_CRM}") | jq .
```
