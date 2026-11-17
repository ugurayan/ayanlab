# Service boundaries (target)

## Roles

| Service | Owns | Exposes |
|---------|------|---------|
| **ayanlab-platform** | Login, JWT, RBAC, apps, profiles, subscriptions, billing, email/SMS/kafka/GCS | `/auth/api/**`, `/core/api/**` on one JVM — bkz. [`README.platform.md`](../README.platform.md) |

**Other products** (vault, pms, crm, …) keep their own DB schema; they validate JWT from platform and call platform HTTP APIs for shared data.

```
Browser/App → ayanlab-vault-backend
              ├─ Authorization: Bearer JWT (issued by platform)
              └─ HTTP → ayanlab-platform-backend (shared data)
```

## Environment (Docker network)

| Variable | Example (compose) |
|----------|-------------------|
| `AYANLAB_PLATFORM_URL` | `http://ayanlab-platform-backend:8080` |

Public edge: host port `8102` (API), `3102` (UI) — [`docs/PORTS.md`](PORTS.md).

## Migration status

| Phase | State |
|-------|--------|
| **Now** | `ayanlab-platform-backend` + `ayanlab-platform-core`; DB `ayanlab_platform` / `core`. |
| **Next** | Product backends: HTTP + JWT only; local app schema. |

## Start

```bash
ENV=local ./scripts/up-platform.sh
```

Health: `POST http://localhost:8102/auth/api/login`, Swagger `http://localhost:8102/swagger-ui/index.html`