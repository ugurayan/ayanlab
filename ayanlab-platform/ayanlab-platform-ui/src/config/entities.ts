export type NavFolderId = "applications" | "profile" | "role" | "policyGroup" | "audit";

export type EntityResource = {
  id: string;
  label: string;
  path: string;
  group: "Auth & RBAC" | "Core business";
  navFolder?: NavFolderId;
  listQuery?: Record<string, string>;
  columns?: string[];
  permission?: string;
  profileScoped?: boolean;
  readOnly?: boolean;
  createFields?: string[];
};

export const NAV_FOLDERS: { id: NavFolderId; titleKey: string; pageId?: string; childIds: string[] }[] = [
  {
    id: "applications",
    titleKey: "nav.applications",
    pageId: "apps",
    childIds: [
      "application-users",
      "application-organizations",
      "application-permissions",
      "application-modules",
    ],
  },
  {
    id: "role",
    titleKey: "nav.roleSection",
    pageId: "roles",
    childIds: ["role-policy-groups"],
  },
  {
    id: "profile",
    titleKey: "nav.profileSection",
    pageId: "profiles",
    childIds: ["profile-permissions", "profile-roles", "profile-policy-groups"],
  },
  {
    id: "policyGroup",
    titleKey: "nav.policyGroupSection",
    pageId: "policy-groups",
    childIds: ["policy-group-permissions"],
  },
  {
    id: "audit",
    titleKey: "nav.audit",
    childIds: ["login-history", "log-errors"],
  },
];

export type AuthNavEntry = { type: "folder"; id: NavFolderId } | { type: "entity"; id: string };

export const AUTH_NAV_SEQUENCE: AuthNavEntry[] = [
  { type: "folder", id: "applications" },
  { type: "entity", id: "users" },
  { type: "entity", id: "user-types" },
  { type: "entity", id: "organizations" },
  { type: "entity", id: "modules" },
  { type: "entity", id: "permissions" },
  { type: "folder", id: "role" },
  { type: "folder", id: "profile" },
  { type: "folder", id: "policyGroup" },
  { type: "folder", id: "audit" },
];

