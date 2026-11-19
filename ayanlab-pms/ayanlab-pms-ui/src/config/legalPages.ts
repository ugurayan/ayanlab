export const LEGAL_SLUGS = [
  "about",
  "help-faq",
  "contact",
  "feedback",
  "terms",
  "privacy",
  "cookies",
  "licenses",
  "kvkk",
  "distance-sales",
  "pre-information",
  "cancellation-refund",
  "account-deletion"
] as const;

export type LegalSlug = (typeof LEGAL_SLUGS)[number];

export const LEGAL_NAV: { slug: LegalSlug; labelKey: string }[] = [
  { slug: "about", labelKey: "nav.about" },
  { slug: "help-faq", labelKey: "nav.helpFaq" },
  { slug: "contact", labelKey: "nav.contact" },
  { slug: "feedback", labelKey: "nav.feedback" },
  { slug: "terms", labelKey: "nav.terms" },
  { slug: "privacy", labelKey: "nav.privacy" },
  { slug: "cookies", labelKey: "nav.cookies" },
  { slug: "licenses", labelKey: "nav.licenses" },
  { slug: "kvkk", labelKey: "nav.kvkk" },
  { slug: "distance-sales", labelKey: "nav.distanceSales" },
  { slug: "pre-information", labelKey: "nav.preInformation" },
  { slug: "cancellation-refund", labelKey: "nav.cancellation" },
  { slug: "account-deletion", labelKey: "nav.accountDeletion" }
];