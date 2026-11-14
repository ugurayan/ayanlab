# IPMS Auth / Access — Implementation Task List

> **Goal:** Canlıya hazır Signup, SignIn, Forgot Password + role/permission/policy/access; backend kaynaklı, FE session’da saklanır, menüler buna göre açılır.  
> **Denetim tarihi:** 2026-07-11  
> Durum: `✅ TAMAM` | `🟡 KISMİ` | `❌ EKSİK`

---

## Faz 0 — Sözleşme (FE ↔ BE)

| ID | Task | Durum | Not |
|----|------|-------|-----|
| T0.1 | Role mapping FE snake ↔ BE SCREAMING | 🟡 | Mapping var; katalogda 2 rol eksik |
| T0.2 | Permission format `ipms:{mod}:{mod}:{action}` | ✅ | Catalog + FE mapping |
| T0.3 | Access payload sözleşmesi (`roleCodes`, `permissionCodes`, `modules`, `menus`, `policies`, `capabilities`, `frontendRole`) | 🟡 | modules/roles/perms var; menus/policies/capabilities yok |
| T0.4 | Alignment dokümanları | ✅ | `AUTH_FRONTEND_BACKEND_ALIGNMENT.md`, `AUTH-BACKEND-ALIGNMENT.md` |

---

## Faz 1 — Signup (backend)

| ID | Task | Durum | Not |
|----|------|-------|-----|
| T1.1 | Step-1 register (public) + email/phone check | ✅ | |
| T1.2 | Email + mobile verification + resend | ✅ | Kod TTL configurable |
| T1.3 | Step-2 profile type PERSONAL / ORGANIZATION | ✅ | |
| T1.4 | Step-3 personal: Tenant, Property Owner, Vendor, Maintenance | 🟡 | Role: Owner→TENANT, Vendor→MAINTENANCE_STAFF (ayrı rol yok) |
| T1.5 | Step-3 new company + company type + org admin atama | ✅ | Hepsi `ORGANIZATION_ADMINISTRATOR` |
| T1.6 | Join invite kodu | ✅ | |
| T1.7 | Join search + request + admin approve/reject | ✅ | Approve → PROPERTY_MANAGER |
| T1.8 | Complete → JWT + redirect + access summary | 🟡 | menus/policies/capabilities eksik |
| T1.9 | Join pending complete engeli | ✅ | `PENDING_JOIN_APPROVAL` |

---

## Faz 2 — Auth (backend SignIn / Forgot / Session)

| ID | Task | Durum | Not |
|----|------|-------|-----|
| T2.1 | `POST /ayanlabs/api/vault/auth/login` gerçek implementasyon | ✅ | |
| T2.2 | Refresh token | ✅ | |
| T2.3 | Logout | 🟡 | Client discard; blacklist TODO |
| T2.4 | Forgot password | 🟡 | Endpoint var; email log-only |
| T2.5 | Reset password | ✅ | |
| T2.6 | Change password (authenticated) | ✅ | Vault + IPMS settings |
| T2.7 | Login response’ta `access` (signup ile aynı şekil) | ✅ | `TokenResponse.access` |
| T2.8 | IPMS `/me` (profile + roles + access) | ❌ | Vault `/me` var; IPMS yok |
| T2.9 | Effective permissions API | ✅ | `/api/core/authorization/effective` |

---

## Faz 3 — Role / Permission / Policy kataloğu (DB seed)

| ID | Task | Durum | Not |
|----|------|-------|-----|
| T3.1 | IPMS 8 rol + permission matrix seed | ✅ | `authorization-catalog.json` |
| T3.2 | Rol: `MAINTENANCE_ORGANIZATION` | ✅ | |
| T3.3 | Rol: `SYSTEM_USER` | ✅ | |
| T3.4 | Rol: `PROPERTY_OWNER` (kişisel) | ✅ | |
| T3.5 | Rol: `VENDOR` (kişisel) | ✅ | |
| T3.6 | Default permission grants per role | ✅ | Catalog `appRoleAssignments` |
| T3.7 | Policy codes: `POL_SCOPE_*` | 🟡 | AccessSummary default map; row middleware yok |
| T3.8 | Capabilities seed | 🟡 | AccessSummary defaults |
| T3.9 | Menu key seed / menu→module matrix backend | ✅ | `Menu` + `RoleMenu` + menu-catalog.json |
| T3.10 | Signup atamasını yeni rollere bağla | ✅ | |

**Önerilen rol matrisi (karar):**

| Signup seçimi | Backend Role.code | FE `frontendRole` |
|---------------|-------------------|-------------------|
| Personal Tenant/Renter | `TENANT` | `tenant` |
| Personal Property Owner | `PROPERTY_OWNER` (yeni) | `tenant` *veya* ayrı UI | 
| Personal Vendor | `VENDOR` (yeni) | `maintenance_staff` |
| Personal Maintenance | `MAINTENANCE_STAFF` | `maintenance_staff` |
| New Co Landlord/HOA/RE | `ORGANIZATION_ADMINISTRATOR` | `property_management_admin` |
| New Co Vendor/Maintenance | `MAINTENANCE_ORGANIZATION` (yeni) | `maintenance_organization` |
| Join invite/request | invite role / `PROPERTY_MANAGER` | `property_management` |
| Platform ops (seed) | `SYSTEM_ADMINISTRATOR` / `SYSTEM_USER` | `system_*` |

---

## Faz 4 — Access builder (backend ortak servis)

