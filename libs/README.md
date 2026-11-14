# Shared Java libraries (not a Gradle root)

Used by every `ayanlab-*/ayanlab-*-backend` via `settings.gradle.kts`:

- `ayanlab-core-domain` — domain from legacy `old/core`
- `ayanlab-security` — JWT / auth services

Each backend has its **own** `gradlew` and builds independently:

```bash
cd ayanlab-auth/ayanlab-auth-backend
./gradlew bootJar -x test
```

Docker builds still need this `libs/` directory on the build host — `docker-compose` uses `context: ..` (repo root), not the product folder alone.