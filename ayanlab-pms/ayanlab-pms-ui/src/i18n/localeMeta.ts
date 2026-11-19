import type { Locale } from "./types";

export type LocaleOption = { code: Locale; label: string; native: string };

/** P4.10 — eleven interface languages */
export const LOCALE_OPTIONS: LocaleOption[] = [
  { code: "en", label: "English", native: "English" },
  { code: "zh", label: "Mandarin Chinese", native: "中文" },
  { code: "hi", label: "Hindi", native: "हिन्दी" },
  { code: "es", label: "Spanish", native: "Español" },
  { code: "fr", label: "French", native: "Français" },
  { code: "ar", label: "Arabic", native: "العربية" },
  { code: "bn", label: "Bengali", native: "বাংলা" },
  { code: "pt", label: "Portuguese", native: "Português" },
  { code: "ru", label: "Russian", native: "Русский" },
  { code: "tr", label: "Turkish", native: "Türkçe" },
  { code: "ur", label: "Urdu", native: "اردو" }
];

/** Shown in language `<select>` options (never ISO codes like EN). */
export function localeOptionLabel(o: LocaleOption): string {
  if (o.native === o.label) return o.label;
  return `${o.label} (${o.native})`;
}