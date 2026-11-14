# Design: AyanLab multi-docker repo layout

**Date:** 2026-11-14  
**Status:** Implemented (scaffold)

## Goal

Replace modular monorepo coupling with **product-isolated** folders, each deployable via Docker Compose, sharing root infra (Postgres, Redis, Kafka).

## Decisions

1. **Mobile:** Expo (React Native) — matches React web stack, OTA-friendly.
2. **Backends:** Standalone Gradle + Spring Boot 3.4 / Java 21 per service (not nested multi-module).
3. **Network:** External Docker network `ayanlab_internal` from infra compose.
4. **Legacy:** `old/` untouched; gradual port of auth/core from `security` + `platform` modules.
5. **Publish:** `scripts/publish-vps.sh` rsync + compose up on VPS; `MODE=local` for dev.

## Products

auth (ui+be), core (be), vault/hrm/pms/crm/booking/comm (be+ui+mobile).