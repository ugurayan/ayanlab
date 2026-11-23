import React, { createContext, useContext, useEffect, useState } from "react";
import { type AppLanguage, APP_LANGUAGE_CODES, isAppLanguage } from "../i18n/localeCatalog";

export type Language = AppLanguage;

type I18nContextValue = {
  language: Language;
  setLanguage: (lang: Language) => void;
};

const I18nContext = createContext<I18nContextValue | undefined>(undefined);

const STORAGE_KEY = "language";

function resolveInitialLanguage(): Language {
  if (typeof window === "undefined") {
    return "en";
  }
  const stored = window.localStorage.getItem(STORAGE_KEY);
  if (stored && isAppLanguage(stored)) {
    return stored;
  }
  return "en";
}

export function LanguageProvider({ children }: { children: React.ReactNode }) {
  const [language, setLanguage] = useState<Language>(() => resolveInitialLanguage());

  useEffect(() => {
    if (typeof window === "undefined") {
      return;
    }
    window.localStorage.setItem(STORAGE_KEY, language);
    document.documentElement.lang = language;
  }, [language]);

  return (
    <I18nContext.Provider value={{ language, setLanguage }}>
      {children}
    </I18nContext.Provider>
  );
}

export function useLanguage() {
  const ctx = useContext(I18nContext);
  if (!ctx) {
    throw new Error("useLanguage must be used within LanguageProvider");
  }
  return ctx;
}

/** Locales with full UI translation bundles (others fall back to English). */
export type UiTranslationLanguage = "en" | "de" | "tr";

export function resolveTranslationLanguage(language: Language): UiTranslationLanguage {
  if (language === "de" || language === "tr") return language;
  return "en";
}

/** UI copy bundles: only `en` / `de` / `tr` are translated; picker locales otherwise fall back to English. */
export function useTranslations<T>(variants: {
  en: T;
  de?: unknown;
  tr?: unknown;
}): T {
  const { language } = useLanguage();
  const key = resolveTranslationLanguage(language);
  if (key === "de" && variants.de !== undefined) return variants.de as T;
  if (key === "tr" && variants.tr !== undefined) return variants.tr as T;
  return variants.en;
}

export { APP_LANGUAGE_CODES };
