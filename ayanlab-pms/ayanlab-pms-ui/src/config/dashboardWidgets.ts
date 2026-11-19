import type { PmsPersona } from "../types/auth";

export type DashboardWidgetId =
  | "balances"
  | "tasks"
  | "maintenance"
  | "insurance"
  | "expiringLeases"
  | "listings"
  | "activity"
  | "violations"
  | "bank"
  | "patrol"
  | "billsEft";

export type DashboardWidgetDef = {
  id: DashboardWidgetId;
  titleKey: string;
  href: string;
  roles: PmsPersona[];
};

export const DASHBOARD_WIDGETS: DashboardWidgetDef[] = [
  {
    id: "balances",
    titleKey: "dashboard.widgets.balances",
    href: "/app/rentals/balances",
    roles: ["PROPERTY_MANAGER", "ACCOUNTANT", "LEASING_AGENT"]
  },
  {
    id: "tasks",
    titleKey: "dashboard.widgets.tasks",
    href: "/app/tasks",
    roles: ["PROPERTY_MANAGER", "LEASING_AGENT", "FIELD_STAFF"]
  },
  {
    id: "maintenance",
    titleKey: "dashboard.widgets.maintenance",
    href: "/app/maintenance/requests",
    roles: ["PROPERTY_MANAGER", "FIELD_STAFF", "LEASING_AGENT"]
  },
  {
    id: "insurance",
    titleKey: "dashboard.widgets.insurance",
    href: "/app/rentals/tenants",
    roles: ["PROPERTY_MANAGER", "LEASING_AGENT"]
  },
  {
    id: "expiringLeases",
    titleKey: "dashboard.widgets.expiringLeases",
    href: "/app/leasing/renewals",
    roles: ["PROPERTY_MANAGER", "LEASING_AGENT"]
  },
  {
    id: "listings",
    titleKey: "dashboard.widgets.listings",
    href: "/app/leasing/listings",
    roles: ["PROPERTY_MANAGER", "LEASING_AGENT"]
  },
  {
    id: "activity",
    titleKey: "dashboard.widgets.activity",
    href: "/app/dashboard",
    roles: ["PROPERTY_MANAGER", "ACCOUNTANT", "LEASING_AGENT", "HOME_OWNER", "FIELD_STAFF"]
  },
  {
    id: "violations",
    titleKey: "dashboard.widgets.violations",
    href: "/app/associations/violations",
    roles: ["PROPERTY_MANAGER", "HOME_OWNER"]
  },
  {
    id: "bank",
    titleKey: "dashboard.widgets.bank",
    href: "/app/accounting/bank",
    roles: ["PROPERTY_MANAGER", "ACCOUNTANT"]
  },
  {
    id: "billsEft",
    titleKey: "dashboard.widgets.billsEft",
    href: "/app/accounting/eft",
    roles: ["ACCOUNTANT", "PROPERTY_MANAGER"]
  },
  {
    id: "patrol",
    titleKey: "dashboard.widgets.patrol",
    href: "/app/tr/patrol",
    roles: ["FIELD_STAFF", "PROPERTY_MANAGER"]
  }
];

const DEFAULT_ORDER: Record<PmsPersona, DashboardWidgetId[]> = {
  PROPERTY_MANAGER: [
    "balances",
    "tasks",
    "maintenance",
    "insurance",
    "expiringLeases",
    "listings",
    "activity",
    "violations",
    "bank"
  ],
  ACCOUNTANT: ["bank", "billsEft", "balances", "violations", "activity", "tasks"],
  LEASING_AGENT: ["listings", "expiringLeases", "insurance", "balances", "tasks", "activity", "maintenance"],
  HOME_OWNER: ["violations", "activity", "balances"],
  FIELD_STAFF: ["maintenance", "tasks", "patrol", "activity"]
};

export function primaryPersona(roles: PmsPersona[]): PmsPersona {
  const order: PmsPersona[] = [
    "PROPERTY_MANAGER",
    "ACCOUNTANT",
    "LEASING_AGENT",
    "FIELD_STAFF",
    "HOME_OWNER"
  ];
  for (const r of order) {
    if (roles.includes(r)) return r;
  }
  return roles[0] ?? "PROPERTY_MANAGER";
}

export function widgetsForRole(role: PmsPersona): DashboardWidgetDef[] {
  const allowed = new Set(DASHBOARD_WIDGETS.filter((w) => w.roles.includes(role)).map((w) => w.id));
  const order = DEFAULT_ORDER[role].filter((id) => allowed.has(id));
  for (const w of DASHBOARD_WIDGETS) {
    if (w.roles.includes(role) && !order.includes(w.id)) order.push(w.id);
  }
  return order
    .map((id) => DASHBOARD_WIDGETS.find((w) => w.id === id))
    .filter((w): w is DashboardWidgetDef => !!w);
}

export function defaultWidgetIds(role: PmsPersona): DashboardWidgetId[] {
  return widgetsForRole(role).map((w) => w.id);
}