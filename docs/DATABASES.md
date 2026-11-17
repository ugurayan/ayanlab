# Per-service PostgreSQL databases

| Service | DB name | Role | Schema |
|---------|---------|------|--------|
| platform | `ayanlab_platform` | `platform_admin` | `core` |
| booking | `ayanlab_booking` | `booking_admin` | `booking` |
| crm | `ayanlab_crm` | `crm_admin` | `crm` |
| hrm | `ayanlab_hrm` | `hrm_admin` | `staffiq` |
| pms | `ayanlab_pms` | `pms_admin` | `ipms` |
| vault | `ayanlab_vault` | `vault_admin` | `vault` |
| comm | `ayanlab_comm` | `comm_admin` | `comm` |

Kimlik ve iş API’leri tek JVM (`ayanlab-platform-backend`) üzerinde; tablolar `ayanlab_platform` / `core` şemasında.

Credentials live in `.env.local` (`*_DB_USER`, `*_DB_PASSWORD`).  
Docker first boot: `docker/postgres/init/01-create-databases.sh`.

Timezone: `TZ=America/Los_Angeles`.

Validate: `./scripts/validate-env-local.sh`

SQL migrations per DB: coming next (Flyway folders per service).