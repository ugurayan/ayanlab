# AyanLab HRM (StaffIQ)

StaffIQ işveren / yönetici portalı ve mobil uygulama için monorepo. Kimlik doğrulama ve yetkilendirme **platform** üzerindedir; bu repodaki backend yalnızca HRM domain API’sini sunar.

| Bileşen | Klasör | Varsayılan port |
|---------|--------|-----------------|
| Backend API | `ayanlab-hrm-backend` | **8104** (`/hrm/api/**`) |
| Web UI | `ayanlab-hrm-ui` | **3104** (Vite) |
| Mobile | `ayanlab-hrm-mobile` | — |

Platform API: **8102** (`/auth/api/**`) — login, JWT, kullanıcı/rol/izin.

---

## Mimari (kısa)

```
┌─────────────┐     JWT + X-Client-* headers      ┌──────────────────────┐
│  hrm-ui     │ ────────────────────────────────► │ ayanlab-hrm-backend  │
│  :3104      │         /hrm/api                  │ :8104                │
└──────┬──────┘                                   └──────────┬───────────┘
       │ login / me                                          │ JPA
       ▼                                                      ▼
┌─────────────┐                                   PostgreSQL (schema: staffiq)
│  platform   │ ◄── authz katalog publish (startup) ──────────┘
│  :8102      │
└─────────────┘
```

- **Şifre ve token üretimi** platformdadır; HRM backend token doğrular (`com.ayanlab.security`).
- **Roller / izinler / seed kullanıcılar**: `ayanlab-hrm-backend/src/main/resources/authz/*.json` — her HRM backend açılışında platforma idempotent yayınlanır (`platform.authz.catalog.publish.enabled=true`, `PLATFORM_INTERNAL_TOKEN` gerekir).
- **Domain verisi**: Postgres `staffiq` şeması; açılışta mock JSON → tablolar (`staffiq.db.reload-*`).

---

## Ön koşullar

- Java **21**, Maven
- Node **20+** (UI)
- PostgreSQL (local Docker veya remote VPS) — repo kökünde `.env.local` / `.env.remote`
- Çalışan **platform backend** (HRM UI ve authz publish için)

Remote DB ilk kurulum (şema/rol):

```bash
./scripts/01.remote_db_initialization.sh   # repo kökünden
```

---

## Çalıştırma (önerilen — repo kökü)

Tüm env ve portlar için ana rehber: [`run-apps.sh`](../run-apps.sh) başlığı.

```bash
# Platform + HRM backend (remote DB örneği)
./run-apps.sh .env.remote platform backend
./run-apps.sh .env.remote hrm backend

# HRM UI (auth proxy → 8102, API → 8104)
./run-apps.sh .env.remote hrm ui
```

Kısa formlar: `./run-apps.sh remote hrm backend`, `./run-apps.sh local hrm ui`.

**Sıra:** Önce platform backend, sonra HRM backend, sonra UI. UI için Vite genelde auth’u aynı origin üzerinden platforma proxy eder; API tabanı `http://localhost:8104/hrm/api`.

### Ortam dosyası

Repo kökündeki `.env.local` / `.env.remote` şunları taşır (örnek):

- `POSTGIS_*` veya `POSTGIS_URL` — HRM DB (`currentSchema=staffiq`)
- `AYANLAB_PLATFORM_URL`, `PLATFORM_INTERNAL_TOKEN` — katalog publish
- `SERVER_PORT=8104` (HRM backend)

`run-apps.sh` seçilen dosyayı child process’lere `ENV` / `ENV_FILE` ile iletir.

### Sadece backend (manuel)

```bash
cd ayanlab-hrm/ayanlab-hrm-backend
# ENV değişkenleri yüklü bir shell’de:
mvn spring-boot:run
```

### Sadece UI (manuel)

```bash
cd ayanlab-hrm/ayanlab-hrm-ui
cp .env.example .env.local   # ihtiyaca göre düzenle
npm install
npm run dev
```

UI önemli değişkenler (`ayanlab-hrm-ui/.env.example`):

