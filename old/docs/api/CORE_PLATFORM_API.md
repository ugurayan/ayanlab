# Core Platform API

Base: `${BASE_URL}/api/core`  
Önce: `source docs/curl/env.example.sh`

---

## Apps — `/apps`

| Method | Path | Query |
|--------|------|-------|
| GET | `/apps` | `page`, `size`, `code`, `name` |
| GET | `/apps/{id}` | |
| POST | `/apps` | body: App |
| PUT | `/apps/{id}` | body: App |
| DELETE | `/apps/{id}` | soft delete |

```bash
curl -s "${BASE_URL}/api/core/apps?page=0&size=20" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .

curl -s "${BASE_URL}/api/core/apps/${APP_ID_CORE}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

---

## Organizations — `/organizations`

| Method | Path | Query |
|--------|------|-------|
| GET | `/organizations` | `page`, `size`, `appId`, `code`, `name` |
| GET | `/organizations/{id}` | |
| POST | `/organizations` | |
| PUT | `/organizations/{id}` | |
| DELETE | `/organizations/{id}` | |

```bash
curl -s "${BASE_URL}/api/core/organizations?appId=${APP_ID_IPMS}&page=0&size=20" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

---

## Modules — `/modules`

| Method | Path | Query |
|--------|------|-------|
| GET | `/modules` | `page`, `size`, `appId`, `code`, `name` |
| GET | `/modules/{id}` | |
| POST | `/modules` | |
| PUT | `/modules/{id}` | |
| DELETE | `/modules/{id}` | |

---

## Users — `/users`

| Method | Path | Query |
|--------|------|-------|
| GET | `/users` | `page`, `size`, `appId` |
| GET | `/users/{id}` | |
| POST | `/users` | |
| PUT | `/users/{id}` | |
| DELETE | `/users/{id}` | |

```bash
curl -s "${BASE_URL}/api/core/users?appId=${APP_ID_IPMS}&page=0&size=20" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .
```

---

## Profiles — `/profiles`

| Method | Path | Query |
|--------|------|-------|
| GET | `/profiles` | `appId` **veya** `userId` |
| GET | `/profiles/{id}` | |
| POST | `/profiles` | |
| PUT | `/profiles/{id}` | |
| DELETE | `/profiles/{id}` | |

```bash
curl -s "${BASE_URL}/api/core/profiles?appId=${APP_ID_IPMS}" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .

curl -s "${BASE_URL}/api/core/profiles?userId=${USER_ID}" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .
```

---

## User types — `/user-types`

| Method | Path |
|--------|------|
| GET | `/user-types` |
| GET | `/user-types/{id}` |
| POST | `/user-types` |
| PUT | `/user-types/{id}` |
| DELETE | `/user-types/{id}` |

---

## Notification templates — `/templates`

| Method | Path | Query |
|--------|------|-------|
| GET | `/templates` | `page`, `size`, `appId`, `type`, `code` |
| GET | `/templates/{id}` | |
| POST | `/templates` | |
| PUT | `/templates/{id}` | |
| DELETE | `/templates/{id}` | |
