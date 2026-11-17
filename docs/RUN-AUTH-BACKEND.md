# ayanlab-platform-backend — run (auth + core API)

Single JVM: `/auth/api/**` and `/core/api/**` on port **8102**.

## Docker (recommended)

```bash
cp .env.local.example .env.local
./scripts/validate-env-local.sh
ENV=local ./scripts/up-platform.sh
```

DB: `ayanlab_platform` / schema `core` (`PLATFORM_DB_*` in `.env.local`).

## Maven (local)

```bash
cd ayanlab-platform
ENV=local ./run-local.sh
```

Swagger: http://localhost:8102/swagger-ui/index.html

Login smoke:

```bash
curl -s -o /dev/null -w "%{http_code}\n" -X POST http://localhost:8102/auth/api/login \
  -H 'Content-Type: application/json' -d '{}'
# 400 = up (validation error on empty body)
```

See [`README.platform.md`](../README.platform.md) and [`MIGRATION-STATUS.md`](MIGRATION-STATUS.md).