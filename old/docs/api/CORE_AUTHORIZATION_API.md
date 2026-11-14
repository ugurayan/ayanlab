# Core Authorization API

Bu doküman `core` modülündeki **permission / policy group / role** CRUD API’lerini ve “effective authorization” çözümlemesini açıklar.

Header kuralları: [CONTROLLER_API_HEADERS.md](../../CONTROLLER_API_HEADERS.md)

Base: `${BASE_URL}/api/core`  
Swagger: `${BASE_URL}/swagger-ui.html`

---

## Model özeti (kısa)

- **Permission**: En küçük yetki birimi. Kod formatı: `app:module:resource:action` (örn. `ipms:properties:properties:read`)
- **PolicyGroup**: Permission paketidir (app’e göre scope’lanır).
- **Role**: İş rolüdür. App’e `ApplicationRole` ile bağlanır.
- **Atama / çözümleme**:
  - `ProfileRole` → Role
  - Role → (`RolePermission` **ve/veya** `RolePolicyGroup` → `PolicyGroupPermission`)
  - Profile override → `ProfilePermission` (**GRANT** / **DENY**)
  - Sonuç: `AuthorizationResolverService` permission code set’i döner.

---

## Effective authorization

### GET `/authorization/effective`
JWT’deki `profileId` üzerinden effective permission’ları döner.

**curl**

```bash
curl -s "${BASE_URL}/api/core/authorization/effective" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

### GET `/authorization/profiles/{profileId}/effective`
Belirli profile için çözümleme.

**curl**

```bash
curl -s "${BASE_URL}/api/core/authorization/profiles/${PROFILE_ID}/effective" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

---

## Permissions (CRUD)

Controller: `PermissionController`  
Base path: `/permissions`

### POST `/permissions`
Permission oluşturur.

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

### GET `/permissions?appCode={appCode}`
Permission listesi (opsiyonel appCode filtreli).

```bash
curl -s "${BASE_URL}/api/core/permissions?appCode=ipms" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

### GET `/permissions/{id}`

```bash
curl -s "${BASE_URL}/api/core/permissions/${PERMISSION_ID}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

### PUT `/permissions/{id}`

```bash
curl -s -X PUT "${BASE_URL}/api/core/permissions/${PERMISSION_ID}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") \
  -H "Content-Type: application/json" \
  -d '{
    "code": "core:admin:test:read",
    "name": "Test Read (updated)",
    "appCode": "core",
    "module": "admin",
    "resource": "test",
    "action": "read"
  }' | jq .
```

### DELETE `/permissions/{id}`
Soft delete (`isActive=false`).

```bash
curl -s -X DELETE "${BASE_URL}/api/core/permissions/${PERMISSION_ID}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

---

## Roles (CRUD)

Controller: `RoleController`  
Base path: `/roles`

### POST `/roles`

```bash
curl -s -X POST "${BASE_URL}/api/core/roles" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") \
  -H "Content-Type: application/json" \
  -d '{
    "code": "DEMO_ROLE",
    "name": "Demo Role",
    "description": "Created via API"
  }' | jq .
```

### GET `/roles?appId={appId}`
`appId` verilirse `ApplicationRole` üzerinden filtrelenir.

```bash
curl -s "${BASE_URL}/api/core/roles?appId=${APP_ID_IPMS}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

### GET `/roles/{id}`

```bash
curl -s "${BASE_URL}/api/core/roles/${ROLE_ID}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

### PUT `/roles/{id}`

```bash
curl -s -X PUT "${BASE_URL}/api/core/roles/${ROLE_ID}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") \
  -H "Content-Type: application/json" \
  -d '{
    "code": "DEMO_ROLE",
    "name": "Demo Role (updated)",
    "description": "Updated via API"
  }' | jq .
```

### DELETE `/roles/{id}`

```bash
curl -s -X DELETE "${BASE_URL}/api/core/roles/${ROLE_ID}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

---

## Policy Groups (CRUD)

Controller: `PolicyGroupController`  
Base path: `/policy-groups`

### POST `/policy-groups`
`appId` göndermezsen `X-App-Id` header’ından alınır.

```bash
curl -s -X POST "${BASE_URL}/api/core/policy-groups" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") \
  -H "Content-Type: application/json" \
  -d '{
    "appId": "'"${APP_ID_IPMS}"'",
    "code": "IPMS_DEMO_POLICY",
    "name": "IPMS Demo Policy",
    "description": "Created via API"
  }' | jq .
```

### GET `/policy-groups?appId={appId}`

```bash
curl -s "${BASE_URL}/api/core/policy-groups?appId=${APP_ID_IPMS}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

### GET `/policy-groups/{id}`

```bash
curl -s "${BASE_URL}/api/core/policy-groups/${POLICY_GROUP_ID}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

### PUT `/policy-groups/{id}`

```bash
curl -s -X PUT "${BASE_URL}/api/core/policy-groups/${POLICY_GROUP_ID}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") \
  -H "Content-Type: application/json" \
  -d '{
    "code": "IPMS_DEMO_POLICY",
    "name": "IPMS Demo Policy (updated)",
    "description": "Updated via API"
  }' | jq .
```

### DELETE `/policy-groups/{id}`

```bash
curl -s -X DELETE "${BASE_URL}/api/core/policy-groups/${POLICY_GROUP_ID}" \
  $(ayanlabs_headers_auth "${APP_ID_CORE}") | jq .
```

---

## Mappings (CRUD + list)

Detaylı curl örnekleri: [CORE_ASSIGNMENTS_API.md](./CORE_ASSIGNMENTS_API.md)

| Resource | Base path | List filter |
|----------|-----------|-------------|
| Role ↔ Permission | `/api/core/role-permissions` | `?roleId=` |
| Role ↔ Policy | `/api/core/role-policy-groups` | `?roleId=` |
| Policy ↔ Permission | `/api/core/policy-group-permissions` | `?policyGroupId=` |
| App ↔ Role | `/api/core/application-roles` | `?appId=` |
| App ↔ Permission | `/api/core/application-permissions` | `?appId=` |
| Profile ↔ Role | `/api/core/profile-roles` | `?profileId=` |
| Profile ↔ Permission | `/api/core/profile-permissions` | `?profileId=` |
| Profile ↔ Policy | `/api/core/profile-policy-groups` | `?profileId=` |
| Profile resource scope | `/api/core/profile-resource-scopes` | `?profileId=` |

