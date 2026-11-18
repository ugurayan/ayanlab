# Authorization v2 Migration Plan (enforcement + permission model)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make permission/role/policy-group authorization actually enforced in every AyanLab app, and move the model to app-scoped roles plus a 3-segment permission code with wildcard support, without losing existing data.

**Architecture:** Enforcement (aspects, permission matching, scope checks) moves into `ayanlab-platform-core`, which every app already depends on. The platform backend stays the single owner of the `core` schema. Each app owns an authorization catalog (JSON) that is imported idempotently: platform catalogs are imported at platform startup, and app catalogs (HRM first) are published to an internal platform endpoint when the app starts. An idempotent startup migration rewrites existing rows (permission codes, global roles) into the v2 shape.

**Tech Stack:** Java 21, Spring Boot 3.3.5, Spring Data JPA (Hibernate `ddl-auto: update`, no Flyway), Spring AOP, JJWT, Jackson, JUnit 5.

**Status (2026-11-18):** Phases 1–3 are implemented. The platform builds and its tests pass. The HRM, CRM, Vault, Booking and Comm backends build and their catalog tests pass. Both UIs typecheck and build. Not yet verified against a running database: the startup migration (`AuthorizationModelMigration`), the catalog publish from the apps, and the `authzVersion` check. PMS still does not compile (pre-existing), so its catalog stays in the platform (`db/authz/ipms.json`).

## Global Constraints

- Permission code format: `{app}:{resource}:{action}`. Each segment matches `[a-z][a-z0-9_]*`; a resource may be dotted (`lease.document`). Grants may use `*` for resource and/or action.
- Resource names are plural snake_case and equal the existing module names (`shifts`, `job_roles`, `users`). This keeps the matrix data unchanged.
- Action vocabulary: `read, create, update, delete, manage, approve, review, export, import, share, search`. Only `*` implies every action. `manage` is an ordinary action, because the existing IPMS matrices grant it next to explicit action lists and making it imply everything would widen access.
- `Role` belongs to exactly one app (`appId`), and optionally to one organization (`organizationId`, null means a system template).
- Fail closed: no security context means no permission and no data scope. Frontend `canAccess` returns false when the permission list is empty.
- No schema tooling change. DDL comes from JPA entities. Data fixes run as an idempotent `CommandLineRunner` that is ordered before the seed loaders.
- Internal catalog endpoint is protected by `X-Internal-Token` = `PLATFORM_INTERNAL_TOKEN`. If the token is unset, the endpoint rejects every call.
- Controllers keep the `@ClientHeaders ClientHeadersPayload` + `tracedCall` pattern (see `.cursor/skills/ayanlabs-controller-headers`).

---

## Phase 1: Enforcement (security fixes)

### Task 1: Permission codes v2 and matcher (platform-core)

**Files:**
- Modify: `ayanlab-platform/ayanlab-platform-core/src/main/java/com/ayanlab/core/util/PermissionCodes.java`
- Modify: `ayanlab-platform/ayanlab-platform-core/src/main/java/com/ayanlab/core/provider/AppSecurityContext.java`
- Modify: `ayanlab-platform/ayanlab-platform-core/pom.xml` (test + aop dependencies)
- Test: `ayanlab-platform/ayanlab-platform-core/src/test/java/com/ayanlab/core/util/PermissionCodesTest.java`

**Interfaces (Produces):**
- `PermissionCodes.of(String app, String resource, String action) -> String`
- `PermissionCodes.isValid(String code) -> boolean` (concrete code, no wildcard)
- `PermissionCodes.isValidGrant(String code) -> boolean` (wildcards allowed)
- `PermissionCodes.fromLegacy(String code) -> Optional<String>` (4-segment `app:module:resource:action` → `app:resource:action`, drops module)
- `PermissionCodes.implies(String grant, String required) -> boolean`
- `PermissionCodes.anyImplies(Collection<String> grants, String required) -> boolean`
- `AppSecurityContext.AppContext.hasPermission(String)` delegates to `anyImplies` and normalizes legacy 4-segment input.

- [ ] Write failing tests: exact match, `*` resource, `*` action, `manage` implies `read`, `manage` does not cross resources or apps, legacy normalization, invalid codes rejected.
- [ ] Run `mvn -q -pl ayanlab-platform-core test` and confirm the new tests fail.
- [ ] Implement `PermissionCodes` and update `hasPermission`.
- [ ] Re-run the tests and confirm they pass.