export const ENTITY_RESOURCES: EntityResource[] = [
  { id: "apps", label: "Applications", path: "/auth/api/apps", group: "Auth & RBAC", columns: ["id", "code", "name", "description", "isActive", "createdAt", "updatedAt"], permission: "core:apps:read" },
  { id: "users", label: "Users", path: "/auth/api/users", group: "Auth & RBAC", columns: ["id", "firstName", "lastName", "email", "mobile", "isActive", "createdAt", "updatedAt"], createFields: ["email", "firstName", "lastName", "password", "appId"], permission: "core:users:read" },
  { id: "application-users", label: "Application Users", path: "/auth/api/application-users", group: "Auth & RBAC", navFolder: "applications", columns: ["id", "appId", "userId", "isActive", "createdAt", "updatedAt"], createFields: ["appId", "userId"] },
  { id: "profiles", label: "Profiles", path: "/auth/api/profiles", group: "Auth & RBAC", columns: ["id", "profileSlug", "profileName", "userId", "isActive", "createdAt", "updatedAt"], profileScoped: true, permission: "core:profiles:read" },
  { id: "organizations", label: "Organizations", path: "/auth/api/organizations", group: "Auth & RBAC", columns: ["id", "code", "name", "description", "isActive", "createdAt", "updatedAt"], permission: "core:organizations:read" },
  { id: "application-organizations", label: "Application Organizations", path: "/auth/api/application-organizations", group: "Auth & RBAC", navFolder: "applications", columns: ["id", "appId", "orgId", "isActive", "createdAt", "updatedAt"], createFields: ["appId", "orgId"] },
  { id: "roles", label: "Roles", path: "/auth/api/roles", group: "Auth & RBAC", columns: ["id", "appId", "organizationId", "code", "name", "description", "isActive", "createdAt", "updatedAt"], createFields: ["appId", "code", "name", "description"], permission: "core:roles:read" },
  { id: "permissions", label: "Permissions", path: "/auth/api/permissions", group: "Auth & RBAC", columns: ["id", "code", "name", "description", "appCode", "module", "resource", "action", "isActive", "createdAt", "updatedAt"], permission: "core:permissions:read" },
  { id: "modules", label: "Modules", path: "/auth/api/modules", group: "Auth & RBAC" },
  { id: "application-modules", label: "Application Modules", path: "/auth/api/application-modules", group: "Auth & RBAC", navFolder: "applications", columns: ["id", "appId", "moduleId", "isActive", "createdAt", "updatedAt"], createFields: ["appId", "moduleId"] },
  { id: "user-types", label: "User Types", path: "/auth/api/user-types", group: "Auth & RBAC" },
  { id: "departments", label: "Departments", path: "/auth/api/departments", group: "Auth & RBAC" },
  { id: "translations", label: "Translations", path: "/auth/api/translations", group: "Auth & RBAC" },
  { id: "application-permissions", label: "Application Permissions", path: "/auth/api/application-permissions", group: "Auth & RBAC", navFolder: "applications", columns: ["id", "appId", "permissionId", "isActive", "createdAt", "updatedAt"], createFields: ["appId", "permissionId"] },
  { id: "profile-roles", label: "Profile Roles", path: "/auth/api/profile-roles", group: "Auth & RBAC", navFolder: "profile" },
  { id: "profile-permissions", label: "Profile Permissions", path: "/auth/api/profile-permissions", group: "Auth & RBAC", navFolder: "profile" },
  { id: "policy-groups", label: "Policy Groups", path: "/auth/api/policy-groups", group: "Auth & RBAC" },
  { id: "policy-group-permissions", label: "Policy Group Permissions", path: "/auth/api/policy-group-permissions", group: "Auth & RBAC", navFolder: "policyGroup" },
  { id: "profile-policy-groups", label: "Profile Policy Groups", path: "/auth/api/profile-policy-groups", group: "Auth & RBAC", navFolder: "profile" },
  { id: "role-policy-groups", label: "Role Policy Group", path: "/auth/api/role-policy-groups", group: "Auth & RBAC", navFolder: "role" },
  { id: "login-history", label: "Login History", path: "/auth/api/login-history", group: "Auth & RBAC", navFolder: "audit", readOnly: true },
  { id: "log-errors", label: "Log Errors", path: "/auth/api/log-errors", group: "Auth & RBAC", navFolder: "audit", readOnly: true },
  { id: "subscription-plans", label: "Subscription plans", path: "/core/api/subscription-plans", group: "Core business" },
  { id: "subscription-plan-prices", label: "Plan prices", path: "/core/api/subscription-plan-prices", group: "Core business" },
  { id: "subscription-plan-features", label: "Plan features", path: "/core/api/subscription-plan-features", group: "Core business" },
  { id: "profile-subscriptions", label: "Profile subscriptions", path: "/core/api/profile-subscriptions", group: "Core business" },
  { id: "payments", label: "Payments", path: "/core/api/payments", group: "Core business" },
  { id: "invoices", label: "Invoices", path: "/core/api/invoices", group: "Core business" },
  { id: "invoice-line-items", label: "Invoice line items", path: "/core/api/invoice-line-items", group: "Core business" },
  { id: "transactions", label: "Transactions", path: "/core/api/transactions", group: "Core business" },
  { id: "bank-accounts", label: "Bank accounts", path: "/core/api/bank-accounts", group: "Core business" },
  { id: "bank-cards", label: "Bank cards", path: "/core/api/bank-cards", group: "Core business" },
  { id: "templates", label: "Notification templates", path: "/core/api/templates", group: "Core business" },
  // Email dispatch is POST-only (send-template); no list GET
  { id: "files", label: "Cloud files", path: "/core/api/files", group: "Core business" },
  { id: "profile-resource-scopes", label: "Profile resource scopes", path: "/core/api/profile-resource-scopes", group: "Core business" },
  { id: "approvals", label: "Approval requests", path: "/core/api/approvals/requests", group: "Core business" },
  {
    id: "auth-effective",
    label: "Authorization (current profile)",
    path: "/auth/api/authorization/effective",
    group: "Auth & RBAC",
    readOnly: true,
  },
];

export function entityById(id: string): EntityResource | undefined {
  return ENTITY_RESOURCES.find((e) => e.id === id);
}