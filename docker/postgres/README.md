# Postgres init

On **first** volume creation, `01-create-databases.sh` reads `*_DB_*` from the container environment (from `.env.local`) and creates 8 databases + roles.

Recreate after changing credentials:

```bash
docker compose -f docker-compose.platform.yml down -v
ENV=local ./scripts/up-auth.sh
```