# Auth (artık `ayanlab-platform` içinde)

Kimlik, JWT ve platform RBAC **`ayanlab-platform/ayanlab-platform-backend`** tek JVM’de çalışır.

- Login: `POST http://localhost:8102/auth/api/login`
- RBAC: `http://localhost:8102/auth/api/users|roles|permissions|profiles|...`
- UI: `http://localhost:3102` (`ayanlab-platform-ui`)
- DB: `ayanlab_platform` / şema `core` — `PLATFORM_DB_*` (`.env.local`)

```bash
ENV=local ./scripts/up-platform.sh
```

Paylaşılan kod: `ayanlab-platform/ayanlab-platform-core`. Auth REST: `ayanlab-platform-backend` (`@AuthPlatformApi`).

Detay: [`docs/AUTH-CORE-SPLIT.md`](docs/AUTH-CORE-SPLIT.md), [`README.platform.md`](README.platform.md).