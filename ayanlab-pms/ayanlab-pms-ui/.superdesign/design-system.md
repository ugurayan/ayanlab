# AyanLab PMS UI — design system (Superdesign)

## Brand
- Primary: teal-800 (light) / teal-600 (dark accents)
- Surface tokens via CSS variables in `src/styles.css` (`--surface`, `--border`, `--text`, …)
- Typography: system UI stack (Tailwind antialiased), tight headings (`tracking-tight`)

## Light / Dark
- `class="dark"` on `<html>` via `ThemeProvider` (`pms.theme`: light | dark | system)
- Toggle: header Sun/Moon, **Settings → Application → Theme**

## Layout
- App shell: 288px sidebar, glass header, collapsible nav modules
- Cards: `rounded-2xl`, semantic borders, soft shadow

## Components (primitives)
- `src/ui/components/`: Button, Card, Input, Badge, PageHeader, ThemeToggle

## Superdesign CLI
Run `npx --yes @superdesign/cli@latest login` then init in `.superdesign/init/` for canvas drafts.