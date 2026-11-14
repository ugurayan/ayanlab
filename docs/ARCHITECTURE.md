# AyanLab — Multi-product Docker architecture

Each top-level `ayanlab-*` directory is an **independent deployable product** (own `docker-compose.yml`, env, and VPS profile).

## Products

| Product | Backend | UI | Mobile |
|---------|---------|-----|--------|
| `ayanlab-auth` | ✓ | ✓ | — |
| `ayanlab-core` | ✓ | — | — |
| `ayanlab-vault` | ✓ | ✓ | ✓ (Expo) |
| `ayanlab-hrm` | ✓ | ✓ | ✓ |
| `ayanlab-pms` | ✓ | ✓ | ✓ |
| `ayanlab-crm` | ✓ | ✓ | ✓ |
| `ayanlab-booking` | ✓ | ✓ | ✓ |
| `ayanlab-comm` | ✓ | ✓ | ✓ |

## Shared infrastructure (repo root)

`docker/docker-compose.infra.yml` runs on network `ayanlab_internal`:

- **PostgreSQL** (PostGIS image) — per-service schemas via `POSTGRES_*` / Flyway later
- **Redis**
- **Kafka** (KRaft single-node for dev/VPS)

Application stacks attach to `ayanlab_internal` as an external network (created by infra compose).

## Tech stack

- **Backend:** Java 21, Spring Boot 3.4.x, Gradle (standalone project per service)
- **UI:** React 19 + TypeScript + Vite, served via nginx in Docker
- **Mobile:** Expo (React Native) — one codebase for iOS/Android, aligns with React UI skills

## Legacy

`old/` remains the migration source (monolithic Gradle modules: `core`, `security`, `platform`, apps). New services copy patterns from there incrementally.

## Repo layout

```
AYANLAB/
  libs/                   # shared JAR sources (core-domain, security) — not a boot app
  docker/                 # infra compose + Dockerfile.backend
  docs/                   # documentation
  skills/                 # agent / team skills (see .cursor/skills)
  scripts/                # publish-vps.sh, helpers
  old/                    # legacy monolith (unchanged)
  ayanlab-<product>/      # product roots (own docker-compose + backend gradlew)
```

**Gradle:** each `ayanlab-<product>/<product>-backend/` has its own wrapper (`./gradlew`). No monorepo `settings.gradle` at repo root.

**Docker deploy per product:** from `ayanlab-auth/`, compose build context is `..` (repo root) so `libs/` is available. VPS rsync uses the full repo; only the chosen product compose stack is started (`PRODUCT=auth`).