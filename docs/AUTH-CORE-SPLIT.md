# Unified platform (ayanlab-platform)

Previous split (**auth :8101** + **core :8102**) is merged into one backend.

| Concern | Path | Package |
|---------|------|---------|
| Login, JWT, RBAC CRUD | `/auth/api/**` | `com.ayanlab.auth.controller` + `@AuthPlatformApi` in platform-backend |
| Payments, bank, invoices | `/core/api/**` | `@CoreBusinessApi` controllers |

**Deploy:** `ENV=local ./scripts/up-platform.sh`  
**Database:** `PLATFORM_DB_NAME` (default `ayanlab_platform`), schema `core` — Hibernate tables + seed loaders.  
**Config:** `ayanlabs.expose.auth-platform-apis: true` and `core-business-apis: true` in `ayanlab-platform-backend` `application.yml`.