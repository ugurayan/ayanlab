/** Seed app ids from default-data.json — used on login before authenticated app list. */
export type PlatformApp = { code: string; name: string; id: string };

export const PLATFORM_APPS: PlatformApp[] = [
  { code: "core", name: "Platform Core", id: "1e7c675c-53e5-41cc-ae94-8f09244c8f91" },
  { code: "ipms", name: "Property (PMS)", id: "ad94b2d2-5022-4818-a785-55807ab139ba" },
  { code: "crm", name: "CRM", id: "5fd9fde7-e513-40fb-9622-9ce3e9699f89" },
  { code: "vault", name: "Vault", id: "0cd4d187-f53e-4ebc-94cd-a601ab467094" },
  { code: "staffiq", name: "HRM / StaffIQ", id: "f01ae844-85fd-48a0-90ed-8bdd7e0e48a6" },
];