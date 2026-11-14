# Core module — curl örnekleri

Base: `${BASE_URL}/api/core`  
App ID: `APP_ID_CORE` = `1e7c675c-53e5-41cc-ae94-8f09244c8f91`

Önce: `source docs/curl/env.example.sh`

Tam API referansı: [CORE_API_INDEX.md](../api/CORE_API_INDEX.md)

---

## Public — subscription plan listesi (token gerekmez)

```bash
curl -s "${BASE_URL}/api/core/subscription-plans?appId=${APP_ID_IPMS}&activeOnly=true" \
  $(ayanlabs_headers_public "${APP_ID_IPMS}") | jq .
```

---

## Public — profile subscription catalog

```bash
curl -s "${BASE_URL}/api/core/profile-subscriptions/catalog?appId=${APP_ID_VAULT}" \
  $(ayanlabs_headers_public "${APP_ID_VAULT}") | jq .
```

---

## Auth — rol getir (seed ADMIN rolü)

```bash
curl -s "${BASE_URL}/api/core/roles/${ROLE_ID_ADMIN}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

---

## Auth — efektif yetkiler (policy resolver)

JWT'deki `profileId` ile çalışır. Register sonrası token'da profile olmalı.

```bash
curl -s "${BASE_URL}/api/core/authorization/effective" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

---

## Auth — policy group listesi

```bash
curl -s "${BASE_URL}/api/core/policy-groups?appId=${APP_ID_IPMS}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

---

## Auth — uygulama listesi (tek kayıt)

```bash
curl -s "${BASE_URL}/api/core/apps/${APP_ID_CORE}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

---

## Auth — permission oluştur (POST örneği)

```bash
curl -s -X POST "${BASE_URL}/api/core/permissions" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") \
  -H "Content-Type: application/json" \
  -d '{
    "code": "core:admin:test:read",
    "name": "Test Read",
    "description": "Smoke test permission",
    "appCode": "core",
    "module": "admin",
    "resource": "test",
    "action": "read"
  }' | jq .
```

---

## Auth — permission listesi

```bash
curl -s "${BASE_URL}/api/core/permissions?appCode=ipms" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

---

## Auth — role listesi (app'e bağlı)

```bash
curl -s "${BASE_URL}/api/core/roles?appId=${APP_ID_IPMS}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

---

## Auth — user listesi

```bash
curl -s "${BASE_URL}/api/core/users?appId=${APP_ID_IPMS}&page=0&size=20" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .
```

---

## Auth — profile listesi

```bash
curl -s "${BASE_URL}/api/core/profiles?appId=${APP_ID_IPMS}" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .
```

---

## Auth — application role listesi

```bash
curl -s "${BASE_URL}/api/core/application-roles?appId=${APP_ID_IPMS}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

---

## Auth — profile role listesi

```bash
curl -s "${BASE_URL}/api/core/profile-roles?profileId=${PROFILE_ID}" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .
```

---

## Email Templates — create/update with parametersJson

```bash
curl -s -X POST "${BASE_URL}/api/core/templates" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") \
  -H "Content-Type: application/json" \
  -d '{
    "code": "welcome-email",
    "type": "EMAIL",
    "language": "en",
    "subject": "Welcome {{firstName}}",
    "body": "Hi {{firstName}}, your account for {{appId}} is ready.",
    "parametersJson": "[{\"name\":\"firstName\",\"type\":\"STRING\",\"required\":true},{\"name\":\"appId\",\"type\":\"STRING\",\"required\":true}]"
  }' | jq .
```

---

## Email — preview template

```bash
curl -s -X POST "${BASE_URL}/api/core/emails/preview-template" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") \
  -H "Content-Type: application/json" \
  -d '{
    "templateCode": "welcome-email",
    "language": "en",
    "params": {
      "firstName": "Ugur",
      "appId": "ipms"
    }
  }' | jq .
```

---

## Email — send template

```bash
curl -s -X POST "${BASE_URL}/api/core/emails/send-template" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") \
  -H "Content-Type: application/json" \
  -d '{
    "to": "ugur.ayan@gmail.com",
    "templateCode": "welcome-email",
    "language": "en",
    "params": {
      "firstName": "Ugur",
      "appId": "ipms"
    }
  }' | jq .
```
