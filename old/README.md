# 🏗️ AyanLabs Multi-App Platform

A modular monolith Spring Boot platform supporting multiple applications with shared security, common services, and independent app modules.

## Local development

Copy `.env.example` to `.env` (gitignored) — defaults point at VPS PostGIS `147.93.138.158`.

```bash
set -a && source .env && set +a && ./gradlew :platform:bootRun
# or: bash scripts/run-platform.sh
```

## API conventions

- [Controller client headers](CONTROLLER_API_HEADERS.md) — required `X-Correlation-Id`, `X-App-Id`, and `X-Client-*` headers on every endpoint (see `RolePermissionController`).
- [Documentation index](docs/README.md) — API rehberleri, curl örnekleri, controller referansları.
- [Module curl examples](docs/curl/README.md) — smoke-test curls per app module (`core`, `ipms`, `crm`, `vault`, `staffiq`).

## Architecture

```bash
./gradlew :platform:bootRun POSTGIS_HOST=147.93.138.158 POSTGIS_USER=core POSTGIS_PASSWORD=UgurAyanLabs9602. POSTGIS_DB=ayanLabs 
```