# Docker — DB ve infra komutları

Veriler **host’ta** kalır:

| Servis | Mount |
|--------|--------|
| Postgres | `./docker/data/postgres` |
| Redis | `./docker/data/redis` |
| Init SQL | `./docker/postgres/init` (read-only, ilk kurulum) |

## Auth stack (Postgres + Redis + auth-backend)

```bash
cd /Users/u0a00dr/Projects/AYANLAB
./scripts/validate-env-local.sh
ENV=local ./scripts/up-auth.sh
```

Eşdeğer ham komut:

```bash
docker compose --env-file .env.local -f docker-compose.platform.yml up -d --build
```

## Sadece DB + Redis (backend olmadan)

```bash
docker compose --env-file .env.local -f docker-compose.platform.yml up -d postgres redis
```

## Tam infra (+ Kafka) — ayrı compose

```bash
docker compose --env-file .env.local -f docker/docker-compose.infra.yml up -d
```

(İnfrayı auth compose ile birlikte kullanırken **çift postgres** açma; ya `docker-compose.platform.yml` ya `docker-compose.infra.yml`.)

## İlk kurulum / şifre sıfırlama (init script tekrar çalışsın)

```bash
docker compose --env-file .env.local -f docker-compose.platform.yml down
rm -rf ./docker/data/postgres/*
ENV=local ./scripts/up-auth.sh
```

## Log / durum

```bash
docker compose -f docker-compose.platform.yml ps
docker compose -f docker-compose.platform.yml logs -f postgres
docker compose -f docker-compose.platform.yml logs -f ayanlab-platform-backend
```

## Opsiyonel data dizini

```bash
export AYANLAB_DATA_DIR=/var/ayanlab/data
ENV=local ./scripts/up-auth.sh
```