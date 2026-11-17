# Platform

Monorepo root: [README.platform.md](../README.platform.md), [README.auth.md](../README.auth.md).

# To Run
```
docker compose --env-file .env.local -f docker-compose.platform.yml down -v
docker compose --env-file .env.local -f docker-compose.platform.yml up -d --build postgres redis

# healthy olana kadar bekle (veya pg_isready)
docker compose --env-file .env.local -f docker-compose.platform.yml ps

./ayanlab-platform/run-local.sh   # 1
```