import type { LegalSlug } from "../../config/legalPages";

export type LegalSection = { heading: string; paragraphs: string[] };

export type LegalDocument = {
  title: string;
  updated: string;
  sections: LegalSection[];
};

export type LegalCatalog = Record<LegalSlug, LegalDocument>;
/** Full legal copy only in en/tr; other UI locales fall back in LegalDocPage */
export type LegalContentLocale = "en" | "tr";
export type LegalByLocale = Record<LegalContentLocale, LegalCatalog>;