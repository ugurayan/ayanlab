import type { Locale, MessageTree } from "./types";
import { SUPPORTED_LOCALES } from "./types";
import { deepMerge } from "./deepMerge";
import { en } from "./catalog/en";
import { tr } from "./catalog/tr";
import { LOCALE_PATCHES } from "./catalog/packs";

function buildMessages(): Record<Locale, MessageTree> {
  const out = {} as Record<Locale, MessageTree>;
  for (const code of SUPPORTED_LOCALES) {
    if (code === "en") out[code] = en;
    else if (code === "tr") out[code] = tr;
    else out[code] = deepMerge(en, LOCALE_PATCHES[code] ?? {});
  }
  return out;
}

export const MESSAGES = buildMessages();

function resolve(obj: MessageTree | string, path: string[]): string | undefined {
  let cur: MessageTree | string | undefined = obj;
  for (const key of path) {
    if (typeof cur !== "object" || cur === null) return undefined;
    cur = cur[key];
  }
  return typeof cur === "string" ? cur : undefined;
}

export function translate(locale: Locale, key: string): string {
  const v = resolve(MESSAGES[locale], key.split("."));
  if (v) return v;
  const fallback = resolve(MESSAGES.en, key.split("."));
  return fallback ?? key;
}