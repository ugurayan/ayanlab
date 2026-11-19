# AyanLab

Çok ürünlü platform; ürünler (`ayanlab-pms`, …) kendi backend’ine sahip. **Platform (auth + core)** tek deploy: `ayanlab-platform`.

| Konu | Doküman |
|------|---------|
| **Platform (auth + API)** | [`README.platform.md`](README.platform.md) |
| Eski auth ayrımı | [`README.auth.md`](README.auth.md) (yönlendirme) |
| Mimari | [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) |
| Veritabanları | [`docs/DATABASES.md`](docs/DATABASES.md) |
| **Portlar** | [`docs/PORTS.md`](docs/PORTS.md) |
| **API yolları** | [`docs/API-PATHS.md`](docs/API-PATHS.md) |
| **PMS demo turu** | [`docs/pms-demo-walkthrough.md`](docs/pms-demo-walkthrough.md) |
| **Swagger / OpenAPI** | [`docs/API-SWAGGER.md`](docs/API-SWAGGER.md) |
| Docker sorunları | [`docs/TROUBLESHOOTING-DOCKER.md`](docs/TROUBLESHOOTING-DOCKER.md) |

---

## Gereksinimler

- **Podman** veya **Docker** + Compose
- **Java 21** ve **Maven 3.9+** (sadece JAR’ı IDE/Maven ile çalıştıracaksan)
- **Node 20+** (UI Vite dev)

Zsh’de `docker` genelde Podman alias’ıdır; `./scripts/*` bash ile çalışır ve `scripts/lib/docker.sh` bunu taklit eder.

---

## Uygulama başlatıcı (`./run-apps.sh`)

Kök dizinden menülü başlatıcı. Host’ta **8080 kullanılmaz**.

```bash
./run-apps.sh
```

| Seçenek | Ne yapar |
|---------|----------|
| **1** | Docker altyapısı — Postgres + Redis + Kafka (`ayanlab_internal`) |
| **2** | Platform Docker stack — Postgres + Redis + backend + UI |
| **3** | Platform lokal → alt menü: Backend / UI / Mobile / ikisi |
| **4** | HRM → Backend / UI / Mobile / ikisi |
| **5** | Vault → Backend / UI / Mobile / ikisi |
| **6** | PMS → Backend / UI / Mobile / ikisi |
| **7** | CRM → Backend / UI / Mobile / ikisi |
| **8** | Booking → Backend / UI / Mobile / ikisi |
| **9** | Comm → Backend / UI / Mobile / ikisi |
| **10** | Demo kullanıcıları seed et (platform backend açılışı / `default-data.json`) |
| **11** | Tüm lokal app'leri kill et (Docker'a dokunmaz) |
| **12** | Hepsini kill et (lokal + `ayanlab-*` Docker) |
| **0** | Çıkış |

Her ürün seçiminde bileşen ayrı sorulur (**Backend / UI / Mobile / ikisi / sadece kill**).  
**Run öncesi ilgili portlar her zaman kill edilir**, sonra yeniden başlatılır.  
Loglar: `./logs/<urun>-backend.log`, `./logs/<urun>-ui.log`, `./logs/<urun>-mobile.log`.

---

## Mobile (Flutter)

Tüm ürünlerin `*-mobile` klasörleri Flutter tabanlıdır ve ortak golden template'ten
üretilir: [`scripts/mobile-template/`](scripts/mobile-template/). İçerik şimdilik
mock (API yok): splash → onboarding → login/register/forgot → ana shell
(drawer + 4 tab + FAB) + settings/security/help/about/terms sayfaları.
11 dil desteği (en, zh, hi, es, fr, ar, bn, pt, ru, tr, ur — `ar`/`ur` RTL).

Yeniden üretim:

```bash
./scripts/scaffold-mobile.sh hrm   # hrm, pms, vault, crm, booking, comm ...
```

