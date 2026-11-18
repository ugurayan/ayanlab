import type { DashboardWidget, WidgetId } from "../../types/dashboard";
import type { MainRoleId } from "./mainRoles";

const SYS = "4a3207bf-cc00-46db-8db7-42e8d9568b8b" as MainRoleId;
const STAFF = "a0fdb0b8-a079-4315-ae21-31e68cd80778" as MainRoleId;
const BIZ_ADMIN = "4b029d24-e658-4a65-8d76-ddbc2627a05c" as MainRoleId;
const BIZ_MEMBER = "055978b5-7e39-494d-9725-a0dca32dda6c" as MainRoleId;
const EMPLOYER = "b0633699-641c-47c5-89c6-6a88f7d83754" as MainRoleId;
const WORKER = "9c2b8be8-bdec-4484-96b6-a2145b654b4d" as MainRoleId;
const GUEST = "e0cd0911-c905-436e-b729-6e9be593f4dd" as MainRoleId;

const ADMIN_ROLES = [SYS, STAFF];
const ORG_ROLES = [BIZ_ADMIN, BIZ_MEMBER, EMPLOYER];

/** Full widget catalog: Admin, Organization, Worker widgets + extras */
export const WIDGET_CATALOG: DashboardWidget[] = [
  // ---------- Admin (System Admin) widgets ----------
  { id: "admin-orgs-registered", name: "Registered organizations", description: "# of registered organizations", roles: ADMIN_ROLES, defaultOrder: 1, defaultSize: { w: 1, h: 1 } },
  { id: "admin-employers-by-org", name: "Employers by organization", description: "# of employers based on organizations", roles: ADMIN_ROLES, defaultOrder: 2, defaultSize: { w: 1, h: 1 } },
  { id: "admin-employers-by-role", name: "Employers by job role", description: "# of employers based on job roles", roles: ADMIN_ROLES, defaultOrder: 3, defaultSize: { w: 1, h: 1 } },
  { id: "admin-users-logged-today", name: "Users logged in today", description: "# of users logged in today", roles: ADMIN_ROLES, defaultOrder: 4, defaultSize: { w: 1, h: 1 } },
  { id: "admin-jobs-created-today", name: "Jobs created today", description: "# of jobs created today, last week, last month", roles: ADMIN_ROLES, defaultOrder: 5, defaultSize: { w: 1, h: 1 } },
  { id: "admin-jobs-assigned-today", name: "Jobs assigned", description: "# of jobs assigned today, last week, last month", roles: ADMIN_ROLES, defaultOrder: 6, defaultSize: { w: 1, h: 1 } },
  { id: "admin-jobs-not-assigned", name: "Jobs not assigned", description: "# of jobs not assigned yet", roles: ADMIN_ROLES, defaultOrder: 7, defaultSize: { w: 1, h: 1 } },
  { id: "admin-jobs-created-chart", name: "Jobs created (last month)", description: "Jobs created for last 1 month – graph", roles: ADMIN_ROLES, defaultOrder: 8, defaultSize: { w: 2, h: 2 } },
  { id: "admin-users-registration-chart", name: "User registration (last month)", description: "Users registration daily – last month", roles: ADMIN_ROLES, defaultOrder: 9, defaultSize: { w: 2, h: 2 } },
  { id: "admin-transaction-daily-chart", name: "Total transactions (last month)", description: "Total transaction daily graph – last month", roles: ADMIN_ROLES, defaultOrder: 10, defaultSize: { w: 2, h: 2 } },
  { id: "admin-active-shifts", name: "Active shifts", description: "Published/draft shifts", roles: ADMIN_ROLES, defaultOrder: 11, defaultSize: { w: 1, h: 1 } },
  { id: "admin-open-applications", name: "Open applications", description: "Applications in review", roles: ADMIN_ROLES, defaultOrder: 12, defaultSize: { w: 1, h: 1 } },
  { id: "admin-pending-documents", name: "Pending documents", description: "Documents awaiting verification", roles: ADMIN_ROLES, defaultOrder: 13, defaultSize: { w: 1, h: 1 } },
  { id: "admin-system-health", name: "System health", description: "API and service status", roles: ADMIN_ROLES, defaultOrder: 14, defaultSize: { w: 1, h: 1 } },
  { id: "admin-recent-activity", name: "Recent activity", description: "Latest platform activity feed", roles: ADMIN_ROLES, defaultOrder: 15, defaultSize: { w: 2, h: 1 } },

  // ---------- Worker widgets ----------
  { id: "worker-open-jobs-count", name: "Open jobs (all profiles)", description: "# of open jobs for all my defined profiles", roles: [WORKER], defaultOrder: 100, defaultSize: { w: 1, h: 1 } },
  { id: "worker-jobs-list", name: "Jobs for current profile", description: "List of jobs (title, company) for current profile", roles: [WORKER], defaultOrder: 101, defaultSize: { w: 2, h: 2 } },
  { id: "worker-earnings", name: "My Earnings", description: "Totals, breakdown, date filters", roles: [WORKER], defaultOrder: 102, defaultSize: { w: 2, h: 2 } },
  { id: "worker-transaction-history-week", name: "Transaction history (last week)", description: "Transaction history of last week", roles: [WORKER], defaultOrder: 103, defaultSize: { w: 2, h: 2 } },
  { id: "worker-applied-jobs", name: "Applied jobs", description: "Jobs I have applied to", roles: [WORKER], defaultOrder: 104, defaultSize: { w: 1, h: 1 } },
  { id: "worker-assigned-jobs", name: "Assigned jobs", description: "Jobs I am assigned to", roles: [WORKER], defaultOrder: 105, defaultSize: { w: 1, h: 1 } },
  { id: "worker-rejected-jobs", name: "Rejected jobs", description: "Applications rejected", roles: [WORKER], defaultOrder: 106, defaultSize: { w: 1, h: 1 } },
  { id: "worker-upcoming-shifts", name: "Upcoming shifts", description: "Upcoming shifts as list", roles: [WORKER], defaultOrder: 107, defaultSize: { w: 2, h: 2 } },
  { id: "worker-last-messages", name: "Last 10 messages", description: "Last 10 messages as list", roles: [WORKER], defaultOrder: 108, defaultSize: { w: 2, h: 2 } },
  { id: "worker-shifts-completed", name: "Shifts completed", description: "Total shifts completed", roles: [WORKER], defaultOrder: 109, defaultSize: { w: 1, h: 1 } },
  { id: "worker-pending-payments", name: "Pending payments", description: "Payments awaiting processing", roles: [WORKER], defaultOrder: 110, defaultSize: { w: 1, h: 1 } },
  { id: "worker-certificates-expiring", name: "Certificates expiring", description: "Certificates expiring soon", roles: [WORKER], defaultOrder: 111, defaultSize: { w: 1, h: 1 } },
  { id: "worker-profile-strength", name: "Profile strength", description: "Profile completeness score", roles: [WORKER], defaultOrder: 112, defaultSize: { w: 1, h: 1 } },
  { id: "worker-saved-jobs", name: "Saved jobs", description: "Jobs I have saved", roles: [WORKER], defaultOrder: 113, defaultSize: { w: 1, h: 1 } },

  // ---------- Organization widgets (Business Admin, Member, Employer) ----------
  { id: "org-created-jobs-list", name: "Created jobs", description: "Created jobs as list", roles: ORG_ROLES, defaultOrder: 200, defaultSize: { w: 2, h: 2 } },
  { id: "org-last-shifts", name: "Last shifts", description: "Most recent shifts", roles: ORG_ROLES, defaultOrder: 201, defaultSize: { w: 2, h: 2 } },
  { id: "org-not-assigned-jobs", name: "Not assigned jobs", description: "Jobs not assigned yet – list", roles: ORG_ROLES, defaultOrder: 202, defaultSize: { w: 2, h: 2 } },
  { id: "org-transaction-list", name: "Transaction list", description: "Organization transactions", roles: ORG_ROLES, defaultOrder: 203, defaultSize: { w: 2, h: 2 } },
  { id: "org-active-workers", name: "Active workers", description: "Workers assigned to your org", roles: ORG_ROLES, defaultOrder: 204, defaultSize: { w: 1, h: 1 } },
  { id: "org-open-positions", name: "Open positions", description: "Jobs currently hiring", roles: ORG_ROLES, defaultOrder: 205, defaultSize: { w: 1, h: 1 } },
  { id: "org-invoices-due", name: "Invoices due", description: "Invoices due this month", roles: ORG_ROLES, defaultOrder: 206, defaultSize: { w: 1, h: 1 } },
  { id: "org-compliance-tasks", name: "Compliance tasks", description: "Documents waiting for approval", roles: ORG_ROLES, defaultOrder: 207, defaultSize: { w: 1, h: 1 } },
  { id: "org-pending-applications", name: "Pending applications", description: "Applications in review", roles: ORG_ROLES, defaultOrder: 208, defaultSize: { w: 1, h: 1 } },
  { id: "org-upcoming-shifts", name: "Upcoming shifts (org)", description: "Next shifts across locations", roles: ORG_ROLES, defaultOrder: 209, defaultSize: { w: 2, h: 1 } },
  { id: "org-total-spend-month", name: "Total spend (month)", description: "Total spend this month", roles: ORG_ROLES, defaultOrder: 210, defaultSize: { w: 1, h: 1 } },
  { id: "org-workers-on-shift", name: "Workers on shift today", description: "Workers currently on shift", roles: ORG_ROLES, defaultOrder: 211, defaultSize: { w: 1, h: 1 } },
  { id: "org-jobs-created-month", name: "Jobs created (month)", description: "Jobs created this month", roles: ORG_ROLES, defaultOrder: 212, defaultSize: { w: 1, h: 1 } },
  { id: "org-fill-rate", name: "Fill rate", description: "Shift fill rate percentage", roles: ORG_ROLES, defaultOrder: 213, defaultSize: { w: 1, h: 1 } },
  { id: "org-top-roles", name: "Top roles", description: "Most requested job roles", roles: ORG_ROLES, defaultOrder: 214, defaultSize: { w: 1, h: 1 } },
];

