# AyanLab — Multi-product Docker architecture

Each top-level `ayanlab-*` directory is an **independent deployable product** (own `docker-compose.yml`, env, and VPS profile).

## Products

| Product | Backend | UI | Mobile |
|---------|---------|-----|--------|
| `ayanlab-platform` | ✓ | ✓ | — |
| `ayanlab-platform` | ✓ | — | — |
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

## Auth & core first (foundation)

Tüm ürünler **ayanlab-platform** (kimlik, yetki, paylaşılan domain) ile **HTTP API** üzerinden konuşur. Derleme: `ayanlab-platform-core` JAR. Hedef mimari [`docs/SERVICES.md`](SERVICES.md).

```bash
./scripts/up-foundation.sh
```

## Legacy

Legacy sources `old/`, `tmp/auth`, and `libs/` were removed after migration into `ayanlab-platform` (see [`MIGRATION-STATUS.md`](MIGRATION-STATUS.md)).

## Repo layout

```
AYANLAB/
  ayanlab-platform/       # platform-core JAR + backend + UI
  docker/                 # infra compose + Dockerfile.backend
  docs/                   # documentation
  skills/                 # agent / team skills (see .cursor/skills)
  scripts/                # publish-vps.sh, helpers
  ayanlab-<product>/      # product roots (own docker-compose + Maven backend)
```

**Maven:** `ayanlab-platform-core` is installed locally; each product backend depends on that artifact.

**Docker deploy per product:** compose build context is repo root so `ayanlab-platform/` is available. VPS rsync uses the full repo; only the chosen product compose stack is started.