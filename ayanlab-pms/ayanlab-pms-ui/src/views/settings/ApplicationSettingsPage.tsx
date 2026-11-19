import { useEffect, useState } from "react";
import { useLocale } from "../../state/locale";
import { useTheme, type ThemeMode } from "../../state/theme";
import type { Locale } from "../../i18n/types";
import { LOCALE_OPTIONS, localeOptionLabel } from "../../i18n/localeMeta";
import { PageHeader } from "../../ui/components/PageHeader";
import { Card, CardContent } from "../../ui/components/Card";
import { Button } from "../../ui/components/Button";
import { Label } from "../../ui/components/Label";
import { Select } from "../../ui/components/Select";
import { interactiveRowClass } from "../../lib/uiClasses";

const PREFS_KEY = "pms.app.prefs";

type AppPrefs = {
  timezone: string;
  dateFormat: string;
  emailNotif: boolean;
  pushNotif: boolean;
  denseTables: boolean;
};

const defaultPrefs: AppPrefs = {
  timezone: "Europe/Istanbul",
  dateFormat: "dd.MM.yyyy",
  emailNotif: true,
  pushNotif: false,
  denseTables: false
};

function loadPrefs(): AppPrefs {
  try {
    const raw = localStorage.getItem(PREFS_KEY);
    if (!raw) return defaultPrefs;
    return { ...defaultPrefs, ...JSON.parse(raw) };
  } catch {
    return defaultPrefs;
  }
}

export function ApplicationSettingsPage() {
  const { locale, setLocale, t } = useLocale();
  const { mode: themeMode, setMode: setThemeMode } = useTheme();
  const [prefs, setPrefs] = useState<AppPrefs>(loadPrefs);

  useEffect(() => {
    localStorage.setItem(PREFS_KEY, JSON.stringify(prefs));
  }, [prefs]);

  return (
    <div className="mx-auto max-w-2xl space-y-4">
      <PageHeader title={t("appSettings.title")} subtitle={t("appSettings.subtitle")} />
      <Card>
        <CardContent className="space-y-5 text-sm">
          <div>
            <Label htmlFor="app-theme">{t("appSettings.theme")}</Label>
            <Select id="app-theme" value={themeMode} onChange={(e) => setThemeMode(e.target.value as ThemeMode)}>
              <option value="system">{t("appSettings.themeSystem")}</option>
              <option value="light">{t("appSettings.themeLight")}</option>
              <option value="dark">{t("appSettings.themeDark")}</option>
            </Select>
          </div>
          <div>
            <Label htmlFor="app-locale">{t("appSettings.locale")}</Label>
            <Select id="app-locale" value={locale} onChange={(e) => setLocale(e.target.value as Locale)}>
              {LOCALE_OPTIONS.map((o) => (
                <option key={o.code} value={o.code}>
                  {localeOptionLabel(o)}
                </option>
              ))}
            </Select>
          </div>
          <div>
            <Label htmlFor="app-tz">{t("appSettings.timezone")}</Label>
            <Select
              id="app-tz"
              value={prefs.timezone}
              onChange={(e) => setPrefs((p) => ({ ...p, timezone: e.target.value }))}
            >
              <option value="Europe/Istanbul">Europe/Istanbul</option>
              <option value="UTC">UTC</option>
              <option value="America/New_York">America/New_York</option>
            </Select>
          </div>
          <div>
            <Label htmlFor="app-df">{t("appSettings.dateFormat")}</Label>
            <Select
              id="app-df"
              value={prefs.dateFormat}
              onChange={(e) => setPrefs((p) => ({ ...p, dateFormat: e.target.value }))}
            >
              <option value="dd.MM.yyyy">dd.MM.yyyy</option>
              <option value="MM/dd/yyyy">MM/dd/yyyy</option>
              <option value="yyyy-MM-dd">yyyy-MM-dd</option>
            </Select>
          </div>
          {(
            [
              ["emailNotif", t("appSettings.emailNotif")],
              ["pushNotif", t("appSettings.pushNotif")],
              ["denseTables", t("appSettings.denseTables")]
            ] as const
          ).map(([key, label]) => (
            <label key={key} className={`${interactiveRowClass} flex min-h-[44px] items-center justify-between gap-2`}>
              <span className="text-[rgb(var(--text))]">{label}</span>
              <input
                type="checkbox"
                className="h-4 w-4 rounded border-[rgb(var(--border))] text-teal-700 focus:ring-teal-600/40"
                checked={prefs[key]}
                onChange={(e) => setPrefs((p) => ({ ...p, [key]: e.target.checked }))}
              />
            </label>
          ))}
          <Button
            type="button"
            onClick={() => {
              localStorage.setItem(PREFS_KEY, JSON.stringify(prefs));
              alert(t("appSettings.saved"));
            }}
          >
            {t("common.save")}
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}