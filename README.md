# AyanLab

Multi-product platform: each `ayanlab-*` directory is a **standalone Docker project** (backend, UI, and/or mobile).

- **Docs:** [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md), [`docs/DEPLOY.md`](docs/DEPLOY.md), [`docs/PORTS.md`](docs/PORTS.md)
- **Legacy monolith:** `old/` (migration source)
- **Infra:** `docker/docker-compose.infra.yml` (PostgreSQL, Redis, Kafka)

```bash
cp .env.example .env
docker compose -f docker/docker-compose.infra.yml up -d
./scripts/publish-vps.sh local   # build all product stacks locally
```

VPS:

```bash
export VPS_HOST=...
./scripts/publish-vps.sh
```