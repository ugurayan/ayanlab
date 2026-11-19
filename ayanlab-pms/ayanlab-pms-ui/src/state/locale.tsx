import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import type { Locale } from "../i18n/types";
import { isLocale } from "../i18n/types";
import { translate } from "../i18n/messages";

const KEY = "pms.locale";

type LocaleCtx = {
  locale: Locale;
  setLocale: (l: Locale) => void;
  t: (key: string) => string;
};

const Ctx = createContext<LocaleCtx | undefined>(undefined);

function readInitial(): Locale {
  const stored = localStorage.getItem(KEY);
  if (stored && isLocale(stored)) return stored;
  const nav = navigator.language.toLowerCase();
  if (nav.startsWith("tr")) return "tr";
  if (nav.startsWith("zh")) return "zh";
  if (nav.startsWith("hi")) return "hi";
  if (nav.startsWith("es")) return "es";
  if (nav.startsWith("fr")) return "fr";
  if (nav.startsWith("ar")) return "ar";
  if (nav.startsWith("bn")) return "bn";
  if (nav.startsWith("pt")) return "pt";
  if (nav.startsWith("ru")) return "ru";
  if (nav.startsWith("ur")) return "ur";
  return "en";
}

export function LocaleProvider({ children }: { children: React.ReactNode }) {
  const [locale, setLocaleState] = useState<Locale>(readInitial);

  useEffect(() => {
    document.documentElement.lang = locale;
    document.documentElement.dir = locale === "ar" || locale === "ur" ? "rtl" : "ltr";
  }, [locale]);

  const setLocale = useCallback((l: Locale) => {
    localStorage.setItem(KEY, l);
    setLocaleState(l);
    document.documentElement.lang = l;
  }, []);

  const t = useCallback((key: string) => translate(locale, key), [locale]);

  const value = useMemo(() => ({ locale, setLocale, t }), [locale, setLocale, t]);

  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}

export function useLocale() {
  const ctx = useContext(Ctx);
  if (!ctx) throw new Error("useLocale outside LocaleProvider");
  return ctx;
}

export function useT() {
  return useLocale().t;
}