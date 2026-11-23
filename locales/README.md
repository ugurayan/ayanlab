# AyanLab supported locales

Canonical list: [`ayanlab-locales.json`](./ayanlab-locales.json) (25 languages, fixed `sortOrder`).

Regenerate consumers after editing the JSON:

```bash
python3 scripts/sync-ayanlab-locales.py
```

**Updated targets**

- Platform UI (`localeCatalog.ts`, language picker order)
- HRM backend navigation seed (`/hrm/api/navigation/locales`)
- HRM UI (`src/i18n/localeCatalog.ts`, AppShell, Settings, job wizard)
- Mobile apps (`LocaleNotifier.supportedTags` in template + all `*-mobile` apps)

**Note:** Full UI translations exist only for some locales (e.g. platform: `en`, `tr`, `ar`, …). Others use English string fallback until messages are added.