### Task 2: Move enforcement aspects into platform-core

The aspects currently live in `ayanlab-platform-backend`. App backends only depend on platform-core and only scan `com.ayanlab.core.config`, `com.ayanlab.security` and their own package, so `@RequireAppRole` / `@RequirePermission` currently do nothing in HRM, CRM, Vault, PMS, Booking and Comm.

**Files:**
- Create: `ayanlab-platform/ayanlab-platform-core/src/main/java/com/ayanlab/security/authz/RequirePermissionAspect.java`
- Create: `ayanlab-platform/ayanlab-platform-core/src/main/java/com/ayanlab/security/authz/RequireAppRoleAspect.java`
- Delete: `ayanlab-platform/ayanlab-platform-backend/src/main/java/com/ayanlab/core/annotation/RequirePermissionAspect.java`
- Delete: `ayanlab-platform/ayanlab-platform-backend/src/main/java/com/ayanlab/core/annotation/RequireAppRoleAspect.java`
- Modify: `ayanlab-platform/ayanlab-platform-core/pom.xml` (`spring-boot-starter-aop`)
- Test: `ayanlab-platform/ayanlab-platform-core/src/test/java/com/ayanlab/security/authz/RequirePermissionAspectTest.java`

**Behavior:**
- A method-level annotation wins over a class-level one.
- A class-level annotation applies to every method that has no annotation of its own.
- Any one of the listed permissions is sufficient (OR semantics), matched with `PermissionCodes.anyImplies`.

- [ ] Write a failing test using `AspectJProxyFactory`: class-level guard, method override, wildcard grant, missing context → `ForbiddenException`.
- [ ] Implement the aspects with `@Around("@within(..) || @annotation(..)")` and resolve the effective annotation.
- [ ] Delete the backend copies and run `mvn -q install` on the platform.

### Task 3: Resource scopes and app key in the token; fail-closed scope checks

**Files:**
- Modify: `ayanlab-platform/ayanlab-platform-core/src/main/java/com/ayanlab/core/data/dto/EffectiveAuthorization.java` (add `appCode`)
- Modify: `ayanlab-platform/ayanlab-platform-backend/src/main/java/com/ayanlab/security/service/JwtService.java` (claims `appKey`, `scopes`)
- Modify: `ayanlab-platform/ayanlab-platform-core/src/main/java/com/ayanlab/security/filter/JwtAuthenticationFilter.java` (parse `scopes`)
- Modify: `ayanlab-platform/ayanlab-platform-core/src/main/java/com/ayanlab/core/provider/AuthorizationScopeSupport.java`

**Behavior:**
- `scopes` claim: a list of `{ "t": RESOURCE_TYPE, "id": uuid, "org": uuid|null }`.
- `hasUnrestrictedAccess()` returns false when there is no context. With a context and no scope rows, access stays org-wide (the documented behavior), because rows now actually reach the context.

- [ ] Implement and add `AuthorizationScopeSupportTest` covering: no context → false/empty; scoped principal → only listed ids.

### Task 4: Replace role-name guards with permission guards; guard the authz admin API

**Files (permission each endpoint requires):**
- `ApprovalController` (platform) → `core:approvals:manage`
- `EmailDispatchController` (platform) → `core:email:manage`
- Role / RolePermission / RolePolicyGroup / ApplicationRole controllers → class `core:roles:manage`, GET methods `core:roles:read`
- Permission / ApplicationPermission controllers → class `core:permissions:manage`, GET `core:permissions:read`
- PolicyGroup / PolicyGroupPermission controllers → class `core:policies:manage`, GET `core:policies:read`
- ProfileRole / ProfilePermission / ProfilePolicyGroup / ProfileResourceScope controllers → class `core:profiles:manage`, GET `core:profiles:read`
- `SubscriptionController` in vault / pms / crm / hrm → `{app}:subscriptions:manage` (and `assertCanReadOrganization` uses the same permission instead of `hasRole("ADMIN")`)
- CRM `AccountingController` → `crm:accounting:read`
- HRM `RideshareController.requestRide` → `staffiq:rides:create`
- Platform UI `src/auth/permissions.ts`: `canAccess` fails closed and uses exact/wildcard matching. `src/config/entities.ts` moves to v2 codes.

