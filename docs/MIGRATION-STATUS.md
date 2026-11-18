# Migration status: `tmp/auth` → `ayanlab-platform`

## Target layout

| Legacy (`tmp/auth`) | Current |
|---------------------|---------|
| `org.ugurayan.auth.data.dto.*` | `ayanlab-platform-core` / backend `entity.*` (`schema = core`) |
| `org.ugurayan.auth.service.*` | `ayanlab-platform-core` + `ayanlab-platform-backend` |
| `org.ugurayan.auth.controller.*` | `AuthController` / domain `@AuthPlatformApi` controllers |
| MySQL / `/api/v1/*` | PostgreSQL / `/auth/api/*`, `/core/api/*` |

## Entity mapping (tmp DTO → platform)

| tmp/auth DTO | Platform entity | Notes |
|--------------|-----------------|-------|
| App | `App` | Same |
| User, UserType, Role, Permission, Module, Organization | Same names | Profile-centric extensions added |
| UserRoles, UserPermissions, … | `ProfileRole`, `ProfilePermission`, … | **Redesigned** — do not recreate join tables |
| AppUsers, AppRoles, … | `Role.appId`, `ApplicationPermission`, `OrganizationUser`, … | App/org scope |
| EmailTemplate | `NotificationTemplate` | Unified templates |
| TwilioSms | `TwilioSmsLog` | Outbound SMS audit |
| PasswordHistory, LoginHistory, Translation, LogError, IpLog | Same (added) | Services + auth hooks |
| Department | `Department` | Was empty stub in tmp |

`old/core` entity set ⊆ `ayanlab-platform` (verified).

## Service mapping

| tmp/auth service | Platform |
|------------------|----------|
| LoginService | `AuthService` + `AuthController` (`ayanlab-platform-backend`) |
| JwtTokenProviderService | `JwtService` |
| UserService, RoleService, PermissionService, OrganizationService, ApplicationService | `*Service` in domain lib |
| EmailTemplateService | `NotificationTemplateService` |
| EmailService | `EmailDispatchService` |
| TwilioSmsService | `TwilioSmsService` |
| PasswordHistoryService | `PasswordHistoryService` (+ change-password hook) |
| LoginHistoryService | `LoginHistoryService` (+ login hook) |
| LogErrorService | `LogErrorService` |
| DepartmentService, TranslationService | Implemented (were stubs or thin) |
| CommonService, CookieService, EncryptDecryptService | Replaced by `CoreContext`, JWT, Spring crypto |

## API parity

- Old: `PUT /api/v1/login/sign-in` → New: `POST /auth/api/login`
- Old CRUD controllers → `/auth/api/users`, `/roles`, `/departments`, `/translations`, …
- Legacy wrapper: `AuthLegacyV1Controller` (platform backend)

## Cleanup checklist

- [x] Platform DB only (`ayanlab_platform` / `core`)
- [x] tmp/auth domain services ported or superseded
- [x] `old/core` entities covered by platform
- [x] `libs/` removed (code lives in `ayanlab-platform-core` / backend)
- [x] Remove `tmp/` after stack smoke test
- [x] Remove `old/` after stack smoke test

## Smoke test

```bash
./scripts/validate-env-local.sh
ENV=local ./scripts/up-platform.sh
curl -s -o /dev/null -w "%{http_code}\n" -X POST http://localhost:8102/auth/api/login -H 'Content-Type: application/json' -d '{}'
# expect 400 (validation), not connection refused
```