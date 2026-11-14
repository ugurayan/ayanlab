# IPMS Authorization Seed

UI **Permission Matrix** (22 modül × 8 aksiyon = **176 permission**) için seed verisi.

## Dosyalar

| Dosya | Açıklama |
|-------|----------|
| `platform/src/main/resources/db/authorization-catalog.json` | IPMS matrisi, roller, policy grupları, demo kullanıcılar, profile ek yetkileri |
| `platform/src/main/resources/db/permission-catalog.json` | Core / CRM / Vault / StaffIQ explicit permission tanımları |
| `platform/.../AuthorizationCatalogLoader.java` | Startup loader (`@Order(15)`) — idempotent, tekrar çalıştırılabilir |

Aktifleştirme: `platform.db.load-authorization-catalog=true` (varsayılan `true`).

## Permission kod formatı

```
ipms:{module}:{module}:{action}
```

**Modüller (22):** dashboard, properties, tenants, applicants, rental_owners, associations, violations, leasing, accounting, maintenance, vendors, work_orders, tasks, communication, files, reports, analytics, addons, settings, users, roles, permissions

**Aksiyonlar (8):** read (View), create, update, delete, manage, approve, export, import

Örnek: `ipms:properties:properties:read` → View Properties

## Roller

| Rol | Policy Group | Açıklama |
|-----|--------------|----------|
| SYSTEM_ADMINISTRATOR | IPMS_ROLE_SYSTEM_ADMINISTRATOR | Tüm 176 permission |
| ORGANIZATION_ADMINISTRATOR | IPMS_ROLE_ORGANIZATION_ADMINISTRATOR | 176 − 39 deny ≈ 137 |
| PROPERTY_MANAGER | IPMS_ROLE_PROPERTY_MANAGER | ~51 permission |
| ACCOUNTANT | IPMS_ROLE_ACCOUNTANT | ~19 permission |
| MAINTENANCE_STAFF | IPMS_ROLE_MAINTENANCE_STAFF | ~13 permission |
| LEASING_AGENT | IPMS_ROLE_LEASING_AGENT | ~25 permission |
| TENANT | IPMS_ROLE_TENANT | ~9 permission |
| VIEWER | IPMS_ROLE_VIEWER | 5 read-only |

## Profile yapısı

```
User → UserProfile (ipms app) → ProfileRole → Role → RolePolicyGroup → PolicyGroup → Permissions
                              → ProfilePermission (GRANT/DENY override)
```

Rol dışında profile'a ek permission örnekleri (`profilePermissionExtras`):

- `manager@ipm-app.com` → GRANT `analytics:export`, GRANT `users:create`
- `tenant@ipm-app.com` → GRANT `reports:read`, DENY `accounting:export`

## Demo kullanıcılar

| Email | Rol | Şifre |
|-------|-----|-------|
| admin@ipm-app.com | System Administrator | Admin123! |
| owner@ipm-app.com | Organization Administrator | Admin123! |
| manager@ipm-app.com | Property Manager | Admin123! |
| leasing@ipm-app.com | Leasing Agent | Admin123! |
| accountant@ipm-app.com | Accountant | Admin123! |
| maintenance@ipm-app.com | Maintenance Staff | Admin123! |
| tenant@ipm-app.com | Tenant | Admin123! |
| demo@demo.com | System Administrator | Admin123! |

## Doğrulama curl

```bash
source docs/curl/env.example.sh
# Vault register veya mevcut token ile:
curl -s "${BASE_URL}/api/core/authorization/effective" \
  $(ayanlabs_headers_auth "${APP_ID_IPMS}") | jq '.data.permissionCodes | length'
```

Property Manager için beklenen: rol permission'ları + profile extras (ör. analytics export).