- [ ] Apply the annotations and confirm with `rg "@RequireAppRole" -g '*.java'` that no `ADMIN`/`RIDER`/`CLIENT` role guards remain.

---

## Phase 2: Model migration

### Task 5: App-scoped roles

**Files:**
- Modify: `.../core/entity/Role.java` (add `appId`, `organizationId`, index `(appId, code)`)
- Modify: `.../core/repository/RoleRepository.java` (`findFirstByAppIdAndCodeAndOrganizationIdIsNull`, `findByAppIdAndIsActiveTrueOrderByCodeAsc`)
- Modify: `.../core/service/impl/AuthorizationResolverService.java`
- Modify: `.../core/service/impl/RoleService.java` (require `appId` on create)
- Modify: `.../security/service/AuthService.java` (role lookup by app on register)
- Modify: `.../core/startup/DefaultDataLoader.java`, `.../core/startup/AuthDemoDataLoader.java` (app-scoped lookups)

**Resolver rules:**
- Ignore role assignments whose `expiresAt` has passed.
- Ignore roles whose `appId` differs from the profile's app.
- Ignore org-specific roles whose `organizationId` differs from the profile's organization.
- Use batch loads (`findAllById`) instead of per-row lookups.

### Task 6: Idempotent data migration runner

**Files:**
- Create: `.../core/startup/AuthorizationModelMigration.java` (`@Order(1)`, before `DefaultDataLoader`)
- Create: `.../resources/db/V7__authz_v2.sql` (reference DDL, same convention as V2–V6)

