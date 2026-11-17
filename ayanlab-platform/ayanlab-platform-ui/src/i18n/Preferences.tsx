import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { columnMessages } from "./columnMessages";
import { LANGUAGE_LABELS, sortedLocales } from "./languageNames";
import { LOCALES, LOCALE_META, messages, type Locale } from "./messages";

export type Theme = "light" | "dark";

const THEME_KEY = "ayanlab.theme";
const LOCALE_KEY = "ayanlab.locale";

type Ctx = {
  theme: Theme;
  setTheme: (t: Theme) => void;
  locale: Locale;
  setLocale: (l: Locale) => void;
  t: (key: string) => string;
  entityName: (id: string, fallback: string) => string;
};

const PreferencesContext = createContext<Ctx | null>(null);

function readTheme(): Theme {
  const v = localStorage.getItem(THEME_KEY);
  return v === "dark" ? "dark" : "light";
}

function readLocale(): Locale {
  const v = localStorage.getItem(LOCALE_KEY);
  return LOCALES.includes(v as Locale) ? (v as Locale) : "en";
}

function applyDoc(theme: Theme, locale: Locale) {
  document.documentElement.dataset.theme = theme;
  document.documentElement.lang = locale;
  document.documentElement.dir = LOCALE_META[locale].rtl ? "rtl" : "ltr";
}

export function PreferencesProvider({ children }: { children: ReactNode }) {
  const [theme, setThemeState] = useState<Theme>(() => (typeof document === "undefined" ? "light" : readTheme()));
  const [locale, setLocaleState] = useState<Locale>(() => (typeof document === "undefined" ? "en" : readLocale()));

  useEffect(() => {
    applyDoc(theme, locale);
  }, [theme, locale]);

  const setTheme = useCallback((next: Theme) => {
    setThemeState(next);
    localStorage.setItem(THEME_KEY, next);
  }, []);

  const setLocale = useCallback((next: Locale) => {
    setLocaleState(next);
    localStorage.setItem(LOCALE_KEY, next);
  }, []);

  const t = useCallback(
    (key: string) =>
      columnMessages[locale]?.[key] ??
      messages[locale]?.[key] ??
      columnMessages.en[key] ??
      messages.en[key] ??
      key,
    [locale],
  );

  const entityName = useCallback(
    (id: string, fallback: string) => {
      const key = `entity.${id}`;
      const value = t(key);
      return value === key ? fallback : value;
    },
    [t],
  );

  const value = useMemo(
    () => ({ theme, setTheme, locale, setLocale, t, entityName }),
    [theme, setTheme, locale, setLocale, t, entityName],
  );
  return <PreferencesContext.Provider value={value}>{children}</PreferencesContext.Provider>;
}

export function useI18n() {
  const ctx = useContext(PreferencesContext);
  if (!ctx) throw new Error("useI18n outside PreferencesProvider");
  return ctx;
}

export function PrefsControls() {
  const { theme, setTheme, locale, setLocale, t } = useI18n();
  return (
    <div className="prefs-controls">
      <select
        className="prefs-select"
        aria-label={t("theme.light")}
        value={theme}
        onChange={(e) => setTheme(e.target.value as Theme)}
      >
        <option value="light">{t("theme.light")}</option>
        <option value="dark">{t("theme.dark")}</option>
      </select>
      <select
        className="prefs-select"
        aria-label="Language"
        value={locale}
        onChange={(e) => setLocale(e.target.value as Locale)}
      >
        {sortedLocales(locale).map((code) => (
          <option key={code} value={code}>
            {LANGUAGE_LABELS[locale][code]}
          </option>
        ))}
      </select>
    </div>
  );
}