Script hedef klasörü silip template'ten yeniden oluşturur (`flutter create .` +
`flutter pub get` dahil). Flutter SDK gerektirir.

---

## Host portları

Host’ta **8080 bağlanmaz**. Docker içinde container `8080` dinleyebilir; host mapping `8102:8080` şeklindedir.

| Servis | Port |
|--------|------|
| Platform API | **8102** |
| Platform UI | **3102** |
| Vault API / UI | 8103 / 3103 |
| HRM API / UI | 8104 / **3104** |
| PMS API / UI | 8105 / 3105 |
| CRM API / UI | 8106 / 3106 |
| Booking API / UI | 8107 / 3107 |
| Comm API / UI | 8108 / 3108 |
| PostgreSQL (host) | 5433 |
| Redis (host) | 6380 |
| Kafka | 9092 |

Detay: [`docs/PORTS.md`](docs/PORTS.md).

---

## Demo hesaplar (login)

Seed: platform backend açılışında `default-data.json`.  
Auth API: `POST http://localhost:8102/auth/api/login` — **application (appKey) ile kullanıcı eşleşmeli**.

| UI | URL | Proxy |
|----|-----|-------|
| Platform admin | http://localhost:3102 | `/auth` → `:8102` |
| HRM | http://localhost:3104 | `/auth` → `:8102` (iş verisi mock) |

### Platform UI (`:3102`) — app seçimi zorunlu

| Application | Email | Password |
|-------------|-------|----------|
| **Core** | `admin@core.local` | `Admin123!` |
| **Core** | `ugur.ayan@gmail.com` | `superadmin` |
| **Core** | `ugur.ayan.us@gmail.com` | `admin` |
| **StaffIQ** | `next.is.future@icloud.com` | `password` |
| **StaffIQ** | `admin@acme.test` | `password` |

Yanlış app + doğru şifre yine de `Invalid email or password` döner (ör. Core seçiliyken StaffIQ kullanıcısı).

### HRM / StaffIQ UI (`:3104`)

Auth platform’a gider; ekran verisi mock (`VITE_USE_MOCKS=true`). **Şifre (hepsi):** `password`

| Email | Rol |
|-------|-----|
| `next.is.future@icloud.com` | `PLATFORM_ADMIN` |
| `admin@acme.test` | `ORG_ADMIN` |
| `manager@acme.test` | `LOCATION_MANAGER` |
| `finance@acme.test` | `ORG_FINANCE` |
| `agent@staffing.test` | `AGENT_USER` |
| `notApproved@platform.test` | `WORKER` |

Önerilen sıra: `./run-apps.sh` → **1** veya **2** → **10** (seed) → Platform UI **3→2** veya HRM **4→2**

---

## Hızlı başlangıç

```bash
cd AYANLAB
cp .env.local.example .env.local
# Düzenle: POSTGRES_SUPERUSER_PASSWORD, *_DB_PASSWORD, JWT_SECRET

./scripts/validate-env-local.sh
# Tercihen: ./run-apps.sh  →  1 veya 2
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

Host port tablosu yukarıda. 5432/6379 doluysa: [`docs/TROUBLESHOOTING-DOCKER.md`](docs/TROUBLESHOOTING-DOCKER.md).

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

## Java paketleri (backend)

Kalıp: **`com.ayanlab.<app>.backend`**

Shared kütüphane (`platform-core`) `com.ayanlab.core.*` / `com.ayanlab.security.*` olarak kalır — yalnızca runnable app `…backend` altındadır.

| App | Paket |
|-----|-------|
| Core / Platform | `com.ayanlab.core.backend` |
| HRM | `com.ayanlab.hrm.backend` |
| Vault | `com.ayanlab.vault.backend` |
| PMS | `com.ayanlab.pms.backend` |
| CRM | `com.ayanlab.crm.backend` |
| Booking | `com.ayanlab.booking.backend` |
| Comm | `com.ayanlab.comm.backend` |

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