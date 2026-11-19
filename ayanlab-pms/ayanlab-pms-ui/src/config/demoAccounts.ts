/** Demo login personas (mock + platform `ipms.json` users). Password for all: `password`. */
export type DemoLoginAccount = {
  email: string;
  label: string;
  hint: string;
  password: string;
};

export const DEMO_LOGIN_ACCOUNTS: DemoLoginAccount[] = [
  {
    email: "pm@demo.ayanlab",
    label: "Property Manager",
    hint: "Dashboard, rentals, HOA, TR modüller",
    password: "password"
  },
  {
    email: "accountant@demo.ayanlab",
    label: "Accountant",
    hint: "Accounting, bank, EFT, budget",
    password: "password"
  },
  {
    email: "leasing@demo.ayanlab",
    label: "Leasing Agent",
    hint: "Listings, leases, renewals, applicants",
    password: "password"
  },
  {
    email: "owner@demo.ayanlab",
    label: "Homeowner",
    hint: "Portal önizleme, aidat, architectural",
    password: "password"
  },
  {
    email: "field@demo.ayanlab",
    label: "Field staff",
    hint: "Tur kontrol, saha (mobil ile birlikte)",
    password: "password"
  }
];

/** Compact list for header persona switcher (mock only). */
export const DEMO_ACCOUNTS = DEMO_LOGIN_ACCOUNTS.map(({ email, label }) => ({ email, label }));