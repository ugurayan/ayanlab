export const SUPPORTED_LOCALES = [
  "en",
  "zh",
  "hi",
  "es",
  "fr",
  "ar",
  "bn",
  "pt",
  "ru",
  "tr",
  "ur"
] as const;

export type Locale = (typeof SUPPORTED_LOCALES)[number];

export type MessageTree = {
  [key: string]: string | MessageTree;
};

export function isLocale(value: string): value is Locale {
  return (SUPPORTED_LOCALES as readonly string[]).includes(value);
}