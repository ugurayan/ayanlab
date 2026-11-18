# Flutter Mobile Template — Design Spec

**Date:** 2026-11-18
**Status:** Approved

## Goal

Convert all 6 `*-mobile` folders (currently minimal Expo/React Native stubs) to Flutter,
sharing one standard template with empty/mock pages: auth flow, main shell
(drawer + bottom tabs + FAB), and 11-language localization. No API calls — all data mocked.

## Scope

Apps: `platform` yok (mobile yok), hedef: **hrm, pms, vault, crm, booking, comm** (6 adet).

Approach: **golden template + generator script** (`scripts/scaffold-mobile.sh`).
Template lives in `scripts/mobile-template/`; script copies it into
`ayanlab-<app>/ayanlab-<app>-mobile`, rewrites app/package names, and deletes old RN files.

## Architecture (per app)

```
lib/
├── main.dart                  # MaterialApp, provider setup, route table
├── core/
│   ├── theme/app_theme.dart   # Light + dark Material 3 themes
│   ├── i18n/                  # ARB files + gen-l10n config
│   └── mock/mock_data.dart    # Fake user, notifications, content
└── features/
    ├── auth/                  # splash, onboarding (4 slides), login, register, forgot-password
    ├── shell/main_shell.dart  # AppBar + Drawer + NavigationBar (4 tabs) + FAB
    ├── home/home_page.dart    # FAB, quick shortcuts, mock feed
    ├── search/search_page.dart
    ├── notifications/notifications_page.dart
    └── profile/               # profile, edit_profile, settings, security, help, about, terms
state/ (provider):
    - SessionNotifier  (mock login/logout)
    - LocaleNotifier   (11 diller)
    - ThemeNotifier    (light/dark)
```

## Key behaviors

- **Bottom tabs (4):** Home · Search · Notifications · Profile
- **Drawer:** profile header, Settings, Security, Help, About, Terms, language picker, theme toggle, logout
- **Auth flow (mock):** Splash → Onboarding (first run) → Login. "Sign in" sets a fake session → MainShell. Logout returns to Login.
- **i18n:** `flutter gen-l10n`; 11 locales: en, zh, hi, es, fr, ar, bn, pt, ru, tr, ur. `ar`, `ur` → RTL (automatic via `Directionality`).
- **Persistence:** `shared_preferences` for locale, theme mode, mock session, onboarding-seen flag.
- **Dependencies:** provider, shared_preferences, flutter_localizations, intl. Nothing else.

## Non-goals

- Real backend/API integration, real auth, push notifications
- Platform-specific customizations (ios/android dirs are default `flutter create` output)

## Verification

- `flutter analyze` passes for one generated app (hrm) — proxy for all (identical source)
- `flutter build` not required in this pass; `flutter test` default smoke test updated