/** Default widget IDs per role (all possible widgets for that role; user can customize later) */
export const DEFAULT_LAYOUT_BY_ROLE: Record<string, WidgetId[]> = {
  [SYS]: WIDGET_CATALOG.filter((w) => w.roles.includes(SYS)).map((w) => w.id),
  [STAFF]: WIDGET_CATALOG.filter((w) => w.roles.some((r) => (ADMIN_ROLES as readonly string[]).includes(r))).map((w) => w.id),
  [BIZ_ADMIN]: WIDGET_CATALOG.filter((w) => w.roles.some((r) => (ORG_ROLES as readonly string[]).includes(r))).map((w) => w.id),
  [BIZ_MEMBER]: WIDGET_CATALOG.filter((w) => w.roles.includes(BIZ_MEMBER)).map((w) => w.id),
  [EMPLOYER]: WIDGET_CATALOG.filter((w) => w.roles.includes(EMPLOYER)).map((w) => w.id),
  [WORKER]: WIDGET_CATALOG.filter((w) => w.roles.includes(WORKER)).map((w) => w.id),
  [GUEST]: [],
};

export function getWidgetById(id: WidgetId): DashboardWidget | undefined {
  return WIDGET_CATALOG.find((w) => w.id === id);
}

export function getWidgetsForRole(roleId: string): DashboardWidget[] {
  return WIDGET_CATALOG.filter((w) => w.roles.includes(roleId)).sort(
    (a, b) => a.defaultOrder - b.defaultOrder
  );
}

export function getDefaultWidgetIdsForRole(roleId: string): WidgetId[] {
  const ids = DEFAULT_LAYOUT_BY_ROLE[roleId];
  if (ids && ids.length > 0) return ids;
  return getWidgetsForRole(roleId).map((w) => w.id);
}
