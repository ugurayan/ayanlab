# ayanlab-platform-core

Paylaşılan platform kütüphanesi (`ayanlab-platform` altında).

Eski `libs/ayanlab-core-domain` + `libs/ayanlab-security` bu artifact’e taşındı; `libs/` kaldırıldı.

**Hedef (Faz 2–3):** Yalnızca DTO, servis portları ve JWT sözleşmeleri; persistence ve REST `ayanlab-platform-backend` içinde.

Kurulum:

```bash
mvn -f ../pom.xml install -DskipTests
```

Bkz. [`docs/superpowers/specs/2026-11-15-platform-module-split-design.md`](../../docs/superpowers/specs/2026-11-15-platform-module-split-design.md)