| Değişken | Açıklama |
|----------|----------|
| `VITE_API_BASE_URL` | Örn. `http://localhost:8104/hrm/api` |
| `VITE_AUTH_API_BASE_URL` | Boş = Vite `/auth` → platform proxy |
| `VITE_APP_ID` | StaffIQ uygulama UUID (platform ile aynı) |
| `VITE_USE_MOCKS` | `false` = gerçek HRM API; `true` = yerel mock veri |

---

## Backend yapısı

Tek kök paket: `com.ayanlab.hrm.backend`

| Paket | İçerik |
|-------|--------|
| `entity` | JPA modelleri |
| `repository` | Spring Data |
| `service` / `service.impl` | İş kuralları, mock seed |
| `controller` | REST (`/hrm/api/...`), `BaseController` + `@ClientHeaders` |
| `config` | `HrmDataProperties`, Spring config |
| `startup` | Navigation + mock data loader |

Swagger (backend ayaktayken): `http://localhost:8104/swagger-ui.html`

### Startup seed

| Ayar | Varsayılan | Kaynak |
|------|------------|--------|
| `staffiq.db.reload-navigation-on-startup` | `true` | `db/staffiq-navigation-seed.json` |
| `staffiq.db.reload-mock-data-on-startup` | `true` | `db/staffiq-mock-data.json` |

Kapatmak: `STAFFIQ_RELOAD_MOCK_DATA=false` / `STAFFIQ_RELOAD_NAVIGATION=false`.

Mock JSON → entity eşlemesi: `MockDataServiceImpl` + `MockDataSeeder` + `MockDataJsonMapper`.

### Yetkilendirme

İzin formatı: `staffiq:{kaynak}:{eylem}` — tanımlar `authz/permissions.json`. Controller’lar `@RequirePermission` kullanır.

Yayın için platformda internal token tanımlı olmalı; aksi halde logda katalog publish uyarısı görülür, izinler güncellenmez.

---

## REST API (özet)

CRUD kalıbı: `GET/POST /hrm/api/{kaynak}`, `GET/PUT/DELETE /hrm/api/{kaynak}/{id}`.

Örnek kaynaklar:

- `organizations`, `locations`, `shifts`, `applications`, `workers`
- `jobs`, `tasks`, `documents`, `document-verifications`
- `finance-records`, `transactions`
- `user-audits`, `system-audits`
- `job-roles`, `job-role-categories`, `skills`, `certificates`, `profiles`, `departments`
- `navigation/menu-items`, `navigation/locales`

İsteklerde platform ile uyumlu başlıklar: `Authorization`, `X-App-Id`, `X-Client-Type`, `X-Correlation-Id`, isteğe bağlı `X-Client-Language`.

---

## Geliştirme araçları

| Araç | Açıklama |
|------|----------|
| `scripts/generate-hrm-crud.py` | Entity/repo/service/controller iskeleti üretir |
| `scripts/export-staffiq-mock-data.mts` | UI mock → `staffiq-mock-data.json` export |
| `mvn -f ayanlab-hrm-backend/pom.xml test` | `HrmAuthorizationCatalogTest` authz doğrulama |

Mock veriyi UI’dan yenilemek:

```bash
cd ayanlab-hrm/ayanlab-hrm-ui
npx tsx ../scripts/export-staffiq-mock-data.mts
# çıktı: ayanlab-hrm-backend/src/main/resources/db/staffiq-mock-data.json
```

Derleme:

```bash
mvn -f ayanlab-hrm/ayanlab-hrm-backend/pom.xml -DskipTests compile
```

---

## Mobil

`ayanlab-hrm-mobile` — ayrı Flutter projesi; ayrıntı için `ayanlab-hrm-mobile/README.md`.

---

## Sorun giderme

| Belirti | Kontrol |
|---------|---------|
| 401 / 403 API | Platform ayakta mı? Token geçerli mi? Yeni izinler için refresh/login |
| DB permission denied | `01.remote_db_initialization.sh`, `POSTGIS_SCHEMA=staffiq` |
| Boş listeler UI’da | `VITE_USE_MOCKS=false`, backend seed logları (`MockDataLoader`) |
| Authz publish fail | `PLATFORM_INTERNAL_TOKEN`, platform URL, platform önce başlatıldı mı |
| Port çakışması | Önceki JVM/UI sürecini kapat (`8104`, `3104`) |

Daha geniş platform/env notları: repo kökünde [`README.platform.md`](../README.platform.md).