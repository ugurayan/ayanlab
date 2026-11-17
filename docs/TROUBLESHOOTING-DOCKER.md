# Docker / Podman sorunları

## `proxy already running`

Bu ortamda `docker compose` çoğu zaman **Podman** üzerinden çalışıyor (`Executing external compose provider` mesajı).

**Sebep:** Host’ta **5432** veya **6379** zaten dolu (ör. `poc-postgres`, `poc-redis`). Podman port proxy ikinci kez bind edemiyor.

**Çözüm A (önerilen):** AyanLab için farklı host portları (varsayılan artık **5433** / **6380**):

```bash
# .env.local
AYANLAB_POSTGRES_HOST_PORT=5433
AYANLAB_REDIS_HOST_PORT=6380
POSTGIS_PORT=5433

docker compose --env-file .env.local -f docker-compose.platform.yml down
podman rm -f ayanlab-postgres ayanlab-redis ayanlab-platform-backend 2>/dev/null || true
ENV=local ./scripts/up-auth.sh
```

**Çözüm B:** Eski poc container’ları durdur:

```bash
podman stop poc-postgres poc-redis
# sonra 5432/6379 ile compose (AYANLAB_POSTGRES_HOST_PORT=5432)
```

**Çözüm C:** Mevcut `poc-postgres` kullan — AyanLab compose’da postgres/redis’i kapat, `.env.local` ile `POSTGIS_HOST=localhost` `POSTGIS_PORT=5432` (manuel DB/user oluşturman gerekir).

## Podman vs Docker Desktop

- `docker ps` çıktısında **BuildahVersion** görüyorsan Podman kullanıyorsun.
- Komut: `podman compose --env-file .env.local -f docker-compose.platform.yml up -d postgres redis`

## `docker: command not found` (script ile)

Zsh’de `docker` bazen **Podman alias**’ıdır; `./scripts/up-auth.sh` **bash** ile çalışır ve alias görmez → `docker: command not found`.

- `up-auth.sh` / `up-foundation.sh` önce `scripts/lib/docker.sh` ile aynı mantıkta bash `docker()` shim’i yükler (yine `docker compose` komutu).
- İnteraktif shell için zsh: `alias docker=podman` (veya PATH’te gerçek Docker Desktop `docker`).

## Kontrol

```bash
podman ps -a | grep -E 'ayanlab|poc'
ss -lntp | grep -E '5432|5433|6379|6380'  # mac: lsof -i :5432
```