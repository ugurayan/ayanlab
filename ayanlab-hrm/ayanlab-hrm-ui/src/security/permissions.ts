import type { Permission, Role } from "../types/auth";

/**
 * StaffIQ permission codes (`staffiq:{resource}:{action}`), issued by the platform from
 * ayanlab-hrm-backend/src/main/resources/authz/*.json. Grants may use `*` for resource or action.
 */
export const P = {
  dashboardRead: "staffiq:dashboard:read",
  profileRead: "staffiq:profile:read",
  jobsRead: "staffiq:jobs:read",
  tasksRead: "staffiq:tasks:read",
  inboxRead: "staffiq:inbox:read",
  orgsRead: "staffiq:organizations:read",
  locationsRead: "staffiq:locations:read",
  shiftsRead: "staffiq:shifts:read",
  shiftsCreate: "staffiq:shifts:create",
  applicationsRead: "staffiq:applications:read",
  workersRead: "staffiq:workers:read",
  documentsRead: "staffiq:documents:read",
  financeRead: "staffiq:finance:read",
  transactionsRead: "staffiq:transactions:read",
  auditRead: "staffiq:audit:read",
  verificationRead: "staffiq:verification:read",
  supportRead: "staffiq:support:read",
  settingsRead: "staffiq:settings:read",
  jobRolesRead: "staffiq:job_roles:read",
  jobRolesCreate: "staffiq:job_roles:create",
  industriesRead: "staffiq:industries:read",
  skillsRead: "staffiq:skills:read",
  skillsCreate: "staffiq:skills:create",
  certificatesRead: "staffiq:certificates:read",
  certificatesCreate: "staffiq:certificates:create",
  documentTypesRead: "staffiq:document_types:read",
  documentTypesCreate: "staffiq:document_types:create",
  contractTypesRead: "staffiq:contract_types:read",
  contactTypesRead: "staffiq:contact_types:read",
  dressCodesRead: "staffiq:dress_codes:read",
  freeTimeWindowsRead: "staffiq:free_time_windows:read",
  expertiseLevelsRead: "staffiq:expertise_levels:read",
  jobDocumentsRead: "staffiq:job_documents:read",
  jobTemplatesRead: "staffiq:job_templates:read"
} as const;

/** Mirrors PermissionCodes.fromLegacy: app:module:resource:action → app:resource:action. */
function normalize(code: string): string {
  const parts = code.split(":");
  return parts.length === 4 ? `${parts[0]}:${parts[2]}:${parts[3]}` : code;
}

/** Mirrors PermissionCodes.implies: same app; resource and action equal or "*". */
export function implies(grant: string, required: string): boolean {
  const [gApp, gResource, gAction] = normalize(grant).split(":");
  const [rApp, rResource, rAction] = normalize(required).split(":");
  if (!gApp || !rApp || gApp !== rApp) return false;
  return (gResource === "*" || gResource === rResource) && (gAction === "*" || gAction === rAction);
}

export function hasPermission(grants: Iterable<string>, required: string): boolean {
  for (const grant of grants) {
    if (implies(grant, required)) return true;
  }
  return false;
}

const BASE = [
  "staffiq:dashboard:read",
  "staffiq:profile:read", "staffiq:profile:update",
  "staffiq:inbox:read", "staffiq:inbox:create",
  "staffiq:support:read", "staffiq:support:create",
  "staffiq:settings:read"
];
const TAXONOMY_READ = [
  "staffiq:job_roles:read", "staffiq:industries:read", "staffiq:skills:read",
  "staffiq:certificates:read", "staffiq:document_types:read",
  "staffiq:contract_types:read", "staffiq:contact_types:read", "staffiq:dress_codes:read",
  "staffiq:free_time_windows:read", "staffiq:expertise_levels:read",
  "staffiq:job_documents:read", "staffiq:job_templates:read"
];
const SHIFT_MANAGE = ["staffiq:shifts:*", "staffiq:jobs:*", "staffiq:tasks:*", "staffiq:job_templates:*"];
const DOCUMENT_REVIEW = [
  "staffiq:documents:read", "staffiq:documents:review",
  "staffiq:verification:read", "staffiq:verification:review"
];

/**
 * Mock mode only (VITE_USE_MOCKS): the real grants come from the platform login response.
 * Keep in sync with ayanlab-hrm-backend/src/main/resources/authz/roles.json.
 */
const ROLE_GRANTS: Record<string, Permission[]> = {
  PLATFORM_ADMIN: ["staffiq:*:*"],
  ADMIN: ["staffiq:*:*"],
  ORG_ADMIN: [
    ...BASE, "staffiq:organizations:*", "staffiq:locations:*", ...SHIFT_MANAGE, "staffiq:applications:*",
    "staffiq:workers:read", ...DOCUMENT_REVIEW, "staffiq:finance:read", "staffiq:transactions:read",
    "staffiq:audit:read", ...TAXONOMY_READ, "staffiq:subscriptions:*"
  ],
  ORG_FINANCE: [...BASE, "staffiq:finance:*", "staffiq:transactions:*", "staffiq:organizations:read", "staffiq:audit:read"],
  LOCATION_MANAGER: [
    ...BASE, "staffiq:locations:read", ...SHIFT_MANAGE, "staffiq:applications:*", "staffiq:workers:read",
    "staffiq:documents:read", ...TAXONOMY_READ
  ],
  RECRUITER: [...BASE, ...SHIFT_MANAGE, "staffiq:applications:*", "staffiq:workers:read", "staffiq:documents:read", ...TAXONOMY_READ],
  WORKER: [
    ...BASE, "staffiq:shifts:read", "staffiq:jobs:read",
    "staffiq:applications:read", "staffiq:applications:create", "staffiq:applications:update",
    "staffiq:documents:read", "staffiq:documents:create", ...TAXONOMY_READ
  ],
  AGENT_ADMIN: [
    ...BASE, "staffiq:organizations:*", ...SHIFT_MANAGE, "staffiq:applications:*", "staffiq:workers:read",
    "staffiq:documents:read", "staffiq:audit:read", ...TAXONOMY_READ
  ],
  AGENT_USER: [
    ...BASE, "staffiq:organizations:read", ...SHIFT_MANAGE, "staffiq:applications:*", "staffiq:workers:read",
    "staffiq:documents:read", ...TAXONOMY_READ
  ],
  VENDOR_USER: [...BASE, ...DOCUMENT_REVIEW],
  RIDER: [...BASE, "staffiq:rides:*", "staffiq:routes:read"],
  SYSTEM_ADMIN: BASE,
  STAFF: BASE,
  BUSINESS_ADMIN: BASE,
  BUSINESS_MEMBER: BASE,
  EMPLOYER: BASE,
  GUEST: ["staffiq:dashboard:read", "staffiq:jobs:read"]
};

/** Mock mode only; see {@link ROLE_GRANTS}. */
export function computePermissions(roles: Role[]): Permission[] {
  const set = new Set<Permission>();
  for (const r of roles) {
    for (const p of ROLE_GRANTS[r] ?? []) set.add(p);
  }
  return [...set];
}
