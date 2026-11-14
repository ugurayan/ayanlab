# Core Assignments API

Rol, permission ve policy atamaları için junction tabloları.

Base: `${BASE_URL}/api/core`

---

## Application roles — `/application-roles`

App ↔ Role bağlantısı (hangi roller bu app'te kullanılabilir).

| Method | Path | Query / Body |
|--------|------|--------------|
| GET | `/application-roles` | `?appId=` |
| GET | `/application-roles/{id}` | |
| POST | `/application-roles` | `{ "appId", "roleId" }` |
| PUT | `/application-roles/{id}` | |
| DELETE | `/application-roles/{id}` | |

```bash
curl -s "${BASE_URL}/api/core/application-roles?appId=${APP_ID_IPMS}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .

curl -s -X POST "${BASE_URL}/api/core/application-roles" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") \
  -H "Content-Type: application/json" \
  -d '{"appId":"'"${APP_ID_IPMS}"'","roleId":"'"${ROLE_ID}"'"}' | jq .
```

---

## Application permissions — `/application-permissions`

App ↔ Permission bağlantısı (hangi permission'lar bu app'te geçerli).

| Method | Path | Query / Body |
|--------|------|--------------|
| GET | `/application-permissions` | `?appId=` |
| GET | `/application-permissions/{id}` | |
| POST | `/application-permissions` | `{ "appId", "permissionId" }` |
| PUT | `/application-permissions/{id}` | |
| DELETE | `/application-permissions/{id}` | |

---

## Profile roles — `/profile-roles`

Profile'a rol atama.

| Method | Path | Query / Body |
|--------|------|--------------|
| GET | `/profile-roles` | `?profileId=` |
| GET | `/profile-roles/{id}` | |
| POST | `/profile-roles` | `{ "profileId", "roleId", "grantedAt?" }` |
| PUT | `/profile-roles/{id}` | |
| DELETE | `/profile-roles/{id}` | |

```bash
curl -s "${BASE_URL}/api/core/profile-roles?profileId=${PROFILE_ID}" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq .
```

---

## Profile permissions — `/profile-permissions`

Profile seviyesinde GRANT/DENY override.

| Method | Path | Query / Body |
|--------|------|--------------|
| GET | `/profile-permissions` | `?profileId=` |
| GET | `/profile-permissions/{id}` | |
| POST | `/profile-permissions` | `{ "profileId", "permissionId", "effect": "GRANT\|DENY" }` |
| PUT | `/profile-permissions/{id}` | |
| DELETE | `/profile-permissions/{id}` | |

---

## Profile policy groups — `/profile-policy-groups`

| Method | Path | Query |
|--------|------|-------|
| GET | `/profile-policy-groups` | `?profileId=` |
| GET | `/profile-policy-groups/{id}` | |
| POST | `/profile-policy-groups` | |
| PUT | `/profile-policy-groups/{id}` | |
| DELETE | `/profile-policy-groups/{id}` | |

---

## Profile resource scopes — `/profile-resource-scopes`

Row-level scope (hangi kaynaklara erişim).

| Method | Path | Query |
|--------|------|-------|
| GET | `/profile-resource-scopes` | `?profileId=` |
| GET | `/profile-resource-scopes/{id}` | |
| POST | `/profile-resource-scopes` | |
| PUT | `/profile-resource-scopes/{id}` | |
| DELETE | `/profile-resource-scopes/{id}` | |

---

## Role permissions — `/role-permissions`

Direct role → permission (legacy, ADMIN seed).

| Method | Path | Query |
|--------|------|-------|
| GET | `/role-permissions` | `?roleId=` |
| GET/POST/PUT/DELETE | `/{id}` | |

---

## Role policy groups — `/role-policy-groups`

Role → policy group (IPMS matris rolleri).

| Method | Path | Query |
|--------|------|-------|
| GET | `/role-policy-groups` | `?roleId=` |
| GET/POST/PUT/DELETE | `/{id}` | |

---

## Policy group permissions — `/policy-group-permissions`

Policy group → permission listesi.

| Method | Path | Query |
|--------|------|-------|
| GET | `/policy-group-permissions` | `?policyGroupId=` |
| GET/POST/PUT/DELETE | `/{id}` | |