| ID | Task | Durum | Not |
|----|------|-------|-----|
| T4.1 | `AccessSummaryService` | ✅ | |
| T4.2 | Signup complete bu servisi kullansın | ✅ | |
| T4.3 | Login bu servisi kullansın | ✅ | |
| T4.4 | `/me` ve `/authorization/effective` zenginleştirme | 🟡 | effective roles/perms var |
| T4.5 | JWT claims tutarlılığı | 🟡 | roles/permissions var; menus body’de |

---

## Faz 5 — Frontend session & API client

| ID | Task | Durum | Not |
|----|------|-------|-----|
| T5.1 | Signup wizard UI + branching | ✅ | |
| T5.2 | Signup gerçek API (`USE_MOCK_SIGNUP=false`) | ✅ | |
| T5.3 | `authApi.js` (login, refresh, logout, forgot, reset) | ✅ | |
| T5.4 | `AuthContext.login` mock → API | ✅ | `USE_MOCK_AUTH` ile mock opsiyonel |
| T5.5 | Session: localStorage `user` + tokens | ✅ | |
| T5.6 | Sayfa yenilemede token + `/me` veya JWT ile session restore | 🟡 | localStorage user restore; `/me` yok |
| T5.7 | `authFromApi` login + signup ortak | ✅ | |
| T5.8 | Forgot / Reset password sayfaları + route | ✅ | |
| T5.9 | Login “Forgot password?” linki | ✅ | |
| T5.10 | Change password Settings → API | ❌ | Dead button |

---

## Faz 6 — Menü & UI ACL

| ID | Task | Durum | Not |
|----|------|-------|-----|
| T6.1 | `roleNavigation.js` rol ağaçları | ✅ | |
| T6.2 | Module gate `AuthorizationContext` + permissionCodes | ✅ | |
| T6.3 | Backend `menus[]` ile nav filtre | ✅ | |
| T6.4 | Capabilities ile UI buton gate | 🟡 | `hasCapability` eklendi; butonlar henüz bağlanmadı |
| T6.5 | Policy `POL_JOIN_PENDING` → limited UI | ❌ | |
| T6.6 | Mock login kaldır / demo sadece DEV | 🟡 | `USE_MOCK_AUTH` |

---

## Faz 7 — Profile (backend + FE)

| ID | Task | Durum | Not |
|----|------|-------|-----|
| T7.1 | IPMS current user profile DTO (org, company type, personal type, roles) | ❌ | |
| T7.2 | FE profile/settings bu DTO’yu kullansın | ❌ | |
| T7.3 | Onboarding/profile entity sync (signup session → profile extras) | 🟡 | Signup session var |

---

## Faz 8 — Güvenlik & prod sertleştirme

| ID | Task | Durum | Not |
|----|------|-------|-----|
| T8.1 | Public endpoints permitAll (register, login, forgot) | ✅ | Login path pattern OK; logic stub |
| T8.2 | Verify/step-2+ JWT zorunlu | ✅ | |
| T8.3 | Rate limit verify/forgot (öneri) | ❌ | |
| T8.4 | Reset token e-posta (Twilio/email gerçek gönderim) | 🟡 | Email adapter var; forgot log-only |
| T8.5 | SMS verify gerçek Twilio dispatch | ❌ | Placeholder |

---

## Faz 9 — Doğrulama

| ID | Task | Durum | Not |
|----|------|-------|-----|
| T9.1 | Curl: signup full happy path → dashboard access | 🟡 | Curl docs var |
| T9.2 | Curl: login → aynı access | ❌ | Login çalışmıyor |
| T9.3 | Curl: forgot → reset → login | ❌ | |
| T9.4 | FE E2E: signup → localStorage → menü doğru | ❌ | |
| T9.5 | FE E2E: login → menü doğru | ❌ | |

---

## Uygulama sırası (önerilen)

```
1. Faz 3  → katalog rolleri + default perms + policies + capabilities (+ menus map)
2. Faz 4  → AccessSummaryService
3. Faz 1.4/1.8 + T3.10 → signup yeni rollere + full access
4. Faz 2   → login/refresh/logout + access + /me
5. Faz 5   → FE authApi + AuthContext + forgot/reset pages
6. Faz 6   → menü/capability gate
7. Faz 7   → profile
8. Faz 8–9 → sertleştirme + test
```

---

## Özet sayaç

| Durum | Adet (yaklaşık) |
|-------|-----------------|
| ✅ TAMAM | ~22 |
| 🟡 KISMİ | ~16 |
| ❌ EKSİK | ~28 |

**Kritik blokörler (canlı öncesi zorunlu):** T2.1, T2.7, T3.2–T3.5, T3.7–T3.8, T4.1–T4.3, T5.3–T5.5, T5.8

---

## Onaylanan kararlar (2026-07-11)

1. **Property Owner / Vendor:** Ayrı backend Role (`PROPERTY_OWNER`, `VENDOR`). Org vendor/maint admin → `MAINTENANCE_ORGANIZATION`.
2. **Session (ipms-web):** **localStorage** (token + user/access).  
   - Cookie’ler bazen “tüm çerezleri engelle” / privacy ayarlarında kırılır; first-party auth cookie genelde çalışır ama SPA+CORS+CSRF maliyeti var.  
   - localStorage her zaman aynı origin’de öngörülebilir; XSS’e karşı kısa access TTL + XSS hijyeni. İleride httpOnly cookie opsiyonel sertleştirme.
3. **Menüler:** Backend `Menu` (+ `RoleMenu`) entity, app-scoped. Login/signup-complete’te `access.menus` döner; FE session’da tutar, sonraki isteklerde menü listesini tekrar çekmez.
4. **SYSTEM_ADMINISTRATOR:** grantAll. **SYSTEM_USER:** dar default (read: dashboard/users/reports/settings); ek haklar `ProfilePermission` / admin grant ile.
