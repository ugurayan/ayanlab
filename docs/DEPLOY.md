# Deploy to VPS (single entrypoint)

## Prerequisites

- Docker + Docker Compose on VPS
- `.env` at repo root (copy from `.env.example`)
- SSH access (`VPS_HOST`, optional `SSH_KEY`)

## Local full stack

```bash
# 1) Shared infra (postgres, redis, kafka)
docker compose -f docker/docker-compose.infra.yml up -d

# 2) One product (example: auth)
docker compose -f ayanlab-auth/docker-compose.yml up -d --build

# Or all products
./scripts/publish-vps.sh local
```

## VPS publish

```bash
export VPS_HOST=your.vps.ip
export POSTGRES_PASSWORD=...
./scripts/publish-vps.sh
```

Default flow:

1. Sync repo to `REMOTE_DIR` on VPS (rsync, excludes `.git` and `old/` build artifacts)
2. Run infra compose on VPS if not running
3. Build and start selected product compose files (`PRODUCT=all` or comma-separated)

Override products:

```bash
PRODUCT=auth,core,vault ./scripts/publish-vps.sh
```