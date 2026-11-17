# AyanLab

Çok ürünlü platform; ürünler (`ayanlab-pms`, …) kendi backend’ine sahip. **Platform (auth + core)** tek deploy: `ayanlab-platform`.

| Konu | Doküman |
|------|---------|
| **Platform (auth + API)** | [`README.platform.md`](README.platform.md) |
| Eski auth ayrımı | [`README.auth.md`](README.auth.md) (yönlendirme) |
| Mimari | [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) |
| Veritabanları | [`docs/DATABASES.md`](docs/DATABASES.md) |
| **API yolları** | [`docs/API-PATHS.md`](docs/API-PATHS.md) |
| **Swagger / OpenAPI** | [`docs/API-SWAGGER.md`](docs/API-SWAGGER.md) |
| Docker sorunları | [`docs/TROUBLESHOOTING-DOCKER.md`](docs/TROUBLESHOOTING-DOCKER.md) |

---

## Gereksinimler

- **Podman** veya **Docker** + Compose
- **Java 21** ve **Maven 3.9+** (sadece JAR’ı IDE/Maven ile çalıştıracaksan)

Zsh’de `docker` genelde Podman alias’ıdır; `./scripts/*` bash ile çalışır ve `scripts/lib/docker.sh` bunu taklit eder.

---

## Hızlı başlangıç

```bash
cd AYANLAB
cp .env.local.example .env.local
# Düzenle: POSTGRES_SUPERUSER_PASSWORD, *_DB_PASSWORD, JWT_SECRET

./scripts/validate-env-local.sh
ENV=local ./scripts/up-platform.sh
```

`up-platform.sh` (veya `up-auth.sh` alias): **Postgres**, **Redis**, **ayanlab-platform-backend**, **ayanlab-platform-ui**.

---

## Nasıl çalışır?

```
.env.local  →  compose env
       ↓
Postgres (ilk boş volume)
  docker/postgres/init/01-create-databases.sh
  → platform + 6 ürün DB’si + kullanıcı + şema
       ↓
ayanlab-platform-backend
  → Hibernate ddl-auto: tabloları oluşturur/günceller (Flyway henüz yok)
       ↓
http://localhost:8102
```

| Ne | Nerede |
|----|--------|
| Postgres verisi | `./docker/data/postgres` |
| Redis verisi | `./docker/data/redis` |
| Init script | yalnızca **ilk** kurulumda (volume boşken) |
| Şema eksikse | `./scripts/ensure-db-schemas.sh` |
| Tablolar | **platform-backend çalışırken** oluşur; sadece `postgres redis` yetmez |

**Host portları (varsayılan):** Postgres `5433`, Redis `6380`, Platform API `8102`, UI `3102`.  
5432/6379 doluysa: [`docs/TROUBLESHOOTING-DOCKER.md`](docs/TROUBLESHOOTING-DOCKER.md).

---

## Kontrol

```bash
curl -s -o /dev/null -w "%{http_code}\n" \
  -X POST http://localhost:8102/auth/api/login \
  -H 'Content-Type: application/json' -d '{}'
# 400 = servis ayakta (body eksik). Empty response = backend çöküyor → loglara bak.
```

```bash
docker logs -f ayanlab-platform-backend
```

| | URL |
|---|-----|
| Swagger | http://localhost:8102/swagger-ui/index.html |
| OpenAPI | http://localhost:8102/v3/api-docs |
| Login | `POST` http://localhost:8102/auth/api/login |

Diğer backend Swagger adresleri: [`docs/API-SWAGGER.md`](docs/API-SWAGGER.md).

`/actuator/health` local’de **DOWN** olabilir (SMTP yapılandırılmadı); login API’sini engellemez.

---

## Sık kullanılan komutlar

## Sıfırdan kurulum sırası

```bash
docker compose --env-file .env.local -f docker-compose.platform.yml down -v
docker compose --env-file .env.local -f docker-compose.platform.yml up -d --build postgres redis

# healthy olana kadar bekle (veya pg_isready)
docker compose --env-file .env.local -f docker-compose.platform.yml ps

./ayanlab-platform/run-local.sh   # 1
```

`run-backend.sh` içine Postgres hazır olana kadar bekleme eklendi; bir sonraki `run-local` bu yarışı daha az yaşatır. 

**Sadece veritabanı + Redis (backend yok):**

```bash
docker compose --env-file .env.local -f docker-compose.platform.yml up -d postgres redis
```

**Postgres’i sıfırla (init yeniden çalışır):**

```bash
docker compose --env-file .env.local -f docker-compose.platform.yml down
rm -rf ./docker/data/postgres/*
ENV=local ./scripts/up-auth.sh
./scripts/ensure-db-schemas.sh
```

**Backend’i Maven ile (Postgres Docker’da):**

```bash
cd ayanlab-platform && ENV=local ./run-local.sh
# veya: ./scripts/run-auth-backend.sh
```

**Başka ürün (önce auth stack hazır olsun):**

```bash
cd ayanlab-vault
ENV_FILE=../.env.local docker compose up -d --build
```

---

## Ortam dosyaları

| Dosya | Git |
|-------|-----|
| `.env.local.example` | ✅ şablon |
| `.env.local` | ❌ lokal |

`ENV=local` → `.env.local`, `ENV=prod` → `.env.prod`

---

## Klasörler

```
ayanlab-platform/  platform-core (JAR) + backend + UI
ayanlab-vault/ …   diğer ürünler (HTTP + JWT)
docker/data/       Postgres + Redis mount
```

---

## Diğer dokümantasyon

- [`docs/DOCKER-RUN.md`](docs/DOCKER-RUN.md)
- [`docs/RUN-AUTH-BACKEND.md`](docs/RUN-AUTH-BACKEND.md)
- [`docs/SECRETS.md`](docs/SECRETS.md)
- [`docs/PORTS.md`](docs/PORTS.md)