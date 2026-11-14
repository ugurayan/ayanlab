# Frontend ↔ Backend Auth / Authorization Alignment

> Kaynaklar: `ipms-web/docs/ACCESS-MANAGEMENT.md`, `ROLES-AND-PERMISSIONS.md`,  
> `ayanlab_backend/docs/api/IPMS_AUTHORIZATION.md`

## Özet

Frontend ve backend **aynı ürünü** tanımlıyor ama **farklı dil** kullanıyor. Bu yüzden signup/login sonrası menü ve yetki sapması oluşuyor.

| Konu | Frontend (ipms-web) | Backend (ayanlab_backend) | Durum |
|------|---------------------|---------------------------|--------|
| Rol anahtarları | `property_management_admin` (snake) | `ORGANIZATION_ADMINISTRATOR` (SCREAMING) | Mapping eklendi |
| Rol kataloğu | 7 rol (system_user, maintenance_organization, …) | 8 rol (ACCOUNTANT, LEASING_AGENT, VIEWER, …) | Kısmi örtüşme |
| Permission formatı | module + action (`properties` + `view`) | `ipms:properties:properties:read` | Mapping eklendi (`view`↔`read`) |
| Login | Hâlâ mock (`AuthContext.login`) | JWT + `/api/core/authorization/effective` | FE henüz API login kullanmıyor |
| Signup complete payload | Beklenen: `permissions.modules/menus/policies` | `TokenResponse` + JWT claims (`roles`, `permissions`) | Access summary eklendi |
| Personal signup | Role: `tenant` / `maintenance_staff` | Sadece UserType (`IPMS_TENANT_RENTER`) | Role ataması düzeltildi |
| Org new company | `property_management_admin` veya `maintenance_organization` | Hep `ORGANIZATION_ADMINISTRATOR` | Vendor/Maint → FE map; BE rol genişletmesi TODO |

## Rol eşlemesi (canonical)

| Backend `roleCodes` | Frontend `ROLES` |
|---------------------|------------------|
| `SYSTEM_ADMINISTRATOR` | `system_administrator` |
| `ORGANIZATION_ADMINISTRATOR` | `property_management_admin` |
| `PROPERTY_MANAGER` | `property_management` |
| `TENANT` | `tenant` |
| `MAINTENANCE_STAFF` | `maintenance_staff` |
| `ACCOUNTANT` | `property_management` *(geçici)* |
| `LEASING_AGENT` | `property_management` *(geçici)* |
| `VIEWER` | `system_user` *(geçici)* |
| *(yok)* | `maintenance_organization` | Backend’e eklenecek |

## Permission eşlemesi

Backend: `ipms:{module}:{module}:{action}`  
Frontend action: `view` = backend `read`; `manage` = backend `manage` veya tüm CRUD.

## Yapılan düzeltmeler

1. **FE** `src/utils/backendAuthMapping.js` — role + permission code çevirisi  
2. **FE** `authFromApi` / `AuthorizationContext` — JWT / access payload kullanımı  
3. **BE** personal signup → `TENANT` / `MAINTENANCE_STAFF` Role ataması  
4. **BE** `SignupCompleteResponse` → `access` özeti (`roleCodes`, `permissionCodes`, mapped frontend role)

## Kalan TODO

- [ ] Gerçek API login (mock login yerine vault/IPMS auth)
- [ ] Backend’e `MAINTENANCE_ORGANIZATION` + `SYSTEM_USER` rolleri
- [ ] Login/signup response’ta `menus` + `capabilities` (ACCESS-MANAGEMENT §I)
- [ ] Row-level policy middleware (`POL_SCOPE_*`)