**Steps the runner performs (each one safe to re-run):**
1. **Rename permission codes.** Every 4-segment code is rewritten to v2. The explicit legacy codes use an alias map (`core:admin:user:read` → `core:users:read`, `crm:accounting:ledger:read` → `crm:accounting:read`, …). If the target code already exists, links (`ApplicationPermission`, `RolePermission`, `PolicyGroupPermission`, `ProfilePermission`) are repointed with de-duplication, and the old row is deleted.
2. **Split global roles.** For every role with `appId IS NULL`, the target apps come from `ApplicationRole`, falling back to the apps of the profiles that hold the role. The first app keeps the original row. Every other app gets a clone. `ProfileRole` (by the profile's app), `RolePolicyGroup` (by the group's app), `RoleMenu` (by the menu's app) and `RolePermission` (copied when the permission belongs to that app) are repointed. An `ApplicationRole` row is kept for each clone so the existing admin UI keeps working.
3. **Create unique indexes.** `uq_permission_code` on `Permission(code)` and `uq_role_app_org_code` on `Role(appId, coalesce(organizationId), code)` are created with `CREATE UNIQUE INDEX IF NOT EXISTS`. A failure is logged, not thrown.

- [ ] Start the platform against the existing DB twice. The second run must report 0 changes.

### Task 7: Catalog v2 format and importer

**Files:**
- Create: `ayanlab-platform-core/.../core/authz/catalog/AppAuthorizationCatalog.java` (records shared by platform and apps)
- Create: `ayanlab-platform-backend/.../core/service/impl/AuthorizationCatalogImportService.java`
- Create: `ayanlab-platform-backend/.../core/controller/InternalAuthorizationCatalogController.java` (`POST /core/api/internal/authorization-catalogs`)
- Modify: `ayanlab-platform-core/.../security/config/SecurityConfig.java` (permit `/core/api/internal/**` past JWT; the controller checks the internal token)
- Replace: `ayanlab-platform-backend/.../core/startup/AuthorizationCatalogLoader.java` (imports `db/authz/*.json`)
- Create: `ayanlab-platform-backend/src/main/resources/db/authz/{core,ipms,crm,vault}.json` (converted from `authorization-catalog.json` + `permission-catalog.json`)
- Delete: `authorization-catalog.json`, `permission-catalog.json`
- Modify: `default-data.json` (staffiq roles/users move to the HRM catalog)

**Catalog format:**

```json
{
  "schemaVersion": 2,
  "appCode": "staffiq",
  "organizations": [{ "code": "ACME_RETAIL", "name": "Acme Retail GmbH" }],
  "resources": [{ "code": "shifts", "module": "scheduling", "name": "Shifts", "actions": ["read", "create"] }],
  "policyGroups": [{ "code": "STAFFIQ_SHIFT_MANAGE", "name": "…", "permissions": ["staffiq:shifts:*"], "excludes": [] }],
  "roles": [{ "code": "ORG_ADMIN", "name": "…", "policyGroups": ["STAFFIQ_SHIFT_MANAGE"] }],
  "users": [{
    "email": "admin@acme.test",
    "password": "…",
    "firstName": "…",
    "lastName": "…",
    "profileSlug": "org-admin",
    "tenancy": "ORGANIZATION",
    "organizationCode": "ACME_RETAIL",
    "registrationStatus": "Approved",
    "roles": ["ORG_ADMIN"],
    "policyGroups": [],
    "permissionOverrides": [{ "code": "staffiq:workers:read", "effect": "GRANT" }]
  }]
}
```

**Import semantics:**
- Upsert by natural key. Existing user passwords are never changed.
- Policy-group links are additive (missing links are added, extra links are left alone).
- Patterns are expanded against the app's declared resources and actions.

### Task 8: HRM catalog and publisher

**Files:**
- Create: `ayanlab-platform-core/.../core/authz/catalog/AuthorizationCatalogPublisher.java` (enabled by `platform.authz.catalog.publish.enabled=true`; async with retry/backoff; merges the configured classpath files)
- Create: `ayanlab-hrm/ayanlab-hrm-backend/src/main/resources/authz/permissions.json`
- Create: `ayanlab-hrm/ayanlab-hrm-backend/src/main/resources/authz/policy-groups.json`
- Create: `ayanlab-hrm/ayanlab-hrm-backend/src/main/resources/authz/roles.json`
- Create: `ayanlab-hrm/ayanlab-hrm-backend/src/main/resources/authz/users.json`
- Modify: `ayanlab-hrm/ayanlab-hrm-backend/src/main/resources/application.yml`
- Modify: `ayanlab-hrm/ayanlab-hrm-backend/src/main/java/com/ayanlab/hrm/backend/HrmBackendApplication.java` (scan `com.ayanlab.core.authz`)
- Delete: `ayanlab-hrm/ayanlab-hrm-backend/.../controller/SubscriptionController.java` (depends on platform-backend classes and cannot compile in HRM; subscriptions are served by the platform at `/core/api/profile-subscriptions`)

**Source of truth:** HRM UI `src/security/permissions.ts`, `src/data/constants/permission.ts`, `src/data/constants/mainRoles.ts`, `src/ui/layout/AppShell.tsx` nav, `src/mock/mockDb.ts` users/orgs.

---

## Phase 3: Cleanup, app-owned catalogs, token invalidation

- **`RolePermission` removed.** The resolver reads roles → policy groups → permissions only. On startup, `AuthorizationModelMigration` converts each role's direct grants into a policy group `{APP}_ROLE_{CODE}_DIRECT` (org roles get an `_{roleId[0..8]}` suffix) and renames the table to `RolePermission_legacy`.
- **`ApplicationRole` removed**, together with its controller. A role's app is `Role.appId`, and the platform UI shows it in the roles list. The table is renamed to `ApplicationRole_legacy` after global roles have been split per app.
- **The HRM UI uses backend codes.** The login and `/me` responses supply `permissionCodes`, and `has()` matches wildcards like the backend (`implies`). Navigation and page guards use `staffiq:{resource}:{action}`. `computePermissions` remains only for mock mode and mirrors `authz/roles.json`.
- **App-owned catalogs.** CRM, Vault, Booking and Comm keep `src/main/resources/authz/{code}.json` and publish it on startup (`AUTHZ_CATALOG_PUBLISH`, default `true`). PMS is still pending: see the status above.
- **`authzVersion`.**
  - `App.authzVersion` and `UserProfile.authzVersion` are bumped after commit, whenever a role, policy group, permission or profile grant changes (`AuthzChangeListener` → `AuthzVersionService`).
  - The token carries `av = "{app}.{profile}"`. When it no longer matches, the request runs unauthenticated and the response has `X-Authz-Stale: true`. Both UIs then call `/auth/api/refresh` once and retry.
  - Apps check through `GET /core/api/internal/authz-versions` with `X-Internal-Token` (`AUTHZ_VERSION_CHECK_MODE=remote`, 30 s cache). If the platform is unreachable, the check fails open. Tokens without `av` are accepted.
