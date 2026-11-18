# Migration from legacy monolith

> **`old/`**, **`tmp/`**, and **`libs/`** removed. Canonical code: `ayanlab-platform/` (`ayanlab-platform-core` + backend + UI). See [`MIGRATION-STATUS.md`](MIGRATION-STATUS.md).

# Migration from `old/` (historical)

## Shared libraries (new)

| Path | Source |
|------|--------|
| `libs/ayanlab-core-domain/` | `old/core` (entities, services, repositories) |
| `libs/ayanlab-security/` | `old/security` (JWT, `AuthService`, filters) |

Each `*-backend` Gradle project includes these via `settings.gradle.kts`.

## Product mapping

| New product | Backend contents | Status |
|-------------|------------------|--------|
| `ayanlab-platform` | `libs/*` + `AuthController` → `/auth/api/**` | Migrated |
| `ayanlab-platform` | `libs/*` + `old/platform` (admin, seed loaders, DB init) | Migrated |
| `ayanlab-vault` | `libs/*` + vault controllers (auth removed → central auth) | Migrated |
| `ayanlab-pms` | `libs/*` + `old/app-ipms` (`propertymgmt`) | Migrated |
| `ayanlab-crm` | `libs/*` + `old/app-crm` (`accounting`) | Migrated |
| `ayanlab-hrm` | `libs/*` + `old/app-staffiq` (`rideshare`) | Migrated |
| `ayanlab-booking` | Shell + shared libs (no legacy app module yet) | Placeholder |
| `ayanlab-comm` | Shell + shared libs | Placeholder |

## Build

Requires **JDK 21** (Gradle 8.10 + Kotlin DSL does not support JDK 26+ yet).

```bash
cd ayanlab-platform/ayanlab-platform-backend && ./gradlew bootJar -x test
cd ayanlab-platform/ayanlab-platform-backend && ./gradlew bootJar -x test
```

There is **no** `gradlew` at repo root — each backend is its own Gradle project.

Docker (repo root context):

```bash
docker build -f docker/Dockerfile.backend \
  --build-arg BACKEND_DIR=ayanlab-platform/ayanlab-platform-backend \
  -t ayanlab-platform-backend .
```

## Component scan rules

- **core:** full `com.ayanlab` (core REST + platform admin).
- **auth / apps:** `core` services + `security` + product package; **excludes** `com.ayanlab.core.controller.*` to avoid duplicating core HTTP APIs on every service.

## Next steps

1. Thin `ayanlab-platform-core` (DTO/JWT only); persistence stays in `ayanlab-platform-backend`.
2. Flyway per schema; turn off Hibernate `ddl-auto: update` on core.
3. Port booking/comm domains from platform menus or greenfield.
4. API gateway routes: `/auth/api` → auth, `/core/api` → core, module paths → respective services.